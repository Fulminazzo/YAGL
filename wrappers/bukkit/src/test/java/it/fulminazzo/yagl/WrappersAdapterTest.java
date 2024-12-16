package it.fulminazzo.yagl;

import it.fulminazzo.yagl.items.AbstractItem;
import it.fulminazzo.yagl.particles.*;
import it.fulminazzo.yagl.particles.Particle;
import it.fulminazzo.yagl.wrappers.Enchantment;
import it.fulminazzo.yagl.wrappers.PotionEffect;
import it.fulminazzo.yagl.wrappers.Sound;
import it.fulminazzo.fulmicollection.objects.Refl;
import it.fulminazzo.fulmicollection.structures.tuples.Tuple;
import it.fulminazzo.fulmicollection.utils.ReflectionUtils;
import it.fulminazzo.jbukkit.BukkitUtils;
import it.fulminazzo.jbukkit.annotations.After1_;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("deprecation")
@After1_(13)
class WrappersAdapterTest extends BukkitUtils {

    @BeforeEach
    @Override
    protected void setUp() {
        super.setUp();
    }

    private static Particle[] getTestLegacyParticles() {
        List<Particle> particles = new ArrayList<>();
        for (LegacyParticleType<?> type : LegacyParticleType.values()) particles.add(type.create());
        for (LegacyParticleType<?> type : LegacyParticleType.legacyValues())
            particles.removeIf(t -> t.getType().equalsIgnoreCase(type.name()));
        particles.add(LegacyParticleType.COMPOSTER_FILL_ATTEMPT.create(new PrimitiveParticleOption<>(true)));
        particles.add(LegacyParticleType.BONE_MEAL_USE.create(new PrimitiveParticleOption<>(1)));
        particles.add(LegacyParticleType.INSTANT_POTION_BREAK.create(new ColorParticleOption(Color.AQUA)));
        return particles.toArray(new Particle[0]);
    }

    @ParameterizedTest
    @MethodSource("getTestLegacyParticles")
    void testSpawnPlayerEffect(Particle particle) {
        Player player = mock(Player.class);

        testSpawnEffect(Player.class, player, player, particle);
    }

    @ParameterizedTest
    @MethodSource("getTestLegacyParticles")
    void testSpawnWorldEffect(Particle particle) {
        Player player = mock(Player.class);
        World world = mock(World.class);
        when(world.getPlayers()).thenReturn(Collections.singletonList(player));

        testSpawnEffect(World.class, world, player, particle);
    }

    private <T> void testSpawnEffect(Class<T> targetClass, T target, Object executor, Particle particle) {
        TestUtils.testMultipleMethods(WrappersAdapter.class, m -> m.getName().equals("spawnEffect") && m.getParameterTypes()[0].equals(targetClass),
                a -> {
                    ArgumentCaptor<?> arg = a[1];
                    Object value = arg.getValue();
                    assertInstanceOf(Effect.class, value);
                    assertEquals(particle.getType(), ((Effect) value).name());
                    if (particle.getOption() != null) assertNotNull(a[a.length - 1].getValue());
                }, new Object[]{target, particle}, executor, "playEffect", Location.class, Effect.class, Object.class);
    }

    private static Particle[] getTestParticles() {
        List<Particle> particles = new ArrayList<>();
        for (ParticleType<?> type : ParticleType.values()) particles.add(type.create());
        particles.add(ParticleType.SCULK_CHARGE.create(new PrimitiveParticleOption<>(10f)));
        particles.add(ParticleType.SHRIEK.create(new PrimitiveParticleOption<>(11)));
        particles.add(ParticleType.SHRIEK.create((PrimitiveParticleOption<Integer>) null));
        particles.add(ParticleType.REDSTONE.create(new DustParticleOption(Color.RED, 12f)));
        particles.add(ParticleType.DUST_COLOR_TRANSITION.create(new DustTransitionParticleOption(
                Color.RED, Color.BLUE, 12f)));
        particles.add(ParticleType.VIBRATION.create(new PrimitiveParticleOption<>(
                new Vibration(mock(Location.class), mock(Vibration.Destination.class), 10))));
        particles.add(ParticleType.ITEM_CRACK.create(new ItemParticleOption<>(mock(AbstractItem.class))));
        particles.add(ParticleType.BLOCK_CRACK.create(new BlockDataOption("oak_log", "axis=y")));
        particles.add(ParticleType.BLOCK_DUST.create(new BlockDataOption("oak_log", "axis=y")));
        particles.add(ParticleType.FALLING_DUST.create(new BlockDataOption("oak_log", "axis=y")));
        return particles.toArray(new Particle[0]);
    }

