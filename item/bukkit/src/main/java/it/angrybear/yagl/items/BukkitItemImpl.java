package it.angrybear.yagl.items;

import it.angrybear.yagl.utils.MaterialUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * An implementation of {@link BukkitItem}.
 */
class BukkitItemImpl extends ItemImpl implements BukkitItem {

    /**
     * Instantiates a new Bukkit item.
     */
    public BukkitItemImpl() {
        super();
    }

    /**
     * Instantiates a new Bukkit item.
     *
     * @param material the material
     */
    public BukkitItemImpl(String material) {
        super(material);
    }

    /**
     * Instantiates a new Bukkit item.
     *
     * @param material the material
     * @param amount   the amount
     */
    public BukkitItemImpl(String material, int amount) {
        super(material, amount);
    }

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
    public BukkitItem setDurability(final int durability) {
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

    @Override
    public BukkitItem copy() {
        return (BukkitItem) super.copy();
    }
}
