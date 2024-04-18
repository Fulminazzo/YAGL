package it.angrybear.yagl.guis;

import it.angrybear.yagl.contents.GUIContent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * An implementation of {@link PageableGUI} that allows data to be automatically displayed in a multi-paged format.
 *
 * @param <T> the type of the data
 */
public class DataGUI<T> extends PageableGUI {
    private static final String ERROR_MESSAGE = "Pages are dynamically calculated when opening this GUI. They cannot be singly edited";

    private final List<T> data;
    private final Function<T, GUIContent> dataConverter;

    /**
     * Instantiates a new Data gui.
     *
     * @param size the size
     * @param data the data
     */
    DataGUI(int size, List<T> data, Function<T, GUIContent> dataConverter) {
        super(size);
        this.data = data;
        this.dataConverter = dataConverter;
    }

    /**
     * Instantiates a new Data gui.
     *
     * @param type the type
     * @param data the data
     */
    DataGUI(@NotNull GUIType type, List<T> data, Function<T, GUIContent> dataConverter) {
        super(type);
        this.data = data;
        this.dataConverter = dataConverter;
    }

    /**
     * Adds the given data.
     *
     * @param data the data
     * @return this gui
     */
    public DataGUI<T> addData(final T @NotNull ... data) {
        return addData(Arrays.asList(data));
    }

    /**
     * Adds the given data.
     *
     * @param data the data
     * @return this gui
     */
    public DataGUI<T> addData(final @NotNull Collection<T> data) {
        this.data.addAll(data);
        return this;
    }

    /**
     * Clears the current data and sets the given data.
     *
     * @param data the data
     * @return the data
     */
    public DataGUI<T> setData(final T @NotNull ... data) {
        return setData(Arrays.asList(data));
    }

    /**
     * Clears the current data and sets the given data.
     *
     * @param data the data
     * @return the data
     */
    public DataGUI<T> setData(final @NotNull Collection<T> data) {
        return clearData().addData(data);
    }

    /**
     * Removes the data equal to any of the given data.
     *
     * @param data the data
     * @return this gui
     */
    public DataGUI<T> removeData(final T @NotNull ... data) {
        return removeData(Arrays.asList(data));
    }

    /**
     * Removes the data equal to any of the given data.
     *
     * @param data the data
     * @return this gui
     */
    public DataGUI<T> removeData(final @NotNull Collection<T> data) {
        return removeData(t -> t.equals(data));
    }

    /**
     * Removes the data that match the given {@link Predicate} function.
     *
     * @param function the function
     * @return this gui
     */
    public DataGUI<T> removeData(final @NotNull Predicate<T> function) {
        this.data.removeIf(function);
        return this;
    }

    /**
     * Removes all the data.
     *
     * @return this gui
     */
    public DataGUI<T> clearData() {
        this.data.clear();
        return this;
    }

    /**
     * Gets the {@link GUI} page from the given index.
     * The index starts from <b>0</b>.
     *
     * @param page the page
     * @deprecated In {@link DataGUI}s pages are not pre-defined, but rather calculated upon opening.
     * @return the corresponding {@link GUI} page
     */
    @Override
    @Deprecated
    public GUI getPage(int page) {
        throw new IllegalStateException(ERROR_MESSAGE);
    }

    /**
     * Sets pages.
     *
     * @param pages the pages
     * @deprecated In {@link DataGUI}s pages are not pre-defined, but rather calculated upon opening.
     * @return this gui
     */
    @Override
    public PageableGUI setPages(int pages) {
        throw new IllegalStateException(ERROR_MESSAGE);
    }

    /**
     * Gets the number of pages based on the amount of data provided.
     *
     * @return the pages
     */
    @Override
    public int pages() {
        double pages = (double) this.data.size() / emptySlots().size();
        return (int) Math.ceil(pages);
    }

}
