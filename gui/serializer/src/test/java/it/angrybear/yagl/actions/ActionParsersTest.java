package it.angrybear.yagl.actions;

import it.fulminazzo.yamlparser.configuration.FileConfiguration;
import it.fulminazzo.yamlparser.utils.FileUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ActionParsersTest {

    private static CommandAction[] getActions() {
        return new CommandAction[]{
                new BiGUICommand("command"),
                new GUIItemCommand("command2"),
                new GUICommand("command3"),
        };
    }

    @ParameterizedTest
    @MethodSource("getActions")
    void testCommandActions(CommandAction action) throws IOException {
        ActionParsers.addParsers();
        File file = new File("build/resources/test/command-action.yml");
        if (file.exists()) FileUtils.deleteFile(file);
        FileUtils.createNewFile(file);
        FileConfiguration configuration = new FileConfiguration(file);
        configuration.set("tmp", action);
        configuration.save();
        configuration = new FileConfiguration(file);
        CommandAction action1 = configuration.get("tmp", action.getClass());
        assertEquals(action, action1);
    }

    @Test
    void testCloseGUI() throws IOException {
        GUIItemAction expected = new GUIItemClose();
        ActionParsers.addParsers();
        File file = new File("build/resources/test/close-action.yml");
        if (file.exists()) FileUtils.deleteFile(file);
        FileUtils.createNewFile(file);
        FileConfiguration configuration = new FileConfiguration(file);
        configuration.set("tmp", expected);
        configuration.save();
        configuration = new FileConfiguration(file);
        GUIItemAction actual = configuration.get("tmp", expected.getClass());
        assertNotNull(actual);
        assertEquals(expected.getClass(), actual.getClass());
        assertNull(configuration.getString("tmp.content"), "Content should be empty");
    }
}