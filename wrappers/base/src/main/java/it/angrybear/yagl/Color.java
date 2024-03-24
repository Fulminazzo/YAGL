package it.angrybear.yagl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;

/**
 * The type Color.
 */
public class Color extends ClassEnum {
    /**
     * The constant WHITE.
     */
    public static final Color WHITE = fromARGB("#FFFFFFFF");
    /**
     * The constant SILVER.
     */
    public static final Color SILVER = fromARGB("#FFC0C0C0");
    /**
     * The constant GRAY.
     */
    public static final Color GRAY = fromARGB("#FF808080");
    /**
     * The constant BLACK.
     */
    public static final Color BLACK = fromARGB("#FF000000");
    /**
     * The constant RED.
     */
    public static final Color RED = fromARGB("#FFFF0000");
    /**
     * The constant MAROON.
     */
    public static final Color MAROON = fromARGB("#FF800000");
    /**
     * The constant YELLOW.
     */
    public static final Color YELLOW = fromARGB("#FFFFFF00");
    /**
     * The constant OLIVE.
     */
    public static final Color OLIVE = fromARGB("#FF808000");
    /**
     * The constant LIME.
     */
    public static final Color LIME = fromARGB("#FF00FF00");
    /**
     * The constant GREEN.
     */
    public static final Color GREEN = fromARGB("#FF008000");
    /**
     * The constant AQUA.
     */
    public static final Color AQUA = fromARGB("#FF00FFFF");
    /**
     * The constant TEAL.
     */
    public static final Color TEAL = fromARGB("#FF008080");
    /**
     * The constant BLUE.
     */
    public static final Color BLUE = fromARGB("#FF0000FF");
    /**
     * The constant NAVY.
     */
    public static final Color NAVY = fromARGB("#FF000080");
    /**
     * The constant FUCHSIA.
     */
    public static final Color FUCHSIA = fromARGB("#FFFF00FF");
    /**
     * The constant PURPLE.
     */
    public static final Color PURPLE = fromARGB("#FF800080");
    /**
     * The constant ORANGE.
     */
    public static final Color ORANGE = fromARGB("#FFFFA500");
    private static final int MAX = 255;

    private final byte alpha;
    private final byte red;
    private final byte green;
    private final byte blue;

    /**
     * Instantiates a new Color.
     *
     * @param red   the red
     * @param green the green
     * @param blue  the blue
     */
    public Color(int red, int green, int blue) {
        this(MAX, red, green, blue);
    }

    /**
     * Instantiates a new Color.
     *
     * @param alpha the alpha
     * @param red   the red
     * @param green the green
     * @param blue  the blue
     */
    public Color(int alpha, int red, int green, int blue) {
        checkRange(alpha);
        this.alpha = (byte) alpha;
        checkRange(red);
        this.red = (byte) red;
        checkRange(green);
        this.green = (byte) green;
        checkRange(blue);
        this.blue = (byte) blue;
    }

    private void checkRange(int n) {
        if (n < 0 || n > MAX)
            throw new IllegalArgumentException(String.format("'%s' is not contained between 0 and %s",
                    n, MAX));
    }

    /**
     * Converts this color to an RGB string.
     *
     * @return the string
     */
    public String toRGB() {
        return "#" + toARGB().substring(3);
    }

    /**
     * Converts this color to an ARGB string.
     *
     * @return the string
     */
    public String toARGB() {
        return String.format("#%02X%02X%02X%02X", this.alpha, this.red, this.green, this.blue).toUpperCase();
    }

    /**
     * Converts the given ARGB string to a {@link Color}.
     * An RGB string is also accepted (the alpha value will be set to {@link #MAX}).
     *
     * @param argb the string
     * @return the color
     */
    public static Color fromARGB(String argb) {
        if (argb.startsWith("#")) argb = argb.substring(1);
        if (argb.length() != 8 && argb.length() != 6)
            throw new IllegalArgumentException(String.format("Invalid ARGB string provided '%s'", argb));
        LinkedList<Integer> nums = new LinkedList<>();
        while (!argb.isEmpty()) {
            int n = Integer.parseInt(argb.substring(0, 2), 16);
            argb = argb.substring(2);
            nums.add(n);
        }
        if (nums.size() < 4) nums.addFirst(MAX);
        return new Color(nums.get(0), nums.get(1), nums.get(2), nums.get(3));
    }

    /**
     * Gets the name from the corresponding static color.
     * If this is not a static color, it returns null.
     *
     * @return the name
     */
    @Override
    public @Nullable String name() {
        try {
            return super.name();
        } catch (IllegalStateException e) {
            return null;
        }
    }

    /**
     * Gets the corresponding static color
     *
     * @param color the color name
     * @return the color
     */
    public static Color valueOf(final @NotNull String color) {
        return valueOf(color, Color.class);
    }

    /**
     * Gets all the static colors.
     *
     * @return the colors
     */
    public static Color[] values() {
        return values(Color.class);
    }
}
