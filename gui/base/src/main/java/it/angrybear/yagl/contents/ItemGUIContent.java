package it.angrybear.yagl.contents;

import it.angrybear.yagl.Metadatable;
import it.angrybear.yagl.actions.GUIItemAction;
import it.angrybear.yagl.contents.requirements.RequirementChecker;
import it.angrybear.yagl.items.Item;
import it.angrybear.yagl.items.fields.ItemField;
import it.angrybear.yagl.items.fields.ItemFlag;
import it.angrybear.yagl.utils.ObjectUtils;
import it.angrybear.yagl.wrappers.Enchantment;
import it.angrybear.yagl.wrappers.Sound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * A {@link GUIContent} that contains a single {@link Item}.
 * It also extends the interface to easily modify it without accessing it directly.
 */
public class ItemGUIContent extends GUIContentImpl implements GUIContent, Item {
    private final Item item;

    private ItemGUIContent() {
        this(Item.newItem());
    }

    ItemGUIContent(final @NotNull String material) {
        this(Item.newItem(material));
    }

    ItemGUIContent(final @NotNull Item item) {
        this.item = item.copy(Item.class);
    }

    @Override
    public @NotNull Item internalRender() {
        return this.item.copy();
    }

    @Override
    public ItemGUIContent setMaterial(@NotNull String material) {
        this.item.setMaterial(material);
        return this;
    }

    @Override
    public @Nullable String getMaterial() {
        return this.item.getMaterial();
    }

    @Override
    public ItemGUIContent setAmount(int amount) {
        this.item.setAmount(amount);
        return this;
    }

    @Override
    public int getAmount() {
        return this.item.getAmount();
    }

    @Override
    public ItemGUIContent setDurability(int durability) {
        this.item.setDurability(durability);
        return this;
    }

    @Override
    public int getDurability() {
        return this.item.getDurability();
    }

    @Override
    public ItemGUIContent setDisplayName(@NotNull String displayName) {
        this.item.setDisplayName(displayName);
        return this;
    }

    @Override
    public @NotNull String getDisplayName() {
        return this.item.getDisplayName();
    }

    @Override
    public @NotNull List<String> getLore() {
        return this.item.getLore();
    }

    @Override
    public Set<Enchantment> getEnchantments() {
        return this.item.getEnchantments();
    }

    @Override
    public Set<ItemFlag> getItemFlags() {
        return this.item.getItemFlags();
    }

    @Override
    public ItemGUIContent setUnbreakable(boolean unbreakable) {
        this.item.setUnbreakable(unbreakable);
        return this;
    }

    @Override
    public boolean isUnbreakable() {
        return this.item.isUnbreakable();
    }

    @Override
    public ItemGUIContent setCustomModelData(int customModelData) {
        this.item.setCustomModelData(customModelData);
        return this;
    }

    @Override
    public int getCustomModelData() {
        return this.item.getCustomModelData();
    }

    @Override
    public boolean isSimilar(@Nullable Item item, ItemField @NotNull ... ignore) {
        return this.item.isSimilar(item, ignore);
    }

    @Override
    public ItemGUIContent addLore(String @NotNull ... lore) {
        return (ItemGUIContent) Item.super.addLore(lore);
    }

    @Override
    public ItemGUIContent addLore(@NotNull Collection<String> lore) {
        return (ItemGUIContent) Item.super.addLore(lore);
    }

    @Override
    public ItemGUIContent removeLore(String @NotNull ... lore) {
        return (ItemGUIContent) Item.super.removeLore(lore);
    }

    @Override
    public ItemGUIContent removeLore(@NotNull Collection<String> lore) {
        return (ItemGUIContent) Item.super.removeLore(lore);
    }

    @Override
    public ItemGUIContent setLore(String @NotNull ... lore) {
        return (ItemGUIContent) Item.super.setLore(lore);
    }

    @Override
    public ItemGUIContent setLore(@NotNull Collection<String> lore) {
        return (ItemGUIContent) Item.super.setLore(lore);
    }

    @Override
    public ItemGUIContent addEnchantment(@NotNull String enchantment, int level) {
        return (ItemGUIContent) Item.super.addEnchantment(enchantment, level);
    }

    @Override
    public ItemGUIContent addEnchantments(String @NotNull ... enchantments) {
        return (ItemGUIContent) Item.super.addEnchantments(enchantments);
    }

    @Override
    public ItemGUIContent addEnchantments(Enchantment @NotNull ... enchantments) {
        return (ItemGUIContent) Item.super.addEnchantments(enchantments);
    }

