package it.angrybear.yagl.wrappers;

import it.angrybear.yagl.TestUtils;
import it.fulminazzo.fulmicollection.interfaces.functions.ConsumerException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class WrapperTest {

    private static Wrapper[] testWrappers() {
        return new Wrapper[]{
                new Enchantment("ench"),
                new Potion("pot"),
                new PotionEffect("eff"),
                new Sound("snd")
        };
    }

    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @MethodSource("testWrappers")
    void testWrappersReturnType(Wrapper item) {
        TestUtils.testReturnType(item, (Class<? super Wrapper>) item.getClass(), null);
    }

    @Test
    void testValidValue() {
        assertDoesNotThrow(() -> new MockWrapper().setValue(5));
    }

    private static Object[][] getTestSetters() {
        return new Object[][]{
                new Object[]{(ConsumerException<MockWrapper>) wrapper -> wrapper.setValue(0), 1, 9},
                new Object[]{(ConsumerException<MockWrapper>) wrapper -> wrapper.setValue(10), 1, 9},
                new Object[]{(ConsumerException<MockWrapper>) wrapper -> wrapper.minOnly(0), 1, null},
                new Object[]{(ConsumerException<MockWrapper>) wrapper -> wrapper.getMaxOnly(10), null, 9},
        };
    }

    @ParameterizedTest
    @MethodSource("getTestSetters")
    void testSettersRanges(ConsumerException<MockWrapper> consumer, Integer min, Integer max) {
        final Class<? extends Exception> expected = IllegalArgumentException.class;
        try {
            MockWrapper wrapper = new MockWrapper();
            consumer.accept(wrapper);
            fail(String.format("Test should have thrown an %s", expected.getSimpleName()));
        } catch (Exception e) {
            assertInstanceOf(expected, e, String.format("Exception should have been %s, but was %s",
                    expected.getSimpleName(), e.getClass().getSimpleName()));

            final String message = e.getMessage();
            assertNotNull(message, "Error message should have not been null");

            if (min != null) {
                assertTrue(message.contains(String.valueOf(min)), "Error message should contain min value");
                if (max == null) assertTrue(message.contains("lower"), "Error message should contain 'lower' keyword");
            } else
                assertFalse(message.contains(String.valueOf(Integer.MIN_VALUE)), "Error message should have not contained min value");

            if (max != null) {
                assertTrue(message.contains(String.valueOf(max)), "Error message should contain max value");
                if (min == null) assertTrue(message.contains("higher"), "Error message should contain 'higher' keyword");
            } else
                assertFalse(message.contains(String.valueOf(Integer.MAX_VALUE)), "Error message should have not contained max value");
        }
    }

    @Test
    void testExtremeRange() {
        assertThrowsExactly(IllegalArgumentException.class, () -> new MockWrapper().setExtremeRange((long) Integer.MAX_VALUE + 1));
    }

    @Test
    void testInvalidRange() {
        assertThrowsExactly(InvalidRangeException.class, () -> new MockWrapper().setInvalidRange(10));
    }

    @Test
    void testNoFieldSet() {
        assertDoesNotThrow(() -> new MockWrapper().setFree(10));
    }

    @Test
    void testIsSimilarNull() {
        Wrapper w1 = new MockWrapper();
        assertFalse(w1.isSimilar(null), "Wrapper should not be similar to null");
    }

    @Test
    void testIsSimilarOtherClass() {
        Wrapper w1 = new MockWrapper();
        assertFalse(w1.isSimilar(mock(Wrapper.class)), "Wrapper should not be similar to a wrapper of different class");
    }

    @SuppressWarnings("unused")
    private static class MockWrapper extends Wrapper {
        @Range(min = 1, max = 9)
        private int value;
        @Range(min = 1)
        private int minOnly;
        @Range(max = 9)
        private int maxOnly;
        private int free;
        @Range(max = Integer.MIN_VALUE, min = Integer.MAX_VALUE)
        private int invalidRange;
        @Range(min = Integer.MIN_VALUE, max = Integer.MAX_VALUE)
        private long extremeRange;

        public void setValue(int value) {
            this.value = check(value);
        }

        public void minOnly(int minOnly) {
            this.minOnly = check(minOnly);
        }

        // For testing purposes. A very bad practice in the real world.
        public void getMaxOnly(int maxOnly) {
            this.maxOnly = check(maxOnly);
        }

        public void setFree(int free) {
            this.free = check(free);
        }

        public void setInvalidRange(int invalidRange) {
            this.invalidRange = check(invalidRange);
        }

        public void setExtremeRange(long extremeRange) {
            this.extremeRange = check(extremeRange);
        }

        @Override
        public String getName() {
            return "aiden";
        }
    }
}