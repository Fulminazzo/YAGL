package it.angrybear.yagl.parsers;

import it.angrybear.yagl.items.Item;
import it.angrybear.yagl.items.fields.ItemFlag;
import it.fulminazzo.yamlparser.configuration.FileConfiguration;
import it.fulminazzo.yamlparser.utils.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ItemParserTest {

    @Test
    void testSaveAndLoad() throws IOException {
        ItemYAGLParser.addAllParsers();

        File output = new File("build/resources/test/item.yml");
        if (output.exists()) FileUtils.deleteFile(output);
        FileUtils.createNewFile(output);
        Item item = Item.newItem().setMaterial("STONE").setAmount(2).setDurability(15)
                .setDisplayName("&7Cool stone").setLore("Click on this", "To be OP")
                .addEnchantment("enchant1", 10).addEnchantment("enchant2", 20)
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS)
                .setUnbreakable(true)
                .setCustomModelData(7);
        FileConfiguration configuration = new FileConfiguration(output);
        configuration.set("item", item);
        configuration.save();

        configuration = new FileConfiguration(output);
        Item item2 = configuration.get("item", Item.class);
        assertEquals(item, item2);
    }

    @Test
    void testVariablesShouldBeSavedInLowerCase() throws IOException {
        ItemYAGLParser.addAllParsers();

        File output = new File("build/resources/test/item.yml");
        if (output.exists()) FileUtils.deleteFile(output);
        FileUtils.createNewFile(output);
        Item item = Item.newItem().setMaterial("STONE")
                .setDisplayName("&7Cool stone").setLore("Click on this", "To be OP")
                .addEnchantment("ENCHANT_1", 10).addEnchantment("ENCHANT_2", 20)
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS);
        FileConfiguration configuration = new FileConfiguration(output);
        configuration.set("item", item);
        item.setMaterial("stone").removeEnchantments("ENCHANT_1", "ENCHANT_2")
                        .addEnchantment("enchant_1", 10)
                        .addEnchantment("enchant_2", 20);
        configuration.save();

        configuration = new FileConfiguration(output);
        Item item2 = configuration.get("item", Item.class);
        assertNotNull(item2);

        assertEquals(item.getMaterial(), item2.getMaterial());
        assertEquals(item.getDisplayName(), item2.getDisplayName());
        assertIterableEquals(item.getLore(), item2.getLore());
        assertIterableEquals(item.getEnchantments(), item2.getEnchantments());
        assertIterableEquals(item.getItemFlags(), item2.getItemFlags());
    }
}