    @Override
    public ItemGUIContent addEnchantments(@NotNull Collection<Enchantment> enchantments) {
        return (ItemGUIContent) Item.super.addEnchantments(enchantments);
    }

    @Override
    public ItemGUIContent removeEnchantment(@NotNull String enchantment, int level) {
        return (ItemGUIContent) Item.super.removeEnchantment(enchantment, level);
    }

    @Override
    public ItemGUIContent removeEnchantments(String @NotNull ... enchantments) {
        return (ItemGUIContent) Item.super.removeEnchantments(enchantments);
    }

    @Override
    public ItemGUIContent removeEnchantments(Enchantment @NotNull ... enchantments) {
        return (ItemGUIContent) Item.super.removeEnchantments(enchantments);
    }

    @Override
    public ItemGUIContent removeEnchantments(@NotNull Collection<Enchantment> enchantments) {
        return (ItemGUIContent) Item.super.removeEnchantments(enchantments);
    }

    @Override
    public ItemGUIContent addItemFlags(ItemFlag @NotNull ... itemFlags) {
        return (ItemGUIContent) Item.super.addItemFlags(itemFlags);
    }

    @Override
    public ItemGUIContent addItemFlags(@NotNull Collection<ItemFlag> itemFlags) {
        return (ItemGUIContent) Item.super.addItemFlags(itemFlags);
    }

    @Override
    public ItemGUIContent removeItemFlags(ItemFlag @NotNull ... itemFlags) {
        return (ItemGUIContent) Item.super.removeItemFlags(itemFlags);
    }

    @Override
    public ItemGUIContent removeItemFlags(@NotNull Collection<ItemFlag> itemFlags) {
        return (ItemGUIContent) Item.super.removeItemFlags(itemFlags);
    }

    public @NotNull ItemGUIContent copy() {
        return ObjectUtils.copy(this);
    }

    @Override
    public @NotNull ItemGUIContent setClickSound(Sound sound) {
        return (ItemGUIContent) super.setClickSound(sound);
    }

    @Override
    public @NotNull ItemGUIContent setPriority(int priority) {
        return (ItemGUIContent) super.setPriority(priority);
    }

    @Override
    public @NotNull ItemGUIContent setViewRequirements(@NotNull RequirementChecker requirements) {
        return (ItemGUIContent) super.setViewRequirements(requirements);
    }

    @Override
    public @NotNull ItemGUIContent onClickItemClose() {
        return (ItemGUIContent) super.onClickItemClose();
    }

    @Override
    public @NotNull ItemGUIContent onClickItem(@NotNull GUIItemAction action) {
        return (ItemGUIContent) super.onClickItem(action);
    }

    @Override
    public @NotNull ItemGUIContent setViewRequirements(@NotNull String permission) {
        return (ItemGUIContent) super.setViewRequirements(permission);
    }

    @Override
    public @NotNull ItemGUIContent onClickItem(@NotNull String command) {
        return (ItemGUIContent) super.onClickItem(command);
    }

    @Override
    public @NotNull ItemGUIContent copyFrom(@NotNull Metadatable other, boolean replace) {
        return (ItemGUIContent) super.copyFrom(other, replace);
    }

    @Override
    public @NotNull ItemGUIContent copyAll(@NotNull Metadatable other, boolean replace) {
        return (ItemGUIContent) super.copyAll(other, replace);
    }

    @Override
    public @NotNull ItemGUIContent unsetVariable(@NotNull String name) {
        return (ItemGUIContent) super.unsetVariable(name);
    }

    @Override
    public @NotNull ItemGUIContent setVariable(@NotNull String name, @NotNull String value) {
        return (ItemGUIContent) super.setVariable(name, value);
    }

    @Override
    public @NotNull String toString() {
        String output = super.toString();
        String item = this.item.toString();
        item = item.substring(1, item.length() - 1);
        return output.replace(String.format("\"item\": {%s}", item), item);
    }

    /**
     * Creates an instance of {@link ItemGUIContent}.
     *
     * @return the item gui content
     */
    public static ItemGUIContent newInstance() {
        return new ItemGUIContent();
    }

    /**
     * Creates an instance of {@link ItemGUIContent} with the given material.
     *
     * @param material the material
     * @return the item gui content
     */
    public static ItemGUIContent newInstance(final @NotNull String material) {
        return new ItemGUIContent(material);
    }

    /**
     * Creates an instance of {@link ItemGUIContent} with the given item.
     *
     * @param item the item
     * @return the item gui content
     */
    public static ItemGUIContent newInstance(final @NotNull Item item) {
        return new ItemGUIContent(item);
    }

}
