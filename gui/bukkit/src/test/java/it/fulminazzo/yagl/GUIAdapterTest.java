package it.fulminazzo.yagl;

import it.fulminazzo.fulmicollection.objects.Refl;
import it.fulminazzo.jbukkit.BukkitUtils;
import it.fulminazzo.yagl.actions.GUIAction;
import it.fulminazzo.yagl.contents.GUIContent;
import it.fulminazzo.yagl.contents.ItemGUIContent;
import it.fulminazzo.yagl.guis.GUI;
import it.fulminazzo.yagl.guis.GUIType;
import it.fulminazzo.yagl.items.Item;
import it.fulminazzo.yagl.utils.GUITestUtils;
import it.fulminazzo.yagl.viewers.PlayerOfflineException;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GUIAdapterTest {
    private Player player;
    private Inventory inventory;

    @BeforeEach
    void setUp() {
        BukkitUtils.setupServer();

        Server server = Bukkit.getServer();
        when(server.getPluginManager()).thenReturn(mock(PluginManager.class));
        when(server.isPrimaryThread()).thenReturn(true);

        this.player = BukkitUtils.addPlayer(UUID.randomUUID(), "Alex");
        when(this.player.isOnline()).thenReturn(true);
        when(this.player.openInventory(any(Inventory.class)))
                .thenAnswer(a -> {
                    this.inventory = a.getArgument(0);
                    return null;
                });
        when(this.player.getServer()).thenReturn(server);
    }

    @Test
    void testOpenInAsync() {
        Server server = Bukkit.getServer();
        BukkitScheduler scheduler = mock(BukkitScheduler.class);
        when(scheduler.runTask(any(), any(Runnable.class))).then(a -> {
            ((Runnable) a.getArguments()[1]).run();
            return null;
        });
        when(server.isPrimaryThread()).thenReturn(false);
        when(server.getScheduler()).thenReturn(scheduler);
        JavaPlugin plugin = mock(JavaPlugin.class);

        try (MockedStatic<JavaPlugin> ignored = mockStatic(JavaPlugin.class)) {
            when(JavaPlugin.getProvidingPlugin(any())).thenReturn(plugin);
            GUI gui = GUI.newGUI(GUIType.CHEST);
            GUIAdapter.openGUI(gui, GUIManager.getViewer(this.player));
        }

        verify(server.getScheduler()).runTask(eq(plugin), any(Runnable.class));
    }

    private static GUI[] guis() {
        return new GUI[]{
                GUI.newGUI(9)
                        .setTitle("Hello world")
                        .addContent(Item.newItem("stone").setDisplayName("test")),
                GUI.newGUI(9)
                        .setTitle(null)
                        .addContent(Item.newItem("stone").setDisplayName("test")),
                GUI.newGUI(GUIType.CHEST)
                        .setTitle("Hello world")
                        .addContent(Item.newItem("stone").setDisplayName("test")),
                GUI.newGUI(GUIType.CHEST)
                        .setTitle(null)
                        .addContent(Item.newItem("stone").setDisplayName("test")),
        };
    }

    @ParameterizedTest
    @MethodSource("guis")
    void testOpenGUI(GUI expected) {
        openGUI(expected);

        assertNotNull(this.inventory);
        assertEquals(expected.size(), this.inventory.getSize());
        assertEquals(expected.getTitle(), new Refl<>(this.inventory).getFieldObject("title"));

        for (int i = 0; i < expected.size(); i++) {
            @NotNull List<GUIContent> contents = expected.getContents(i);
            GUIContent guiContent = contents.isEmpty() ? null : contents.get(0);
            ItemStack actual = this.inventory.getItem(i);
            if (guiContent == null) assertNull(actual);
            else {
                Item item = new Refl<>(guiContent).getFieldObject("item");
                ItemStack exp = ItemAdapter.itemToItemStack(item);
                assertEquals(exp, actual);
            }
        }
    }

    @Test
    void testOpenGUIMeta() {
        GUI gui = GUI.newGUI(GUIType.CHEST)
                .setTitle(null)
                .addContent(Item.newItem(Material.PLAYER_HEAD.name()).setDisplayName("<player_name>"));
        GUITestUtils.mockPlugin(p -> GUIAdapter.openGUI(gui, GUIManager.getViewer(this.player), m -> m.setUnbreakable(true)));

        assertNotNull(this.inventory);

        ItemStack expected = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta meta = expected.getItemMeta();
        meta.setDisplayName(this.player.getName());
        meta.setUnbreakable(true);
        expected.setItemMeta(meta);

        assertEquals(expected, this.inventory.getItem(0));
    }

    @Test
    void testOpenGUIRequirements() {
        when(this.player.hasPermission(anyString())).thenReturn(false);

        GUI expected = GUI.newGUI(9)
                .setTitle("Hello world")
                .addContent(ItemGUIContent.newInstance("stone").setViewRequirements(v -> false),
                        ItemGUIContent.newInstance("diamond").setViewRequirements("permission"));
        openGUI(expected);

        assertNotNull(this.inventory);
        assertEquals(expected.size(), this.inventory.getSize());
        assertEquals(expected.getTitle(), new Refl<>(this.inventory).getFieldObject("title"));

        for (int i = 0; i < expected.size(); i++)
            assertNull(this.inventory.getItem(i));
    }

    @Test
    void testOpenGUIForOfflinePlayer() {
        BukkitUtils.removePlayer(this.player);
        assertThrowsExactly(PlayerOfflineException.class, () -> openGUI(GUI.newGUI(9)));
    }

    @Test
    void testCloseGUIForOfflinePlayer() {
        BukkitUtils.removePlayer(this.player);
        assertThrowsExactly(PlayerOfflineException.class, this::closeGUI);
    }

    @Test
    void testOpenGUIAction() {
        GUI gui = GUI.newGUI(9);
        AtomicBoolean executed = new AtomicBoolean(false);
        GUIAction openAction = (g, v) -> executed.set(true);
        gui.onOpenGUI(openAction);
        openGUI(gui);
        assertTrue(executed.get(), "openAction was not executed");
    }

    private void openGUI(GUI gui) {
        GUITestUtils.mockPlugin(p -> gui.open(GUIManager.getViewer(this.player)));
    }

    private void closeGUI() {
        GUITestUtils.mockPlugin(p -> GUIAdapter.closeGUI(GUIManager.getViewer(this.player)));
    }

}