package it.angrybear.yagl.particles;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An option used by {@link ParticleType#BLOCK_CRACK}, {@link ParticleType#BLOCK_DUST} and {@link ParticleType#FALLING_DUST}.
 */
public class BlockDataOption extends ParticleOption<String> {
    private static final String NBT_REGEX = "^([^\\[]*)(?:\\[(.*)])?$";
    @Getter
    private final @NotNull String material;
    private final @NotNull String nbt;

    /**
     * Instantiates a new Block data option.
     *
     * @param blockData the raw block data
     */
    public BlockDataOption(final @NotNull String blockData) {
        String[] parsed = parseRaw(blockData, NBT_REGEX);
        this.material = parsed[0];
        this.nbt = parsed[1];
    }

    /**
     * Instantiates a new Block data option.
     *
     * @param material the material
     * @param nbt      the nbt
     */
    public BlockDataOption(final @NotNull String material, final @NotNull String nbt) {
        this.material = parseMaterial(material);
        this.nbt = nbt;
    }

    /**
     * Gets nbt.
     *
     * @return the nbt
     */
    public String getNBT() {
        return this.nbt;
    }

    /**
     * Converts the given data to a pair based on the given regex.
     * Throws an {@link IllegalArgumentException} in case of failure.
     *
     * @param data the data
     * @return the pair
     */
    static String @NotNull [] parseRaw(final @NotNull String data, final @NotNull String nbtRegex) {
        Matcher matcher = Pattern.compile(nbtRegex).matcher(data);
        if (matcher.matches()) {
            String nbt = matcher.group(2);
            if (nbt == null) nbt = "";
            return new String[]{parseMaterial(matcher.group(1)), nbt.trim()};
        } else throw new IllegalArgumentException(String.format("Invalid data '%s'", data));
    }

    /**
     * Parses the given raw material by removing any white-space or <i>minecraft:</i> prepending the actual material.
     * Throws an {@link IllegalArgumentException} in case the material is invalid.
     *
     * @param material the material
     * @return the string
     */
    static @NotNull String parseMaterial(@NotNull String material) {
        material = material.toLowerCase();
        if (material.startsWith("minecraft:")) material = material.substring("minecraft:".length());
        if (material.trim().isEmpty() || Pattern.compile("[\r\n\t :]").matcher(material).find())
            throw new IllegalArgumentException(String.format("Invalid material '%s'", material));
        return material;
    }

    @Override
    public String getOption() {
        String output = this.material;
        if (!this.nbt.isEmpty()) output += String.format("[%s]", this.nbt);
        return output;
    }
}
