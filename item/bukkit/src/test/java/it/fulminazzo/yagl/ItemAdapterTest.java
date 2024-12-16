package it.fulminazzo.yagl;

import it.fulminazzo.yagl.items.BukkitItem;
import it.fulminazzo.yagl.items.Item;
import it.fulminazzo.yagl.items.fields.ItemFlag;
import it.fulminazzo.yagl.items.recipes.FurnaceRecipe;
import it.fulminazzo.yagl.items.recipes.Recipe;
import it.fulminazzo.yagl.items.recipes.ShapedRecipe;
import it.fulminazzo.yagl.items.recipes.ShapelessRecipe;
import it.fulminazzo.fulmicollection.objects.Refl;
import it.fulminazzo.fulmicollection.utils.ReflectionUtils;
import it.fulminazzo.jbukkit.BukkitUtils;
import it.fulminazzo.jbukkit.annotations.After1_;
import it.fulminazzo.jbukkit.inventory.meta.MockItemMeta;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemAdapterTest extends BukkitUtils {

    @BeforeEach
    @Override
    protected void setUp() {
        super.setUp();
        setupServer();
    }

    @Test
    void testItem() {
        setupEnchantments();
        Item expected = Item.newItem("STONE").setAmount(2).setDurability(15)
                .setDisplayName("&7Cool stone").setLore("Click on this", "To be OP")
                .addEnchantment("flame", 10)
                .addEnchantment("infinity", 20)
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS)
                .setUnbreakable(true)
                .setCustomModelData(7);
        ItemStack itemStack = ItemAdapter.itemToItemStack(expected);

        assertEquals(expected, ItemAdapter.itemStackToItem(itemStack));
    }

    private ItemFactory mockItemFactory(ItemMeta meta) {
        ItemFactory itemFactory = mock(ItemFactory.class);
        when(itemFactory.getItemMeta(any(Material.class))).thenReturn(meta);
        return itemFactory;
    }

    @Nested
    @DisplayName("ItemStack to Item conversion")
    class ItemStackToItem {

        @Test
        void testNullItemStackShouldReturnNull() {
            assertNull(ItemAdapter.itemStackToItem(null));
        }

        @Test
        void testNullItemMeta() {
            when(Bukkit.getServer().getItemFactory()).thenAnswer(a -> mockItemFactory(null));
            assertDoesNotThrow(() -> ItemAdapter.itemStackToItem(new ItemStack(Material.STONE)));
        }

        @Test
        void testNullDisplayNameAndLore() {
            ItemMeta meta = mock(ItemMeta.class);
            when(meta.getDisplayName()).thenReturn(null);
            when(meta.getLore()).thenReturn(null);
            when(Bukkit.getServer().getItemFactory()).thenAnswer(a -> mockItemFactory(meta));
            assertDoesNotThrow(() -> ItemAdapter.itemStackToItem(new ItemStack(Material.STONE)));
        }

        @Test
        void testNullFieldsItem() {
            check();
            ItemStack itemStack = new ItemStack(Material.STONE);
            ItemMeta meta = new MockItemMeta();
            meta.setDisplayName(null);
            meta.setLore(null);
            try {
                meta.setCustomModelData(-1);
            } catch (NoSuchMethodError ignored) {}
            itemStack.setItemMeta(meta);
            assertEquals(BukkitItem.newItem(Material.STONE), ItemAdapter.itemStackToItem(itemStack));
        }

        @Test
        void testNullMetaItem() {
            ItemStack itemStack = new ItemStack(Material.STONE);
            itemStack.setItemMeta(null);
            assertEquals(BukkitItem.newItem(Material.STONE), ItemAdapter.itemStackToItem(itemStack));
        }

    }

    @Nested
    @DisplayName("Item to ItemStack conversion")
    class ItemToItemStack {

        @Test
        void testNullItemShouldReturnNull() {
            assertNull(ItemAdapter.itemToItemStack(null));
        }

        @Test
        void testItemWithNoMaterial() {
            assertThrowsExactly(IllegalArgumentException.class, () -> ItemAdapter.itemToItemStack(Item.newItem()));
        }

        @Test
        void testNotDamageableItemMeta() {
            when(Bukkit.getServer().getItemFactory()).thenAnswer(a -> mockItemFactory(new MockItemMeta()));
            assertDoesNotThrow(() -> ItemAdapter.itemToItemStack(BukkitItem.newItem(Material.STONE)));
        }

        @Test
        void testNullItemMeta() {
            when(Bukkit.getServer().getItemFactory()).thenAnswer(a -> mockItemFactory(null));
            assertDoesNotThrow(() -> ItemAdapter.itemToItemStack(BukkitItem.newItem(Material.STONE)));
        }

        @Test
        void testOlderItemMeta() {
            Damageable meta = mock(Damageable.class, withSettings().extraInterfaces(ItemMeta.class));
            doThrow(NoSuchMethodError.class).when(meta).setDamage(anyInt());
            when(Bukkit.getServer().getItemFactory()).thenAnswer(a -> mockItemFactory(meta));
            assertDoesNotThrow(() -> ItemAdapter.itemToItemStack(Item.newItem("stone")));
        }

    }

    @SuppressWarnings("deprecation")
    @Nested
    class RecipeConversion {

        @Test
        void testNullRecipe() throws InvocationTargetException, IllegalAccessException {
            final String methodName = "recipeToMinecraft";
            Refl<Class<ItemAdapter>> adapter = new Refl<>(ItemAdapter.class);
            @NotNull List<Method> methods = adapter.getMethods(m -> m.getName().equals(methodName));
            assertEquals(4, methods.size(), String.format("Could not find all '%s' methods", methodName));
            for (Method method : methods) {
                Object result = ReflectionUtils.setAccessibleOrThrow(method).invoke(ItemAdapter.class, (Object) null);
                assertNull(result);
            }
        }

        @Test
        @After1_(13)
        void testShapedRecipe() {
            check();
            org.bukkit.inventory.ShapedRecipe expected = new org.bukkit.inventory.ShapedRecipe(new org.bukkit.NamespacedKey("yagl", "test"),
                    new ItemStack(Material.STONE));
            expected.shape("ABC", "DEF");
            Material[] materials = new Material[]{Material.IRON_INGOT, Material.GOLD_INGOT, Material.REDSTONE,
                    Material.DIAMOND, Material.EMERALD, Material.LAPIS_LAZULI};
            for (int i = 0; i < materials.length; i++)
                expected.setIngredient((char) ('A' + i), new org.bukkit.inventory.RecipeChoice.ExactChoice(new ItemStack(materials[i])));
            expected.setIngredient((char) ('A' + 3), (org.bukkit.inventory.RecipeChoice) null);

            ShapedRecipe recipe = new ShapedRecipe("test")
                    .setOutput(Item.newItem("STONE")).setShape(2, 3);
            for (int i = 0; i < materials.length; i++) recipe.setIngredient(i, BukkitItem.newItem(materials[i]));
            recipe.setIngredient(3, null);

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
        @After1_(13)
        void testShapelessRecipe() {
            check();
            org.bukkit.inventory.ShapelessRecipe expected = new org.bukkit.inventory.ShapelessRecipe(new org.bukkit.NamespacedKey("yagl", "test"),
                    new ItemStack(Material.STONE));
            expected.addIngredient(new org.bukkit.inventory.RecipeChoice.ExactChoice(new ItemStack(Material.DIAMOND)));

            ShapelessRecipe recipe = new ShapelessRecipe("test")
                    .setOutput(Item.newItem("STONE")).addIngredient(Item.newItem("DIAMOND"));

            Refl<?> r1 = new Refl<>(expected);
            Refl<?> r2 = new Refl<>(ItemAdapter.recipeToMinecraft(recipe));

            for (Field field : r1.getNonStaticFields())
                assertEquals((Object) r1.getFieldObject(field), r2.getFieldObject(field));
        }

        @Test
        @After1_(13)
        void testFurnaceRecipe() {
            check();
            org.bukkit.inventory.FurnaceRecipe expected = new org.bukkit.inventory.FurnaceRecipe(new org.bukkit.NamespacedKey("yagl", "test"),
                    new ItemStack(Material.STONE), Material.COAL, 10, 20);
            new Refl<>(expected).setFieldObject("ingredient", new org.bukkit.inventory.RecipeChoice.ExactChoice(new ItemStack(Material.COAL)));

            FurnaceRecipe recipe = new FurnaceRecipe("test")
                    .setOutput(Item.newItem("STONE")).setIngredient(Item.newItem("COAL"))
                    .setExperience(10).setCookingTime(1);

            Refl<?> r1 = new Refl<>(expected);
            Refl<?> r2 = new Refl<>(ItemAdapter.recipeToMinecraft(recipe));

            for (Field field : r1.getNonStaticFields())
                assertEquals((Object) r1.getFieldObject(field), r2.getFieldObject(field));
        }

        @Test
        @After1_(13)
        void testResizeRecipe() {
            check();
            final String id = "test";
            final Material craftMaterial = Material.REDSTONE;
            final BukkitItem returnItem = BukkitItem.newItem(Material.REDSTONE_BLOCK);
            final int size = 4;

            org.bukkit.inventory.ShapedRecipe expected = new org.bukkit.inventory.ShapedRecipe(new org.bukkit.NamespacedKey("yagl", id),
                    returnItem.create());
            expected.shape("AB", "CD");
            for (int i = 0; i < size; i++) expected.setIngredient((char) ('A' + i), new org.bukkit.inventory.RecipeChoice.ExactChoice(new ItemStack(craftMaterial)));

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
}