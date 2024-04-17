package it.angrybear.yagl.guis;


/**
 * Represents a "chest" GUI that cannot be resized.
 */
class DefaultGUI extends GUIImpl {

    private DefaultGUI() {
        this(0);
    }

    /**
     * Instantiates a new Gui.
     *
     * @param size the size
     */
    DefaultGUI(int size) {
        super(size);
        checkSize(size);
    }

    /**
     * Checks if the given size is in bounds.
     *
     * @param size  the size
     */
    void checkSize(int size) {
        if (size < 0 || size > MAX_SIZE) throw new IllegalArgumentException("GUIs size must be bound between 0 and 54!");
        if (size % 9 != 0)
            throw new IllegalArgumentException(String.format("%s is not a valid size. Only multiple of 9 can be accepted", size));
    }
}
