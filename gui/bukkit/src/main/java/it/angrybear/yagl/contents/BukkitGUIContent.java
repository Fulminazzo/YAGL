package it.angrybear.yagl.contents;

import it.angrybear.yagl.Metadatable;
import it.angrybear.yagl.actions.GUIItemAction;
import it.angrybear.yagl.contents.requirements.RequirementChecker;
import it.angrybear.yagl.exceptions.NotImplemented;
import it.angrybear.yagl.items.BukkitItem;
import it.angrybear.yagl.items.Item;
import it.angrybear.yagl.items.fields.ItemFlag;
import it.angrybear.yagl.utils.ObjectUtils;
import it.angrybear.yagl.wrappers.Enchantment;
import it.angrybear.yagl.wrappers.Sound;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public class BukkitGUIContent extends ItemGUIContent implements BukkitItem {
    private final Consumer<ItemMeta> metaFunction;

    BukkitGUIContent() {
        super();
        this.metaFunction = m -> {
            throw new NotImplemented();
        };
    }

    BukkitGUIContent(@NotNull String material, Consumer<ItemMeta> metaFunction) {
        super(material);
        this.metaFunction = metaFunction;
    }

    BukkitGUIContent(@NotNull Item item, Consumer<ItemMeta> metaFunction) {
        super(item);
        this.metaFunction = metaFunction;
    }

    @Override
    public @NotNull Item internalRender() {
        return this;
    }

    @Override
    public <I extends Item> I copy(@NotNull Class<I> clazz) {
        if (BukkitItem.class.isAssignableFrom(clazz)) return (I) this;
        return super.copy(clazz);
    }

    @Override
    public @NotNull <M extends ItemMeta> ItemStack create(@Nullable Class<M> itemMetaClass, @Nullable Consumer<M> metaFunction) {
        if (itemMetaClass == null) itemMetaClass = (Class<M>) ItemMeta.class;
        if (metaFunction == null) metaFunction = (Consumer<M>) this.metaFunction;
        return BukkitItem.super.create(itemMetaClass, metaFunction);
    }

    @Override
    public @NotNull BukkitGUIContent copy() {
        return ObjectUtils.copy(this);
    }

    @Override
    public BukkitGUIContent setMaterial(@NotNull String material) {
        return (BukkitGUIContent) super.setMaterial(material);
    }

    @Override
    public BukkitGUIContent setAmount(int amount) {
        return (BukkitGUIContent) super.setAmount(amount);
    }

    @Override
    public BukkitGUIContent setDurability(int durability) {
        return (BukkitGUIContent) super.setDurability(durability);
    }

    @Override
    public BukkitGUIContent setDisplayName(@NotNull String displayName) {
        return (BukkitGUIContent) super.setDisplayName(displayName);
    }

    @Override
    public BukkitGUIContent setUnbreakable(boolean unbreakable) {
        return (BukkitGUIContent) super.setUnbreakable(unbreakable);
    }

    @Override
    public BukkitGUIContent setCustomModelData(int customModelData) {
        return (BukkitGUIContent) super.setCustomModelData(customModelData);
    }

    @Override
    public BukkitGUIContent addLore(String @NotNull ... lore) {
        return (BukkitGUIContent) super.addLore(lore);
    }

    @Override
    public BukkitGUIContent addLore(@NotNull Collection<String> lore) {
        return (BukkitGUIContent) super.addLore(lore);
    }

    @Override
    public BukkitGUIContent removeLore(String @NotNull ... lore) {
        return (BukkitGUIContent) super.removeLore(lore);
    }

    @Override
    public BukkitGUIContent removeLore(@NotNull Collection<String> lore) {
        return (BukkitGUIContent) super.removeLore(lore);
    }

    @Override
    public BukkitGUIContent setLore(String @NotNull ... lore) {
        return (BukkitGUIContent) super.setLore(lore);
    }

    @Override
    public BukkitGUIContent setLore(@NotNull Collection<String> lore) {
        return (BukkitGUIContent) super.setLore(lore);
    }

    @Override
    public BukkitGUIContent addEnchantment(@NotNull String enchantment, int level) {
        return (BukkitGUIContent) super.addEnchantment(enchantment, level);
    }

    @Override
    public BukkitGUIContent addEnchantments(String @NotNull ... enchantments) {
        return (BukkitGUIContent) super.addEnchantments(enchantments);
    }

    @Override
    public BukkitGUIContent addEnchantments(Enchantment @NotNull ... enchantments) {
        return (BukkitGUIContent) super.addEnchantments(enchantments);
    }

    @Override
    public BukkitGUIContent addEnchantments(@NotNull Collection<Enchantment> enchantments) {
        return (BukkitGUIContent) super.addEnchantments(enchantments);
    }

    @Override
    public BukkitGUIContent removeEnchantment(@NotNull String enchantment, int level) {
        return (BukkitGUIContent) super.removeEnchantment(enchantment, level);
    }

    @Override
    public BukkitGUIContent removeEnchantments(String @NotNull ... enchantments) {
        return (BukkitGUIContent) super.removeEnchantments(enchantments);
    }

    @Override
    public BukkitGUIContent removeEnchantments(Enchantment @NotNull ... enchantments) {
        return (BukkitGUIContent) super.removeEnchantments(enchantments);
    }

    @Override
    public BukkitGUIContent removeEnchantments(@NotNull Collection<Enchantment> enchantments) {
        return (BukkitGUIContent) super.removeEnchantments(enchantments);
    }

    @Override
    public BukkitGUIContent addItemFlags(ItemFlag @NotNull ... itemFlags) {
        return (BukkitGUIContent) super.addItemFlags(itemFlags);
    }

    @Override
    public BukkitGUIContent addItemFlags(@NotNull Collection<ItemFlag> itemFlags) {
        return (BukkitGUIContent) super.addItemFlags(itemFlags);
    }

    @Override
    public BukkitGUIContent removeItemFlags(ItemFlag @NotNull ... itemFlags) {
        return (BukkitGUIContent) super.removeItemFlags(itemFlags);
    }

    @Override
    public BukkitGUIContent removeItemFlags(@NotNull Collection<ItemFlag> itemFlags) {
        return (BukkitGUIContent) super.removeItemFlags(itemFlags);
    }

    @Override
    public @NotNull BukkitGUIContent setClickSound(Sound sound) {
        return (BukkitGUIContent) super.setClickSound(sound);
    }

    @Override
    public @NotNull BukkitGUIContent setPriority(int priority) {
        return (BukkitGUIContent) super.setPriority(priority);
    }

    @Override
    public @NotNull BukkitGUIContent setViewRequirements(@NotNull RequirementChecker requirements) {
        return (BukkitGUIContent) super.setViewRequirements(requirements);
    }

    @Override
    public @NotNull BukkitGUIContent onClickItem(@NotNull GUIItemAction action) {
        return (BukkitGUIContent) super.onClickItem(action);
    }

    @Override
    public @NotNull BukkitGUIContent setViewRequirements(@NotNull String permission) {
        return (BukkitGUIContent) super.setViewRequirements(permission);
    }

    @Override
    public @NotNull BukkitGUIContent onClickItem(@NotNull String command) {
        return (BukkitGUIContent) super.onClickItem(command);
    }

    @Override
    public @NotNull BukkitGUIContent copyFrom(@NotNull Metadatable other, boolean replace) {
        return (BukkitGUIContent) super.copyFrom(other, replace);
    }

    @Override
    public @NotNull BukkitGUIContent copyAll(@NotNull Metadatable other, boolean replace) {
        return (BukkitGUIContent) super.copyAll(other, replace);
    }

    @Override
    public @NotNull BukkitGUIContent unsetVariable(@NotNull String name) {
        return (BukkitGUIContent) super.unsetVariable(name);
    }

    @Override
    public @NotNull BukkitGUIContent setVariable(@NotNull String name, @NotNull String value) {
        return (BukkitGUIContent) super.setVariable(name, value);
    }

    @Override
    public BukkitGUIContent setMaterial(@NotNull Material material) {
        return (BukkitGUIContent) BukkitItem.super.setMaterial(material);
    }

    public static BukkitGUIContent newInstance(final @NotNull String material,
                                               final @NotNull Consumer<ItemMeta> metaFunction) {
        return new BukkitGUIContent(material, metaFunction);
    }

    public static BukkitGUIContent newInstance(final @NotNull Item item,
                                               final @NotNull Consumer<ItemMeta> metaFunction) {
        return new BukkitGUIContent(item, metaFunction);
    }
}
