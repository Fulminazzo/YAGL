package it.angrybear.yagl.particles;

import it.fulminazzo.fulmicollection.interfaces.functions.BiFunctionException;
import it.fulminazzo.fulmicollection.interfaces.functions.TriConsumer;
import it.fulminazzo.yamlparser.configuration.IConfiguration;
import it.fulminazzo.yamlparser.parsers.YAMLParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A parser to serialize {@link BlockDataOption}.
 * Should take priority over {@link ParticleOptionParser}.
 */
public class BlockDataOptionParser extends YAMLParser<BlockDataOption> {

    /**
     * Instantiates a new Block data option parser.
     */
    public BlockDataOptionParser() {
        super(BlockDataOption.class);
    }

    @Override
    protected BiFunctionException<@NotNull IConfiguration, @NotNull String, @Nullable BlockDataOption> getLoader() {
        return (c, s) -> {
            String raw = c.getString(s);
            if (raw == null) return null;
            return new BlockDataOption(raw);
        };
    }

    @Override
    protected TriConsumer<@NotNull IConfiguration, @NotNull String, @Nullable BlockDataOption> getDumper() {
        return (c, s, b) -> {
            c.set(s, null);
            if (b == null) return;
            c.set(s, b.getOption());
        };
    }
}
