package it.angrybear.yagl.contents;

import it.angrybear.yagl.actions.GUIItemAction;
import it.angrybear.yagl.actions.GUIItemCommand;
import it.angrybear.yagl.items.Item;
import it.angrybear.yagl.viewers.Viewer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Represents the generic content of a GUI.
 */
public interface GUIContent {

    /**
     * Renders the current content in an Item.
     * This will NOT check for {@link #hasViewRequirements(Viewer)} or {@link #getPriority()}.
     *
     * @return the item
     */
    @NotNull Item render();

    /**
     * Sets priority.
     * If two {@link GUIContent} share the same slot,
     * only the one with higher priority will be shown.
     *
     * @param priority the priority
     * @return this content
     */
    @NotNull GUIContent setPriority(final int priority);

    /**
     * Gets priority.
     *
     * @return the priority
     */
    int getPriority();

    /**
     * Sets the sound played upon clicking on the content in the GUI.
     * This is NOT checked until the sound is actually played.
     *
     * @param rawSound the raw sound
     * @return this content
     */
    @NotNull GUIContent setClickSound(final String rawSound);

    /**
     * Gets the sound played upon clicking on this content in the GUI.
     *
     * @return the sound, if set
     */
    @Nullable String getClickSound();

    /**
     * Allows to set a {@link PermissionRequirementChecker} with the given permission as the view requirement.
     *
     * @param permission the permission
     * @return this content
     */
    default @NotNull GUIContent setViewRequirements(final @NotNull String permission) {
        return setViewRequirements(new PermissionRequirementChecker(permission));
    }

    /**
     * Sets the given predicate as a checker for {@link #hasViewRequirements(Viewer)}.
     *
     * @param requirements the requirements
     * @return this content
     */
    @NotNull GUIContent setViewRequirements(final @NotNull RequirementChecker requirements);

    /**
     * Check if the given {@link Viewer} has enough requirements to view this content.
     *
     * @param viewer the viewer
     * @return the boolean
     */
    boolean hasViewRequirements(final @NotNull Viewer viewer);

    /**
     * Forces the {@link Viewer} to execute the given command when clicking on this GUIContent in a GUI.
     *
     * @param command the command
     * @return this content
     */
    default @NotNull GUIContent onClickItem(final @NotNull String command) {
        return onClickItem(new GUIItemCommand(command));
    }

    /**
     * Executes the given action when clicking on this GUIContent in a GUI.
     *
     * @param action the action
     * @return this content
     */
    @NotNull GUIContent onClickItem(final @NotNull GUIItemAction action);

    /**
     * Click item action.
     *
     * @return the action
     */
    @NotNull Optional<GUIItemAction> clickItemAction();

    /**
     * Copy this content to another one.
     *
     * @return the copy
     */
    @NotNull GUIContent copyContent();
}