    @ParameterizedTest
    @MethodSource("getTestParticles")
    void testPlayerSpawnParticle(Particle particle) {
        testSpawnParticle(Player.class, particle);
    }

    @ParameterizedTest
    @MethodSource("getTestParticles")
    void testWorldSpawnParticle(Particle particle) {
        testSpawnParticle(World.class, particle);
    }

    private <T> void testSpawnParticle(Class<T> targetClass, Particle particle) {
        // Initialize Bukkit variables
        initializeBlockData();

        T target = mock(targetClass);

        final @NotNull Consumer<ArgumentCaptor<?>[]> captorsValidator;
        final Class<?> @NotNull [] invokedMethodParamTypes;

        if (particle.getOption() == null) {
            captorsValidator = a -> {
                Object value = a[0].getValue();
                assertInstanceOf(org.bukkit.Particle.class, value);
                assertEquals(particle.getType(), ((org.bukkit.Particle) value).name());
            };
            invokedMethodParamTypes = new Class[]{org.bukkit.Particle.class, Location.class, int.class,
                    double.class, double.class, double.class, double.class};
        } else {
            captorsValidator = a -> {
                Object value = a[0].getValue();
                assertInstanceOf(org.bukkit.Particle.class, value);
                assertEquals(particle.getType(), ((org.bukkit.Particle) value).name());
                assertNotNull(a[a.length - 1].getValue());
            };
            invokedMethodParamTypes = new Class[]{org.bukkit.Particle.class, Location.class, int.class,
                    double.class, double.class, double.class, double.class, Object.class};
        }

        TestUtils.testMultipleMethods(WrappersAdapter.class,
                m -> m.getName().equals("spawnParticle") && m.getParameterTypes()[0].isAssignableFrom(target.getClass()),
                captorsValidator, new Object[]{target, particle}, target, "spawnParticle", invokedMethodParamTypes);
    }

    @Test
    void testSpawnItemCrack() throws NoSuchMethodException {
        // Initialize Bukkit variables
        BukkitUtils.setupServer();

        Particle particle = ParticleType.ITEM_CRACK.create(mock(AbstractItem.class));
        Player player = mock(Player.class);

        ArgumentCaptor<?> @NotNull [] captors = TestUtils.testSingleMethod(WrappersAdapter.class,
                WrappersAdapter.class.getMethod("spawnParticle", Player.class, Particle.class, Location.class, int.class),
                new Object[]{player, particle}, player, "spawnParticle",
                org.bukkit.Particle.class, Location.class, int.class, double.class, double.class, double.class, double.class, Object.class);

        Object value = captors[0].getValue();
        assertInstanceOf(org.bukkit.Particle.class, value);
        assertEquals(particle.getType(), ((org.bukkit.Particle) value).name());

        ArgumentCaptor<?> extra = captors[captors.length - 1];
        ItemStack expected = new ItemStack(Material.STONE, 7);
        assertInstanceOf(ItemStack.class, extra.getValue());
        ItemStack actual = (ItemStack) extra.getValue();
        assertEquals(expected.getType(), actual.getType());
        assertEquals(expected.getAmount(), actual.getAmount());
    }

    @Test
    void testInvalidTargetSpawnParticle() {
        assertThrowsExactly(IllegalArgumentException.class, () ->
                new Refl<>(WrappersAdapter.class).invokeMethod("spawnParticleCommon",
                        10, ParticleType.ASH.create(),
                        mock(Location.class), 0, 0.0, 1.0, 0.0, 0.0));
    }

