package it.angrybear.yagl.wrappers;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link Wrapper} class used to represent potions in older version of Minecraft.
 * Currently used only for {@link it.angrybear.yagl.particles.LegacyParticleType#POTION_BREAK}.
 */
@Getter
public class Potion extends Wrapper {
    private @NotNull String type;
    private int level;
    private boolean splash;
    private boolean extended;

    /**
     * Instantiates a new Potion.
     *
     * @param type the type
     */
    public Potion(final @NotNull String type) {
        this(type, 1);
    }

    /**
     * Instantiates a new Potion.
     *
     * @param type  the type
     * @param level the level
     */
    public Potion(final @NotNull String type, final int level) {
        this(type, level, false, false);
    }

    /**
     * Instantiates a new Potion.
     *
     * @param type   the type
     * @param level  the level
     * @param splash the splash
     */
    public Potion(final @NotNull String type, final int level, final boolean splash) {
        this(type, level, splash, false);
    }

    /**
     * Instantiates a new Potion.
     *
     * @param type     the type
     * @param level    the level
     * @param splash   the splash
     * @param extended the extended
     */
    public Potion(final @NotNull String type, final int level, final boolean splash, final boolean extended) {
        this.type = type;
        setLevel(level);
        this.splash = splash;
        this.extended = extended;
    }

    /**
     * Sets type.
     *
     * @param type the type
     * @return the type
     */
    public Potion setType(final @NotNull String type) {
        this.type = type;
        return this;
    }

    /**
     * Sets amplifier.
     *
     * @param level the amplifier
     * @return this potion
     */
    public Potion setLevel(final int level) {
        if (level < 1) throw new IllegalArgumentException("Amplifier cannot be lower than 1");
        this.level = level;
        return this;
    }

    /**
     * Gets amplifier.
     *
     * @return the amplifier
     */
    public int getLevel() {
        return this.level - 1;
    }

    /**
     * Sets splash.
     *
     * @return this potion
     */
    public Potion setSplash() {
        this.splash = true;
        return this;
    }

    /**
     * Unset splash potion.
     *
     * @return this potion
     */
    public Potion unsetSplash() {
        this.splash = false;
        return this;
    }

    /**
     * Sets extended.
     *
     * @return this potion
     */
    public Potion setExtended() {
        this.extended = true;
        return this;
    }

    /**
     * Unset extended potion.
     *
     * @return this potion
     */
    public Potion unsetExtended() {
        this.extended = false;
        return this;
    }

}