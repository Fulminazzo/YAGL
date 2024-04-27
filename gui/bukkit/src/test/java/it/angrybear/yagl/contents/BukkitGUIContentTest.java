package it.angrybear.yagl.contents;

import it.angrybear.yagl.items.BukkitItem;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BukkitGUIContentTest {

    @Test
    void testCopyAndRenderReturnSelf() {
        GUIContent content = BukkitGUIContent.newInstance("stone", m -> {});
        int expected = content.hashCode();
        int actual = content.copy().render().copy(BukkitItem.class).hashCode();
        assertEquals(expected, actual);
    }

}
