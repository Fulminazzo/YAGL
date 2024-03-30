package it.angrybear.yagl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;

/**
 * An abstract class that allows to "convert" classes to enums by providing the most three common methods:
 * {@link #name()}, {@link #values(Class)} and {@link #valueOf(String, Class)}.
 */
@SuppressWarnings("unchecked")
public abstract class ClassEnum {
    private static final Map<Class<?>, EnumCache<?>> CACHE_MAP = new HashMap<>();
    private final int index;

    /**
     * Instantiates a new Class enum.
     */
    public ClassEnum() {
        this.index = getCache().getAndIncrementOrdinal();
    }

    /**
     * Returns the index of the current object.
     *
     * @return the index
     */
    public int ordinal() {
        return this.index;
    }

    /**
     * Gets the name of the current object.
     *
     * @return the name
     */
    public String name() {
        return getCache().name(this);
    }

    /**
     * Gets the field from its index, returned by {@link #ordinal()}..
     *
     * @param <T>   the type parameter
     * @param index the index
     * @param clazz the clazz
     * @return the t
     */
    protected static <T extends ClassEnum> T valueOf(final @Range(from = 0, to = Integer.MAX_VALUE) int index,
                                                     final @NotNull Class<T> clazz) {
        return getCache(clazz).valueOf(index);
    }

    /**
     * Gets the field from its name.
     *
     * @param <T>   the type parameter
     * @param name  the name
     * @param clazz the clazz
     * @return the field
     */
    protected static <T extends ClassEnum> T valueOf(final @NotNull String name, final @NotNull Class<T> clazz) {
        return getCache(clazz).valueOf(name);
    }

    /**
     * Gets all the fields in an array.
     *
     * @param <T>   the type parameter
     * @param clazz the clazz
     * @return the fields
     */
    protected static <T extends ClassEnum> T[] values(final @NotNull Class<T> clazz) {
        return getCache(clazz).values();
    }

    private <T extends ClassEnum> EnumCache<T> getCache() {
        return getCache((Class<T>) getClass());
    }

    private static <T extends ClassEnum> EnumCache<T> getCache(final @NotNull Class<T> clazz) {
        return (EnumCache<T>) CACHE_MAP.computeIfAbsent(clazz, c -> new EnumCache<>((Class<T>) c));
    }

    private static class EnumCache<T extends ClassEnum> {
        private final Class<T> clazz;
        private final Map<String, T> values;
        private int ordinal;

        private EnumCache(Class<T> clazz) {
            this.clazz = clazz;
            this.values = new LinkedHashMap<>();
            this.ordinal = 0;
        }

        public int getAndIncrementOrdinal() {
            return this.ordinal++;
        }

        public String name(final @NotNull T t) {
            if (this.values.isEmpty()) fillValues();
            for (String k : this.values.keySet())
                if (t.hashCode() == this.values.get(k).hashCode())
                    return k;
            throw new IllegalStateException(String.format("Could not find name of object '%s'", t));
        }

        public T valueOf(final @Range(from = 0, to = Integer.MAX_VALUE) int index) {
            if (this.values.isEmpty()) fillValues();
            for (T t : this.values.values()) if (t.ordinal() == index) return t;
            throw new IllegalArgumentException(String.format("Could not find %s with index '%s'", this.clazz.getSimpleName().toLowerCase(), index));
        }

        public T valueOf(final @NotNull String name) {
            if (this.values.isEmpty()) fillValues();
            return this.values.get(name.toUpperCase());
        }

        public T[] values() {
            if (this.values.isEmpty()) fillValues();
            return this.values.values().toArray((T[]) Array.newInstance(this.clazz, 0));
        }

        private void fillValues() {
            for (Field field : this.clazz.getDeclaredFields())
                if (field.getType().equals(this.clazz))
                    try {
                        field.setAccessible(true);
                        this.values.put(field.getName().toUpperCase(), (T) field.get(this.clazz));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
        }
    }
}
