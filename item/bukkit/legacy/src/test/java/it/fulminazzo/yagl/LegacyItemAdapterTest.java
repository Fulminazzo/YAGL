package it.fulminazzo.yagl;

import it.fulminazzo.yagl.items.BukkitItem;
import it.fulminazzo.yagl.items.Item;
import it.fulminazzo.yagl.items.fields.ItemFlag;
import it.fulminazzo.yagl.items.recipes.FurnaceRecipe;
import it.fulminazzo.yagl.items.recipes.Recipe;
import it.fulminazzo.yagl.items.recipes.ShapedRecipe;
import it.fulminazzo.yagl.items.recipes.ShapelessRecipe;
import it.fulminazzo.fulmicollection.objects.Refl;
import it.fulminazzo.jbukkit.BukkitUtils;
import it.fulminazzo.jbukkit.annotations.Before1_;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@Before1_(13.2)
class LegacyItemAdapterTest extends BukkitUtils {

    @BeforeEach
    @Override
    protected void setUp() {
        super.setUp();
        BukkitUtils.setupServer();
        BukkitUtils.setupEnchantments();
    }

    @Test
    void testItemConversion() {
        Item expected = Item.newItem("STONE").setAmount(2).setDurability(15)
                .setDisplayName("&7Cool stone").setLore("Click on this", "To be OP")
                .addEnchantment("INFINITY", 20)
                .addEnchantment("FLAME", 10)
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS)
                .setUnbreakable(true);
        ItemStack itemStack = ItemAdapter.itemToItemStack(expected);

        assertEquals(expected, ItemAdapter.itemStackToItem(itemStack));
    }

    @Test
    void testShapedRecipeConversion() {
        org.bukkit.inventory.ShapedRecipe expected = new org.bukkit.inventory.ShapedRecipe(new ItemStack(Material.STONE));
        expected.shape("ABC", "DEF");
        Material[] materials = new Material[]{Material.IRON_INGOT, Material.GOLD_INGOT, Material.REDSTONE,
                Material.DIAMOND, Material.EMERALD, Material.INK_SACK};
        for (int i = 0; i < materials.length; i++)
            expected.setIngredient((char) ('A' + i), new ItemStack(materials[i]).getData());

        ShapedRecipe recipe = new ShapedRecipe("test")
                .setOutput(Item.newItem("STONE")).setShape(2, 3);
        for (int i = 0; i < materials.length; i++) recipe.setIngredient(i, BukkitItem.newItem(materials[i]));

        Refl<?> r1 = new Refl<>(expected);
        Refl<?> r2 = new Refl<>(ItemAdapter.recipeToMinecraft(recipe));

        for (Field field : r1.getNonStaticFields()) {
            Object obj1 = r1.getFieldObject(field);
            Object obj2 = r2.getFieldObject(field);
            if (obj1 != null && obj1.getClass().isArray())
                assertArrayEquals((Object[]) obj1, (Object[]) obj2);
            else assertEquals((Object) r1.getFieldObject(field), r2.getFieldObject(field));
        }
    }

    @Test
    void testShapelessRecipeConversion() {
        org.bukkit.inventory.ShapelessRecipe expected = new org.bukkit.inventory.ShapelessRecipe(new ItemStack(Material.STONE));
        expected.addIngredient(new ItemStack(Material.GRASS).getData());

        ShapelessRecipe recipe = new ShapelessRecipe("test")
                .setOutput(Item.newItem("STONE")).addIngredient(Item.newItem("GRASS"));

        Refl<?> r1 = new Refl<>(expected);
        Refl<?> r2 = new Refl<>(ItemAdapter.recipeToMinecraft(recipe));

        for (Field field : r1.getNonStaticFields())
            assertEquals((Object) r1.getFieldObject(field), r2.getFieldObject(field));
    }

    @Test
    void testFurnaceRecipeConversion() {
        org.bukkit.inventory.FurnaceRecipe expected = new org.bukkit.inventory.FurnaceRecipe(new ItemStack(Material.STONE), Material.COAL);
        new Refl<>(expected).setFieldObject("ingredient", new ItemStack(Material.COAL));

        FurnaceRecipe recipe = new FurnaceRecipe("test")
                .setOutput(Item.newItem("STONE")).setIngredient(Item.newItem("COAL"))
                .setExperience(10).setCookingTime(1);

        Refl<?> r1 = new Refl<>(expected);
        Refl<?> r2 = new Refl<>(ItemAdapter.recipeToMinecraft(recipe));

        for (Field field : r1.getNonStaticFields())
            assertEquals((Object) r1.getFieldObject(field), r2.getFieldObject(field));
    }

    @Test
    void testResizeRecipe() {
        final String id = "test";
        final Material craftMaterial = Material.REDSTONE;
        final BukkitItem returnItem = BukkitItem.newItem(Material.REDSTONE_BLOCK);
        final int size = 4;

        org.bukkit.inventory.ShapedRecipe expected = new org.bukkit.inventory.ShapedRecipe(returnItem.create());
        expected.shape("AB", "CD");
        for (int i = 0; i < size; i++) expected.setIngredient((char) ('A' + i), new ItemStack(craftMaterial).getData());

        ShapedRecipe recipe = new ShapedRecipe(id)
                .setOutput(returnItem).setShape(3, 3);
        for (int i = 0; i < 9; i++) recipe.setIngredient(i, BukkitItem.newItem(Material.COBBLESTONE));

        recipe.setShape(size / 2, size / 2);
        for (int i = 0; i < size; i++) recipe.setIngredient(i, BukkitItem.newItem(craftMaterial));

        assertEquals(size, recipe.size(), "Invalid recipe size");
        for (Item item : recipe)
            assertEquals(craftMaterial.name(), item.getMaterial(), String.format("Expected material %s", craftMaterial.name()));

        Refl<?> r1 = new Refl<>(expected);
        Refl<?> r2 = new Refl<>(ItemAdapter.recipeToMinecraft(recipe));

        for (Field field : r1.getNonStaticFields()) {
            Object obj1 = r1.getFieldObject(field);
            Object obj2 = r2.getFieldObject(field);
            if (obj1 != null && obj1.getClass().isArray())
                assertArrayEquals((Object[]) obj1, (Object[]) obj2);
            else assertEquals((Object) r1.getFieldObject(field), r2.getFieldObject(field));
        }
    }

    @Test
    void testInvalidRecipeType() {
        assertThrowsExactly(IllegalArgumentException.class, () -> ItemAdapter.recipeToMinecraft(mock(Recipe.class)));
    }
}