package it.fulminazzo.yagl;

import it.fulminazzo.yagl.particles.*;
import it.fulminazzo.yagl.wrappers.Potion;
import it.fulminazzo.yagl.wrappers.PotionEffect;
import it.fulminazzo.fulmicollection.objects.Refl;
import it.fulminazzo.jbukkit.BukkitUtils;
import it.fulminazzo.jbukkit.annotations.After1_;
import it.fulminazzo.jbukkit.annotations.Before1_;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("deprecation")
@Before1_(12.2)
@After1_(9)
public class LegacyWrappersAdapterTest extends BukkitUtils {

    @BeforeEach
    @Override
    protected void setUp() {
        super.setUp();
    }

    private static Color[] getColors() {
        return Color.values();
    }

    private static Particle[] getTestLegacyParticles() {
        List<Particle> particles = new ArrayList<>();
        for (LegacyParticleType<?> type : LegacyParticleType.legacyValues()) particles.add(type.create());
        particles.add(LegacyParticleType.VILLAGER_PLANT_GROW.create(new PrimitiveParticleOption<>(10)));
        particles.add(LegacyParticleType.SMOKE.create(new PrimitiveParticleOption<>(BlockFace.NORTH.name())));
        particles.add(LegacyParticleType.ITEM_BREAK.create(new PrimitiveParticleOption<>(Material.STONE.name())));
        particles.add(LegacyParticleType.TILE_BREAK.create(new MaterialDataOption(Material.STONE.name())));
        particles.add(LegacyParticleType.TILE_DUST.create(new MaterialDataOption(Material.STONE.name(), 10)));
        particles.add(LegacyParticleType.POTION_BREAK.create(new PotionParticleOption(new Potion(PotionType.JUMP.name()))));
        return particles.toArray(new Particle[0]);
    }

    @ParameterizedTest
    @MethodSource("getTestLegacyParticles")
    void testSpawnEffect(Particle particle) {
        Player player = mock(Player.class);

        TestUtils.testMultipleMethods(WrappersAdapter.class, m -> m.getName().equals("spawnEffect") && m.getParameterTypes()[0].equals(Player.class),
                a -> {
                    ArgumentCaptor<?> arg = a[1];
                    Object value = arg.getValue();
                    assertInstanceOf(Effect.class, value);
                    assertEquals(particle.getType(), ((Effect) value).name());
                    if (particle.getOption() != null) assertNotNull(a[a.length - 1].getValue());
                }, new Object[]{player, particle}, player, "playEffect", Location.class, Effect.class, Object.class);
    }

    @Test
    void testWPotionEffectToPotionEffect() {
        String name = "INCREASE_DAMAGE";
        PotionEffectType type = mock(PotionEffectType.class);
        when(type.getName()).thenReturn(name);
        Map<String, PotionEffectType> map = new Refl<>(PotionEffectType.class).getFieldObject("byName");
        map.put(name.toLowerCase(), type);
        PotionEffect potionEffect = new PotionEffect(type.getName(), 10, 1, false, true);
        assertEquals(new org.bukkit.potion.PotionEffect(type, (int) (10 * Constants.TICKS_IN_SECOND), 0, false, false),
                WrappersAdapter.wPotionEffectToPotionEffect(potionEffect));
    }

    @Test
    void testPotionEffectToWPotionEffect() {
        String name = "INCREASE_DAMAGE";
        PotionEffectType type = mock(PotionEffectType.class);
        when(type.getName()).thenReturn(name);
        Map<String, PotionEffectType> map = new Refl<>(PotionEffectType.class).getFieldObject("byName");
        map.put(name.toLowerCase(), type);
        org.bukkit.potion.PotionEffect potionEffect = new org.bukkit.potion.PotionEffect(type, (int) (10 * Constants.TICKS_IN_SECOND), 0, false, false);
        assertEquals(new PotionEffect(type.getName(), 10, 1, false, true),
                WrappersAdapter.potionEffectToWPotionEffect(potionEffect));
    }

    @Test
    void testPotionConversion() {
        Potion expected = new Potion(PotionType.JUMP.name(), 1, true, false);
        org.bukkit.potion.Potion potion = WrappersAdapter.wPotionToPotion(expected);
        assertEquals(expected, WrappersAdapter.potionToWPotion(potion));
    }

    @ParameterizedTest
    @MethodSource("getColors")
    void testColorConversion(Color expected) {
        org.bukkit.Color color = WrappersAdapter.wColorToColor(expected);
        assertEquals(expected, WrappersAdapter.colorToWColor(color));
    }
}