    @Test
    void testPlaySound() {
        Sound sound = new Sound(org.bukkit.Sound.BLOCK_GLASS_STEP.name(),10, 2, SoundCategory.BLOCKS.name());
        Player player = mock(Player.class);

        when(player.getLocation()).thenReturn(new Location(null, 0, 1, 0));

        WrappersAdapter.playSound(player, sound);

        ArgumentCaptor<org.bukkit.Sound> soundArg = ArgumentCaptor.forClass(org.bukkit.Sound.class);
        ArgumentCaptor<SoundCategory> categoryArg = ArgumentCaptor.forClass(SoundCategory.class);
        ArgumentCaptor<Float> volumeArg = ArgumentCaptor.forClass(Float.class);
        ArgumentCaptor<Float> pitchArg = ArgumentCaptor.forClass(Float.class);
        verify(player).playSound(any(Location.class), soundArg.capture(),
                categoryArg.capture(), volumeArg.capture(), pitchArg.capture());

        assertEquals(sound.getName(), soundArg.getValue().name());

        assertEquals(sound.getVolume(), volumeArg.getValue());
        assertEquals(sound.getPitch(), pitchArg.getValue());

        assertEquals(sound.getCategory(), categoryArg.getValue().name());
    }

    @Test
    void testPlaySoundNoCategory() {
        Sound sound = new Sound(org.bukkit.Sound.BLOCK_GLASS_STEP.name(),10, 2);
        Player player = mock(Player.class);

        when(player.getLocation()).thenReturn(new Location(null, 0, 1, 0));

        WrappersAdapter.playSound(player, sound);

        ArgumentCaptor<org.bukkit.Sound> soundArg = ArgumentCaptor.forClass(org.bukkit.Sound.class);
        ArgumentCaptor<Float> volumeArg = ArgumentCaptor.forClass(Float.class);
        ArgumentCaptor<Float> pitchArg = ArgumentCaptor.forClass(Float.class);
        verify(player).playSound(any(Location.class), soundArg.capture(), volumeArg.capture(), pitchArg.capture());

        assertEquals(sound.getName(), soundArg.getValue().name());

        assertEquals(sound.getVolume(), volumeArg.getValue());
        assertEquals(sound.getPitch(), pitchArg.getValue());
    }

    @Test
    void testPlaySoundInvalidCategory() {
        Player player = mock(Player.class);
        assertThrowsExactly(IllegalArgumentException.class, () ->
                WrappersAdapter.playSound(player, new Sound(org.bukkit.Sound.BLOCK_AZALEA_FALL.name(),
                        1f, 1f, "hostiles")));
    }

    @Test
    void testCustomPlaySound() {
        Sound sound = new Sound(
                "custom_sound",10, 2, SoundCategory.BLOCKS.name());
        Player player = mock(Player.class);

        when(player.getLocation()).thenReturn(new Location(null, 0, 1, 0));

        WrappersAdapter.playCustomSound(player, sound);

        ArgumentCaptor<String> soundArg = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<SoundCategory> categoryArg = ArgumentCaptor.forClass(SoundCategory.class);
        ArgumentCaptor<Float> volumeArg = ArgumentCaptor.forClass(Float.class);
        ArgumentCaptor<Float> pitchArg = ArgumentCaptor.forClass(Float.class);
        verify(player).playSound(any(Location.class), soundArg.capture(),
                categoryArg.capture(), volumeArg.capture(), pitchArg.capture());

        assertEquals(sound.getName(), soundArg.getValue());

        assertEquals(sound.getVolume(), volumeArg.getValue());
        assertEquals(sound.getPitch(), pitchArg.getValue());

        assertEquals(sound.getCategory(), categoryArg.getValue().name());
    }

    @Test
    void testCustomPlaySoundNoCategory() {
        Sound sound = new Sound(
                "custom_sound",10, 2);
        Player player = mock(Player.class);

        when(player.getLocation()).thenReturn(new Location(null, 0, 1, 0));

        WrappersAdapter.playCustomSound(player, sound);

        ArgumentCaptor<String> soundArg = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Float> volumeArg = ArgumentCaptor.forClass(Float.class);
        ArgumentCaptor<Float> pitchArg = ArgumentCaptor.forClass(Float.class);
        verify(player).playSound(any(Location.class), soundArg.capture(), volumeArg.capture(), pitchArg.capture());

        assertEquals(sound.getName(), soundArg.getValue());

        assertEquals(sound.getVolume(), volumeArg.getValue());
        assertEquals(sound.getPitch(), pitchArg.getValue());
    }

