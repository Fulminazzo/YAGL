package it.angrybear.actions;

import it.angrybear.events.items.click.ClickItemEvent;
import it.angrybear.events.items.click.PreClickItemEvent;
import it.angrybear.events.items.interact.InteractItemEvent;
import it.angrybear.events.items.interact.PreInteractItemEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * A general interface for executing actions requiring an {@link ItemStack}.
 */
@SuppressWarnings("deprecation")
@FunctionalInterface
public interface BukkitItemAction {

    /**
     * Execute.
     *
     * @param player    the player
     * @param itemStack the item stack
     * @deprecated this method should be used only for defining the interface. This will NOT call the relative events. Use the static methods found in this interface instead.
     */
    @Deprecated
    void execute(final @NotNull Player player, final @NotNull ItemStack itemStack);

    /**
     * Run click item action.
     *
     * @param action    the action
     * @param player    the player
     * @param itemStack the item stack
     * @param clickType the click type
     */
    static void runClickItemAction(final @NotNull ClickItemAction action,
                                   final @NotNull Player player,
                                   final @NotNull ItemStack itemStack,
                                   final @NotNull ClickType clickType) {
        PreClickItemEvent preClickItemEvent = new PreClickItemEvent(player, itemStack, clickType);
        Bukkit.getPluginManager().callEvent(preClickItemEvent);
        if (preClickItemEvent.isCancelled()) return;
        action.execute(player, itemStack, clickType);
        ClickItemEvent clickItemEvent = new ClickItemEvent(player, itemStack, clickType);
        Bukkit.getPluginManager().callEvent(clickItemEvent);
    }

    /**
     * Run action item action.
     *
     * @param action     the action
     * @param player     the player
     * @param itemStack  the item stack
     * @param interactAction the interact action
     */
    static void runInteractItemAction(final @NotNull InteractItemAction action,
                                      final @NotNull Player player,
                                      final @NotNull ItemStack itemStack,
                                      final @NotNull Action interactAction) {
        PreInteractItemEvent preInteractItemEvent = new PreInteractItemEvent(player, itemStack, interactAction);
        Bukkit.getPluginManager().callEvent(preInteractItemEvent);
        if (preInteractItemEvent.isCancelled()) return;
        action.execute(player, itemStack, interactAction);
        InteractItemEvent interactItemEvent = new InteractItemEvent(player, itemStack, interactAction);
        Bukkit.getPluginManager().callEvent(interactItemEvent);
    }
}