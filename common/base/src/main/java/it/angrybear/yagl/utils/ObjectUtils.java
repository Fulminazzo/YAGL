package it.angrybear.yagl.utils;

import it.fulminazzo.fulmicollection.objects.Refl;
import it.fulminazzo.fulmicollection.utils.ReflectionUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The type Object utils.
 */
@SuppressWarnings("unchecked")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ObjectUtils {

    /**
     * Copies the given object to a new one.
     *
     * @param <T> the type of the object to copy from
     * @param t   the object to copy from
     * @return the copy
     */
    public static <T> T copy(final @NotNull T t) {
        Class<? extends T> clazz = (Class<? extends T>) t.getClass();
        try {
            ReflectionUtils.getConstructor(clazz);
        } catch (Exception e) {
            // Get the most abstract class
            Class<?> c = clazz;
            Class<?>[] interfaces;
            while ((interfaces = c.getInterfaces()).length != 0) {
                Class<?> tmp = interfaces[0];
                if (!tmp.equals(Iterable.class) && !tmp.getSimpleName().startsWith("Abstract"))
                    c = tmp;
                else break;
            }
            clazz = ReflectionUtils.getClass(c.getCanonicalName() + "Impl");
        }
        return copy(t, clazz);
    }

    /**
     * Copies the given object to a new one using the provided class.
     * If an interface is provided, it tries to convert it to a non-abstract implementation by appending <i>Impl</i>.
     * If no such class is found, an {@link IllegalArgumentException} is thrown.
     *
     * @param <T>   the type of the object to copy from
     * @param <O>   the type of the returned object
     * @param t     the object to copy from
     * @param clazz the class to copy to
     * @return the copy
     */
    public static <T, O extends T> O copy(final @NotNull T t, @NotNull Class<O> clazz) {
        if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers()))
            try {
                clazz = ReflectionUtils.getClass(clazz.getCanonicalName() + "Impl");
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(String.format("Could not copy object to abstract class '%s': no '%sImpl' class found",
                        clazz.getCanonicalName(), clazz.getCanonicalName()));
            }

        final Refl<O> object = new Refl<>(clazz, new Object[0]);
        for (final Field field : object.getNonStaticFields()) {
            field.setAccessible(true);
            try {
                ReflectionUtils.get(field, t).map(obj1 -> {
                    if (obj1 == null) return null;
                    else if (obj1 instanceof Collection) return copyCollection(obj1);
                    else if (obj1 instanceof Map) return copyMap(obj1);
                    else if (obj1.getClass().isArray()) return copyArray(obj1);
                    else return copyWithMethod(obj1);
                }).ifPresent(obj1 -> object.setFieldObject(field, obj1));
            } catch (IllegalArgumentException e) {
                if (!e.getMessage().contains("Can not set")) throw e;
            }
        }
        return object.getObject();
    }

    private static @NotNull Object[] copyArray(final @NotNull Object obj1) {
        Object[] tmp = (Object[]) obj1;
        Object[] arr = (Object[]) Array.newInstance(obj1.getClass().getComponentType(), tmp.length);
        for (int i = 0; i < tmp.length; i++) arr[i] = copyWithMethod(tmp[i]);
        return arr;
    }

    private static @NotNull Map<Object, Object> copyMap(final @NotNull Object obj1) {
        Map<Object, Object> map = new HashMap<>();
        ((Map<Object, Object>) obj1).forEach((k, v) ->
                map.put(copyWithMethod(k), copyWithMethod(v)));
        return map;
    }

    private static @NotNull Collection<?> copyCollection(final @NotNull Object obj1) {
        Class<?> tmpClass = obj1.getClass();
        // In the case of creation with Arrays.asList()
        if (tmpClass.getCanonicalName().equals(Arrays.class.getCanonicalName() + ".ArrayList"))
            tmpClass = ArrayList.class;
        Class<Collection<Object>> finalClass = (Class<Collection<Object>>) tmpClass;
        return ((Collection<?>) obj1).stream()
                .map(ObjectUtils::copyWithMethod)
                .collect(Collectors.toCollection(() -> new Refl<>(finalClass, new Object[0]).getObject()));
    }

    private static @Nullable Object copyWithMethod(final @Nullable Object obj1) {
        try {
            if (obj1 == null) return null;
            Method copy = obj1.getClass().getDeclaredMethod("copy");
            return ReflectionUtils.setAccessible(copy)
                    .map(m -> m.invoke(obj1))
                    .orElseGet(obj1);
        } catch (NoSuchMethodException e) {
            return obj1;
        }
    }

}