    private static org.bukkit.potion.PotionEffect[] getPotionEffects() {
        List<PotionEffectType> potionEffects = new ArrayList<>();
        for (Field field : PotionEffectType.class.getDeclaredFields())
            if (field.getType().equals(PotionEffectType.class)) {
                PotionEffectType type = ReflectionUtils.getOrThrow(field, PotionEffectType.class);
                potionEffects.add(new MockPotionEffect(type.getId(), field.getName()));
            }
        // Register potion effects
        Map<String, PotionEffectType> byName = new Refl<>(PotionEffectType.class)
                .getFieldObject("byName");
        if (byName != null) potionEffects.forEach(e -> byName.put(e.getName().toLowerCase(), e));
        return potionEffects.stream()
                .map(t -> new org.bukkit.potion.PotionEffect(t, 15, 2, true, true, false))
                .toArray(org.bukkit.potion.PotionEffect[]::new);
    }

    @ParameterizedTest
    @MethodSource("getPotionEffects")
    void testPotionsConversion(org.bukkit.potion.PotionEffect expected) {
        PotionEffect enchantment = WrappersAdapter.potionEffectToWPotionEffect(expected);
        assertEquals(expected, WrappersAdapter.wPotionEffectToPotionEffect(enchantment));
    }

    private static org.bukkit.enchantments.Enchantment[] getEnchantments() {
        List<org.bukkit.enchantments.Enchantment> enchantments = new ArrayList<>();
        for (Field field : org.bukkit.enchantments.Enchantment.class.getDeclaredFields())
            if (field.getType().equals(org.bukkit.enchantments.Enchantment.class)) {
                org.bukkit.enchantments.Enchantment enchant = ReflectionUtils.getOrThrow(field, org.bukkit.enchantments.Enchantment.class);
                enchantments.add(mockEnchantment(enchant.getKey()));
            }
        // Register enchantments
        Map<NamespacedKey, org.bukkit.enchantments.Enchantment> byKey = new Refl<>(org.bukkit.enchantments.Enchantment.class)
                .getFieldObject("byKey");
        if (byKey != null) enchantments.forEach(e -> byKey.put(e.getKey(), e));
        return enchantments.toArray(new org.bukkit.enchantments.Enchantment[0]);
    }

    @ParameterizedTest
    @MethodSource("getEnchantments")
    void testEnchantmentsConversion(org.bukkit.enchantments.Enchantment expected) {
        Enchantment enchantment = WrappersAdapter.enchantToWEnchant(expected);
        assertEquals(expected, WrappersAdapter.wEnchantToEnchant(enchantment).getKey());
    }

    @Test
    void testEnchantmentConversionByName() {
        org.bukkit.enchantments.Enchantment expected = mockEnchantment(org.bukkit.enchantments.Enchantment.FIRE_ASPECT.getKey());
        // Register enchantments
        Map<String, org.bukkit.enchantments.Enchantment> byName = new Refl<>(org.bukkit.enchantments.Enchantment.class)
                .getFieldObject("byName");
        if (byName != null) byName.put(expected.getName(), expected);
        Enchantment enchantment = new Enchantment(expected.getName());
        assertEquals(expected, WrappersAdapter.wEnchantToEnchant(enchantment).getKey());
    }

    @Test
    void testInvalidEnchantmentProvided() {
        assertThrowsExactly(IllegalArgumentException.class, () -> WrappersAdapter.wEnchantToEnchant(new Enchantment("mock")));
    }

    private static org.bukkit.enchantments.Enchantment mockEnchantment(final @NotNull NamespacedKey key) {
        org.bukkit.enchantments.Enchantment mock = mock(org.bukkit.enchantments.Enchantment.class);
        when(mock.getKey()).thenReturn(key);
        when(mock.getName()).thenReturn(key.getKey());
        return mock;
    }

    @Test
    void testInvalidClassProvided() {
        Refl<?> refl = new Refl<>(WrappersAdapter.class);
        assertThrowsExactly(IllegalArgumentException.class, () -> refl.invokeMethod("wParticleToGeneral",
                ParticleType.DUST_COLOR_TRANSITION.create(Color.RED, Color.RED, 3f),
                org.bukkit.Particle.class, (Function<?, Class<?>>) s -> org.bukkit.Particle.REDSTONE.getDataType()));
    }

    @Test
    void testNoConstructorDataTypeProvided() {
        Refl<?> refl = new Refl<>(WrappersAdapter.class);
        assertThrowsExactly(IllegalArgumentException.class, () -> refl.invokeMethod("wParticleToGeneral",
                ParticleType.REDSTONE.create(Color.RED, 3f), org.bukkit.Particle.class,
                (Function<?, Class<?>>) s -> AbstractItem.class));
    }

    private static Color[] getColors() {
        return Color.values();
    }

