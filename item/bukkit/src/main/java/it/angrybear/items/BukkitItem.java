package it.angrybear.items;

import it.angrybear.utils.MaterialUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * An implementation of {@link Item} for Bukkit.
 */
public class BukkitItem extends ItemImpl {

    @Override
    public BukkitItem setMaterial(@NotNull String material) {
        MaterialUtils.getMaterial(material, true);
        return (BukkitItem) super.setMaterial(material);
    }

    @Override
    public BukkitItem setAmount(final int amount) {
        return (BukkitItem) super.setAmount(amount);
    }

    @Override
    public BukkitItem setDurability(final short durability) {
        return (BukkitItem) super.setDurability(durability);
    }

    @Override
    public BukkitItem setDisplayName(final @NotNull String displayName) {
        return (BukkitItem) super.setDisplayName(displayName);
    }

    @Override
    public BukkitItem setLore(final @NotNull Collection<String> lore) {
        return (BukkitItem) super.setLore(lore);
    }

    @Override
    public BukkitItem setUnbreakable(final boolean unbreakable) {
        return (BukkitItem) super.setUnbreakable(unbreakable);
    }
}
