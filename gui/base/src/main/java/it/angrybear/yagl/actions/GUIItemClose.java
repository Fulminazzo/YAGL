package it.angrybear.yagl.actions;

import it.angrybear.yagl.contents.GUIContent;
import it.angrybear.yagl.guis.GUI;
import it.angrybear.yagl.viewers.Viewer;
import org.jetbrains.annotations.NotNull;

/**
 * An implementation of {@link GUIItemAction} that closes the open {@link GUI} for the {@link Viewer}.
 */
public class GUIItemClose implements GUIItemAction {

    @Override
    public void execute(@NotNull Viewer viewer, @NotNull GUI gui, @NotNull GUIContent content) {
        viewer.closeGUI();
    }

    @Override
    public String serialize() {
        return "";
    }
}