    @ParameterizedTest
    @MethodSource("getColors")
    void testColorConversion(Color expected) {
        org.bukkit.Color color = WrappersAdapter.wColorToColor(expected);
        assertEquals(expected, WrappersAdapter.colorToWColor(color));
    }

    @Test
    void testNullDataForMaterialData() {
        Material material = Material.LEGACY_STONE;
        Tuple<String, Integer> option = new Tuple<>(material.name(), null);
        MaterialData expected = material.getNewData((byte) 0);
        assertEquals(expected, WrappersAdapter.convertOption(MaterialData.class, option));
    }

    @Test
    void testInvalidOptionForMaterialData() {
        assertThrowsExactly(IllegalArgumentException.class, () -> WrappersAdapter.convertOption(MaterialData.class, "string"));
    }

    @ParameterizedTest
    @EnumSource(Material.class)
    void testAllBlockData(Material material) {
        initializeBlockData();
        Executable executable = () -> WrappersAdapter.convertOption(BlockData.class, material.name());
        if (material.isBlock()) assertDoesNotThrow(executable);
        else assertThrowsExactly(IllegalArgumentException.class, executable);
    }

    @Test
    void testInvalidParticleGeneralDataType() {
        Tuple<?, ?> conversion = new Refl<>(WrappersAdapter.class).invokeMethod("wParticleToGeneral",
                ParticleType.ASH.create(), org.bukkit.Particle.class, (Function<?, Class<?>>) s -> null);
        assertNotNull(conversion);
        assertNotNull(conversion.getKey());
        assertNull(conversion.getValue());
    }

    @Test
    void testInvalidDataTypeButNotOption() {
        Tuple<?, ?> conversion = new Refl<>(WrappersAdapter.class).invokeMethod("wParticleToGeneral",
                ParticleType.REDSTONE.create(Color.RED, 3f), org.bukkit.Particle.class, (Function<?, Class<?>>) s -> null);
        assertNotNull(conversion);
        assertNotNull(conversion.getKey());
        assertNull(conversion.getValue());
    }

    @Test
    void testInvalidDataType() {
        assertThrowsExactly(IllegalArgumentException.class, () -> WrappersAdapter.convertOption(MockDataType.class, "String"));
    }

    @Test
    void testItemModuleNotProvided() {
        try (MockedStatic<ReflectionUtils> clazz = mockStatic(ReflectionUtils.class)) {
            clazz.when(() -> ReflectionUtils.getClass(any(String.class))).thenThrow(IllegalArgumentException.class);

            assertThrowsExactly(IllegalStateException.class, () -> WrappersAdapter.itemToItemStack(null),
                    "Should throw exception signaling missing item module");
        }
    }

    private static void initializeBlockData() {
        Server server = mock(Server.class);
        when(server.createBlockData(any(Material.class), any(String.class))).thenReturn(mock(BlockData.class));
        new Refl<>(Bukkit.class).setFieldObject("server", server);
    }

    @SuppressWarnings("unused")
    private static class MockDataType {

        public MockDataType(String s1, String s2, String s3, String s4) {

        }

    }

    private static class MockPotionEffect extends PotionEffectType {
        private final String name;

        protected MockPotionEffect(int id, String name) {
            super(id, NamespacedKey.minecraft(name.toLowerCase()));
            this.name = name;
        }

        @Override
        public double getDurationModifier() {
            return 0;
        }

        @NotNull
        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public boolean isInstant() {
            return false;
        }

        @NotNull
        @Override
        public org.bukkit.Color getColor() {
            return null;
        }
    }

    private static class MockEnchantment extends org.bukkit.enchantments.Enchantment {

        public MockEnchantment(@NotNull NamespacedKey key) {
            super(key);
        }

        @NotNull
        @Override
        public String getName() {
            return getKey().getKey();
        }

        @Override
        public int getMaxLevel() {
            return 0;
        }

        @Override
        public int getStartLevel() {
            return 0;
        }

        @NotNull
        @Override
        public EnchantmentTarget getItemTarget() {
            return null;
        }

        @Override
        public boolean isTreasure() {
            return false;
        }

        @Override
        public boolean isCursed() {
            return false;
        }

        @Override
        public boolean conflictsWith(@NotNull org.bukkit.enchantments.Enchantment other) {
            return false;
        }

        @Override
        public boolean canEnchantItem(@NotNull ItemStack item) {
            return false;
        }
    }
}