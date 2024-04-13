package it.angrybear.yagl.guis;

import it.angrybear.yagl.Metadatable;
import it.angrybear.yagl.TestUtils;
import it.angrybear.yagl.items.Item;
import it.angrybear.yagl.viewers.Viewer;
import it.fulminazzo.fulmicollection.objects.Refl;
import it.fulminazzo.fulmicollection.utils.ReflectionUtils;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class PageableGUITest {

    @Test
    void testPageableGUIMethods() throws InvocationTargetException, IllegalAccessException {
        GUI expected = setupGUI(GUI.newGUI(27));
        GUI actual = setupGUI(PageableGUI.newGUI(27));

        for (Method method : GUI.class.getDeclaredMethods()) {
            try {
                if (method.equals(GUI.class.getDeclaredMethod("open", Viewer.class))) continue;
                Metadatable.class.getDeclaredMethod(method.getName(), method.getParameterTypes());
                continue;
            } catch (NoSuchMethodException ignored) {}
            method = ReflectionUtils.setAccessible(method);
            Object[] params = Arrays.stream(method.getParameterTypes())
                    .map(TestUtils::mockParameter)
                    .map(o -> o instanceof Integer ? 9 : o)
                    .toArray(Object[]::new);
            Object obj1 = method.invoke(expected, params);
            Object obj2 = method.invoke(actual, params);
            if (obj1 != null && obj1.getClass().isArray())
                assertArrayEquals((Object[]) obj1, (Object[]) obj2);
            else if (!(obj1 instanceof GUI)) assertEquals(obj1, obj2);

            GUI template = new Refl<>(actual).getFieldObject("templateGUI");
            assertEquals(expected, template);
        }
    }

    @Test
    void testGetGUIPageException() {
        assertThrowsExactly(IndexOutOfBoundsException.class, () -> PageableGUI.newGUI(1).getPage(1));
    }

    private GUI setupGUI(GUI gui) {
        return gui.setTitle("hello world")
                .setMovable(3, true)
                .setMovable(4, false)
                .addContent(Item.newItem().setMaterial("stone"))
                .setContents(3, Item.newItem().setMaterial("diamond"))
                .setContents(4, Item.newItem().setMaterial("diamond"))
                .unsetContent(3)
                .onCloseGUI("command")
                .onClickOutside("command")
                .onOpenGUI("command")
                .onChangeGUI("command");
    }
}