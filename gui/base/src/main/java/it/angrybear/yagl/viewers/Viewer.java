package it.angrybear.yagl.viewers;

import it.angrybear.yagl.guis.GUI;
import it.angrybear.yagl.wrappers.Sound;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * The viewer of a GUI, usually a player.
 */
@Getter
public abstract class Viewer {
    protected final UUID uniqueId;
    protected final String name;
    protected GUI previousGUI;
    protected GUI openGUI;

    /**
     * Instantiates a new Viewer.
     *
     * @param uniqueId the unique id
     * @param name     the name
     */
    protected Viewer(final UUID uniqueId, final String name) {
        this.uniqueId = uniqueId;
        this.name = name;
    }

    /**
     * Checks if the current viewer has a GUI opened.
     *
     * @return true if they do
     */
    public boolean hasOpenGUI() {
        return this.openGUI != null;
    }

    /**
     * Plays the given sound for the player.
     *
     * @param sound the sound
     */
    public abstract void playSound(final @NotNull Sound sound);

    /**
     * Sends the given message to the player.
     *
     * @param message the message
     */
    public abstract void sendMessage(final @NotNull String message);

    /**
     * Execute command.
     *
     * @param command the command
     */
    public abstract void executeCommand(final @NotNull String command);

    /**
     * Check if the viewer has the given permission
     *
     * @param permission the permission
     * @return true if they do
     */
    public abstract boolean hasPermission(final @NotNull String permission);

    /**
     * Closes the currently open GUI.
     */
    public abstract void closeGUI();
}
