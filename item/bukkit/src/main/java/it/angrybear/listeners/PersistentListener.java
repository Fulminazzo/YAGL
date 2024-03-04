package it.angrybear.listeners;

import it.angrybear.items.PersistentItem;
import it.angrybear.persistent.DeathAction;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A listener for {@link PersistentItem}s.
 */
public class PersistentListener implements Listener {
    private static boolean INITIALIZED = false;

    /**
     * Instantiates a new Persistent listener.
     */
    public PersistentListener() {
        INITIALIZED = true;
    }

    @EventHandler
    void on(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Map<Integer, PersistentItem> toRestore = new HashMap<>();
        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            int finalI = i;
            final ItemStack item = contents[i];
            findPersistentItem(item, p -> {
                if (p.getDeathAction() == DeathAction.MAINTAIN) toRestore.put(finalI, p);
                event.getDrops().remove(item);
            });
        }
        if (toRestore.isEmpty()) return;
        new Thread(() -> {
            try {
                Thread.sleep(50);
                toRestore.forEach((i, p) -> player.getInventory().setItem(i, p.create()));
            } catch (InterruptedException ignored) {
            }
        }).start();
    }

    @EventHandler
    void on(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        interactPersistentItem(event.getItem(), cancelled(event), player);
    }

    @EventHandler
    void on(PlayerItemConsumeEvent event) {
        findPersistentItem(event.getItem(), cancelled(event));
    }

    @EventHandler
    void on(PlayerItemDamageEvent event) {
        findPersistentItem(event.getItem(), cancelled(event));
    }

    @EventHandler
    void on(BlockPlaceEvent event) {
        PlayerInventory inventory = event.getPlayer().getInventory();
        findPersistentItem(inventory.getItem(inventory.getHeldItemSlot()), cancelled(event));
    }

    @EventHandler
    void on(PlayerDropItemEvent event) {
        findPersistentItem(event.getItemDrop().getItemStack(), cancelled(event));
    }

    @EventHandler
    void on(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ClickType type = event.getClick();
        if (clickPersistentItem(event.getCurrentItem(), player, type, cancelled(event))) return;
        clickPersistentItem(event.getCursor(), player, type, cancelled(event));
    }

    @EventHandler
    void on(InventoryDragEvent event) {
        Player player = (Player) event.getWhoClicked();
        ClickType type = ClickType.LEFT;
        if (clickPersistentItem(event.getCursor(), player, type, cancelled(event))) return;
        if (clickPersistentItem(event.getOldCursor(), player, type, cancelled(event))) return;
        Collection<ItemStack> items = event.getNewItems().values();
        for (ItemStack i : items)
            if (clickPersistentItem(i, player, type, p -> cancelled(event).accept(p))) return;
    }

    private Consumer<PersistentItem> cancelled(Cancellable event) {
        return p -> event.setCancelled(true);
    }

    private boolean interactPersistentItem(final @Nullable ItemStack itemStack,
                                           final @NotNull Player player,
                                           final @NotNull Action interactAction,
                                           final @Nullable Consumer<PersistentItem> ifPresent) {
        return findPersistentItem(itemStack, p -> {
            if (itemStack != null) p.interact(player, itemStack, interactAction);
            if (ifPresent != null) ifPresent.accept(p);
        });
    }

    private boolean clickPersistentItem(final @Nullable ItemStack itemStack,
                                        final @NotNull Player player,
                                        final @NotNull ClickType clickType,
                                        final @Nullable Consumer<PersistentItem> ifPresent) {
        return findPersistentItem(itemStack, p -> {
            if (itemStack != null) p.click(player, itemStack, clickType);
            if (ifPresent != null) ifPresent.accept(p);
        });
    }

    private boolean findPersistentItem(final @Nullable ItemStack itemStack,
                                       final @NotNull Consumer<PersistentItem> ifPresent) {
        if (itemStack != null) {
            PersistentItem persistentItem = PersistentItem.getPersistentItem(itemStack);
            if (persistentItem != null) {
                ifPresent.accept(persistentItem);
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the current listener has been initialized at least once.
     *
     * @return the boolean
     */
    public static boolean isInitialized() {
        return INITIALIZED;
    }
}
