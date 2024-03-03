package it.angrybear.items;

import it.angrybear.items.recipes.Recipe;
import it.angrybear.utils.ItemUtils;
import it.angrybear.utils.MaterialUtils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Iterator;

/**
 * An implementation of {@link RecipeItemImpl} that actually implements {@link #registerRecipe()} and {@link #unregisterRecipe()}.
 */
class BukkitRecipeItemImpl extends RecipeItemImpl implements BukkitRecipeItem {

    @Override
    public void registerRecipe() {
        if (this.recipe == null) return;
        org.bukkit.inventory.Recipe realRecipe = ItemUtils.recipeToMinecraft(this.recipe);
        Bukkit.addRecipe(realRecipe);
    }

    @Override
    public void unregisterRecipe() {
        Iterator<org.bukkit.inventory.Recipe> recipes = Bukkit.recipeIterator();
        while (recipes.hasNext()) {
            org.bukkit.inventory.Recipe r = recipes.next();
            if (r.getResult().isSimilar(create())) recipes.remove();
        }
    }

    @Override
    public BukkitRecipeItem setMaterial(@NotNull String material) {
        MaterialUtils.getMaterial(material, true);
        return (BukkitRecipeItem) super.setMaterial(material);
    }

    @Override
    public BukkitRecipeItem setAmount(final int amount) {
        return (BukkitRecipeItem) super.setAmount(amount);
    }

    @Override
    public BukkitRecipeItem setDurability(final int durability) {
        return (BukkitRecipeItem) super.setDurability(durability);
    }

    @Override
    public BukkitRecipeItem setDisplayName(final @NotNull String displayName) {
        return (BukkitRecipeItem) super.setDisplayName(displayName);
    }

    @Override
    public BukkitRecipeItem setLore(final @NotNull Collection<String> lore) {
        return (BukkitRecipeItem) super.setLore(lore);
    }

    @Override
    public BukkitRecipeItem setUnbreakable(final boolean unbreakable) {
        return (BukkitRecipeItem) super.setUnbreakable(unbreakable);
    }

    @Override
    public BukkitRecipeItem copy() {
        return super.copy(BukkitRecipeItemImpl.class);
    }

    @Override
    public BukkitRecipeItem setRecipe(@Nullable Recipe recipe) {
        return (BukkitRecipeItem) super.setRecipe(recipe);
    }
}