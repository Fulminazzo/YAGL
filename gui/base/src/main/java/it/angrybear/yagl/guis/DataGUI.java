package it.angrybear.yagl.guis;

import it.angrybear.yagl.Metadatable;
import it.angrybear.yagl.actions.BiGUIAction;
import it.angrybear.yagl.actions.GUIAction;
import it.angrybear.yagl.contents.GUIContent;
import it.angrybear.yagl.contents.ItemGUIContent;
import it.angrybear.yagl.exceptions.NotImplemented;
import it.angrybear.yagl.items.Item;
import it.angrybear.yagl.viewers.Viewer;
import lombok.Data;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * An implementation of {@link PageableGUI} that allows data to be automatically displayed in a multi-paged format.
 *
 * @param <T> the type of the data
 */
@SuppressWarnings("unchecked")
public class DataGUI<T> extends PageableGUI {
    private static final String ERROR_MESSAGE = "Pages are dynamically calculated when opening this GUI. They cannot be singly edited";

    @Getter
    private final @NotNull List<T> data;
    private final @NotNull Function<T, GUIContent> dataConverter;

    private DataGUI() {
        this.data = new LinkedList<>();
        this.dataConverter = t -> {
            throw new NotImplemented();
        };
    }

    private DataGUI(final int size) {
        this(size, t -> {
            throw new NotImplemented();
        });
    }

    /**
     * Instantiates a new Data gui.
     *
     * @param size          the size
     * @param dataConverter the data converter
     */
    DataGUI(final int size, final @NotNull Function<T, GUIContent> dataConverter) {
        super(size);
        this.data = new LinkedList<>();
        this.dataConverter = dataConverter;
    }

    /**
     * Instantiates a new Data gui.
     *
     * @param type          the type
     * @param dataConverter the data converter
     */
    DataGUI(final @NotNull GUIType type, final @NotNull Function<T, GUIContent> dataConverter) {
        super(type);
        this.data = new LinkedList<>();
        this.dataConverter = dataConverter;
    }

    /**
     * Adds the given data.
     *
     * @param data the data
     * @return this gui
     */
    public @NotNull DataGUI<T> addData(final T @NotNull ... data) {
        return addData(Arrays.asList(data));
    }

    /**
     * Adds the given data.
     *
     * @param data the data
     * @return this gui
     */
    public @NotNull DataGUI<T> addData(final @NotNull Collection<T> data) {
        this.data.addAll(data);
        return this;
    }

    /**
     * Clears the current data and sets the given data.
     *
     * @param data the data
     * @return the data
     */
    public @NotNull DataGUI<T> setData(final T @NotNull ... data) {
        return setData(Arrays.asList(data));
    }

    /**
     * Clears the current data and sets the given data.
     *
     * @param data the data
     * @return the data
     */
    public @NotNull DataGUI<T> setData(final @NotNull Collection<T> data) {
        return clearData().addData(data);
    }

    /**
     * Removes the data equal to any of the given data.
     *
     * @param data the data
     * @return this gui
     */
    public @NotNull DataGUI<T> removeData(final T @NotNull ... data) {
        return removeData(Arrays.asList(data));
    }

    /**
     * Removes the data equal to any of the given data.
     *
     * @param data the data
     * @return this gui
     */
    public @NotNull DataGUI<T> removeData(final @NotNull Collection<T> data) {
        return removeData(t -> data.stream().anyMatch(c -> Objects.equals(c, t)));
    }

    /**
     * Removes the data that match the given {@link Predicate} function.
     *
     * @param function the function
     * @return this gui
     */
    public @NotNull DataGUI<T> removeData(final @NotNull Predicate<T> function) {
        this.data.removeIf(function);
        return this;
    }

    /**
     * Removes all the data.
     *
     * @return this gui
     */
    public @NotNull DataGUI<T> clearData() {
        this.data.clear();
        return this;
    }

    @Override
    public void open(@NotNull Viewer viewer, int page) {
        fillContents(prepareOpenGUI(this.templateGUI, page), page).open(viewer);
    }

    private @NotNull GUI fillContents(final @NotNull GUI gui, final int page) {
        int emptySlots = emptySlots().size();
        int min = emptySlots * page;
        if (min >= this.data.size())
            throw new IllegalArgumentException(String.format("No such page '%s'", page));
        if (page > 0) min++;
        int size = Math.min(gui.emptySlots().size() + min, this.data.size());
        for (int i = min; i < size; i++) {
            T data = this.data.get(i);
            GUIContent content = this.dataConverter.apply(data);
            gui.addContent(content);
        }
        return gui;
    }

    /**
     * Gets the first page empty slots.
     *
     * @return the slots
     */
    protected int getFirstPageEmptySlots() {
        int slots = getPageEmptySlots();
        if (this.previousPage.isPresent()) slots++;
        return slots;
    }

    /**
     * Gets the last page empty slots.
     *
     * @return the slots
     */
    protected int getLastPageEmptySlots() {
        int slots = getPageEmptySlots();
        if (this.nextPage.isPresent()) slots++;
        return slots;
    }

    /**
     * Gets a general page empty slots.
     *
     * @return the slots
     */
    protected int getPageEmptySlots() {
        final Set<Integer> slots = emptySlots();
        this.previousPage.ifPresent((i, p) -> slots.remove(i));
        this.nextPage.ifPresent((i, p) -> slots.remove(i));
        return slots.size();
    }

    /**
     * Gets the number of pages based on the amount of data provided.
     *
     * @return the pages
     */
    @Override
    public int pages() {
        int dataSize = this.data.size();
        if (dataSize == 0) throw new IllegalArgumentException("Cannot set empty data");
        final int emptySlots = getPageEmptySlots();
        final int firstPageSlots = getFirstPageEmptySlots();
        final int lastPageSlots = getLastPageEmptySlots();
        int pages = 1;
        // First page
        dataSize -= firstPageSlots;
        if (dataSize < 1) return pages;
        if (emptySlots < 1)
            throw new IllegalStateException(String.format("Not enough empty slots available to set %s items of data", dataSize));
        // Last page
        pages++;
        dataSize -= lastPageSlots;
        if (dataSize < 1) return pages;
        // Other pages
        while (dataSize > 0) {
            pages++;
            dataSize -= emptySlots;
        }
        return pages;
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
    public @NotNull GUI getPage(int page) {
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
    @Deprecated
    public @NotNull DataGUI<T> setPages(int pages) {
        throw new IllegalStateException(ERROR_MESSAGE);
    }

    @Override
    public @NotNull DataGUI<T> setPreviousPage(int slot, @NotNull Item previousPage) {
        return (DataGUI<T>) super.setPreviousPage(slot, previousPage);
    }

    @Override
    public @NotNull DataGUI<T> setPreviousPage(int slot, @NotNull GUIContent previousPage) {
        return (DataGUI<T>) super.setPreviousPage(slot, previousPage);
    }

    @Override
    public @NotNull DataGUI<T> unsetPreviousPage() {
        return (DataGUI<T>) super.unsetPreviousPage();
    }

    @Override
    public @NotNull DataGUI<T> setNextPage(int slot, @NotNull Item nextPage) {
        return (DataGUI<T>) super.setNextPage(slot, nextPage);
    }

    @Override
    public @NotNull DataGUI<T> setNextPage(int slot, @NotNull GUIContent nextPage) {
        return (DataGUI<T>) super.setNextPage(slot, nextPage);
    }

    @Override
    public @NotNull DataGUI<T> unsetNextPage() {
        return (DataGUI<T>) super.unsetNextPage();
    }

    @Override
    public @NotNull DataGUI<T> setTitle(@Nullable String title) {
        return (DataGUI<T>) super.setTitle(title);
    }

    @Override
    public @NotNull DataGUI<T> setMovable(int slot, boolean movable) {
        return (DataGUI<T>) super.setMovable(slot, movable);
    }

    @Override
    public @NotNull DataGUI<T> addContent(GUIContent @NotNull ... contents) {
        return (DataGUI<T>) super.addContent(contents);
    }

    @Override
    public @NotNull DataGUI<T> setContents(int slot, GUIContent @NotNull ... contents) {
        return (DataGUI<T>) super.setContents(slot, contents);
    }

    @Override
    public @NotNull DataGUI<T> setContents(int slot, @NotNull Collection<GUIContent> contents) {
        return (DataGUI<T>) super.setContents(slot, contents);
    }

    @Override
    public @NotNull DataGUI<T> unsetContent(int slot) {
        return (DataGUI<T>) super.unsetContent(slot);
    }

    @Override
    public @NotNull DataGUI<T> onClickOutside(@NotNull GUIAction action) {
        return (DataGUI<T>) super.onClickOutside(action);
    }

    @Override
    public @NotNull DataGUI<T> onOpenGUI(@NotNull GUIAction action) {
        return (DataGUI<T>) super.onOpenGUI(action);
    }

    @Override
    public @NotNull DataGUI<T> onCloseGUI(@NotNull GUIAction action) {
        return (DataGUI<T>) super.onCloseGUI(action);
    }

    @Override
    public @NotNull DataGUI<T> onChangeGUI(@NotNull BiGUIAction action) {
        return (DataGUI<T>) super.onChangeGUI(action);
    }

    @Override
    public @NotNull DataGUI<T> setAllMovable() {
        return (DataGUI<T>) super.setAllMovable();
    }

    @Override
    public @NotNull DataGUI<T> setAllUnmovable() {
        return (DataGUI<T>) super.setAllUnmovable();
    }

    @Override
    public @NotNull DataGUI<T> addContent(Item @NotNull ... contents) {
        return (DataGUI<T>) super.addContent(contents);
    }

    @Override
    public @NotNull DataGUI<T> addContent(ItemGUIContent @NotNull ... contents) {
        return (DataGUI<T>) super.addContent(contents);
    }

    @Override
    public @NotNull DataGUI<T> setContents(int slot, Item @NotNull ... contents) {
        return (DataGUI<T>) super.setContents(slot, contents);
    }

    @Override
    public @NotNull DataGUI<T> setContents(int slot, ItemGUIContent @NotNull ... contents) {
        return (DataGUI<T>) super.setContents(slot, contents);
    }

    @Override
    public @NotNull DataGUI<T> setAllSides(Item @NotNull ... contents) {
        return (DataGUI<T>) super.setAllSides(contents);
    }

    @Override
    public @NotNull DataGUI<T> setAllSides(ItemGUIContent @NotNull ... contents) {
        return (DataGUI<T>) super.setAllSides(contents);
    }

    @Override
    public @NotNull DataGUI<T> setAllSides(GUIContent @NotNull ... contents) {
        return (DataGUI<T>) super.setAllSides(contents);
    }

    @Override
    public @NotNull DataGUI<T> setAllSides(@NotNull Collection<GUIContent> contents) {
        return (DataGUI<T>) super.setAllSides(contents);
    }

    @Override
    public @NotNull DataGUI<T> unsetAllSides() {
        return (DataGUI<T>) super.unsetAllSides();
    }

    @Override
    public @NotNull DataGUI<T> setTopAndBottomSides(Item @NotNull ... contents) {
        return (DataGUI<T>) super.setTopAndBottomSides(contents);
    }

    @Override
    public @NotNull DataGUI<T> setTopAndBottomSides(ItemGUIContent @NotNull ... contents) {
        return (DataGUI<T>) super.setTopAndBottomSides(contents);
    }

    @Override
    public @NotNull DataGUI<T> setTopAndBottomSides(GUIContent @NotNull ... contents) {
        return (DataGUI<T>) super.setTopAndBottomSides(contents);
    }

    @Override
    public @NotNull DataGUI<T> setTopAndBottomSides(@NotNull Collection<GUIContent> contents) {
        return (DataGUI<T>) super.setTopAndBottomSides(contents);
    }

    @Override
    public @NotNull DataGUI<T> unsetTopAndBottomSides() {
        return (DataGUI<T>) super.unsetTopAndBottomSides();
    }

    @Override
    public @NotNull DataGUI<T> setLeftAndRightSides(Item @NotNull ... contents) {
        return (DataGUI<T>) super.setLeftAndRightSides(contents);
    }

    @Override
    public @NotNull DataGUI<T> setLeftAndRightSides(ItemGUIContent @NotNull ... contents) {
        return (DataGUI<T>) super.setLeftAndRightSides(contents);
    }

    @Override
    public @NotNull DataGUI<T> setLeftAndRightSides(GUIContent @NotNull ... contents) {
        return (DataGUI<T>) super.setLeftAndRightSides(contents);
    }

    @Override
    public @NotNull DataGUI<T> setLeftAndRightSides(@NotNull Collection<GUIContent> contents) {
        return (DataGUI<T>) super.setLeftAndRightSides(contents);
    }

    @Override
    public @NotNull DataGUI<T> unsetLeftAndRightSides() {
        return (DataGUI<T>) super.unsetLeftAndRightSides();
    }

    @Override
    public @NotNull DataGUI<T> setTopSide(Item @NotNull ... contents) {
        return (DataGUI<T>) super.setTopSide(contents);
    }

    @Override
    public @NotNull DataGUI<T> setTopSide(ItemGUIContent @NotNull ... contents) {
        return (DataGUI<T>) super.setTopSide(contents);
    }

    @Override
    public @NotNull DataGUI<T> setTopSide(GUIContent @NotNull ... contents) {
        return (DataGUI<T>) super.setTopSide(contents);
    }

    @Override
    public @NotNull DataGUI<T> setTopSide(@NotNull Collection<GUIContent> contents) {
        return (DataGUI<T>) super.setTopSide(contents);
    }

    @Override
    public @NotNull DataGUI<T> unsetTopSide() {
        return (DataGUI<T>) super.unsetTopSide();
    }

    @Override
    public @NotNull DataGUI<T> setLeftSide(Item @NotNull ... contents) {
        return (DataGUI<T>) super.setLeftSide(contents);
    }

    @Override
    public @NotNull DataGUI<T> setLeftSide(ItemGUIContent @NotNull ... contents) {
        return (DataGUI<T>) super.setLeftSide(contents);
    }

    @Override
    public @NotNull DataGUI<T> setLeftSide(GUIContent @NotNull ... contents) {
        return (DataGUI<T>) super.setLeftSide(contents);
    }

    @Override
    public @NotNull DataGUI<T> setLeftSide(@NotNull Collection<GUIContent> contents) {
        return (DataGUI<T>) super.setLeftSide(contents);
    }

    @Override
    public @NotNull DataGUI<T> unsetLeftSide() {
        return (DataGUI<T>) super.unsetLeftSide();
    }

    @Override
    public @NotNull DataGUI<T> setBottomSide(Item @NotNull ... contents) {
        return (DataGUI<T>) super.setBottomSide(contents);
    }

    @Override
    public @NotNull DataGUI<T> setBottomSide(ItemGUIContent @NotNull ... contents) {
        return (DataGUI<T>) super.setBottomSide(contents);
    }

    @Override
    public @NotNull DataGUI<T> setBottomSide(GUIContent @NotNull ... contents) {
        return (DataGUI<T>) super.setBottomSide(contents);
    }

    @Override
    public @NotNull DataGUI<T> setBottomSide(@NotNull Collection<GUIContent> contents) {
        return (DataGUI<T>) super.setBottomSide(contents);
    }

    @Override
    public @NotNull DataGUI<T> unsetBottomSide() {
        return (DataGUI<T>) super.unsetBottomSide();
    }

    @Override
    public @NotNull DataGUI<T> setRightSide(Item @NotNull ... contents) {
        return (DataGUI<T>) super.setRightSide(contents);
    }

    @Override
    public @NotNull DataGUI<T> setRightSide(ItemGUIContent @NotNull ... contents) {
        return (DataGUI<T>) super.setRightSide(contents);
    }

    @Override
    public @NotNull DataGUI<T> setRightSide(GUIContent @NotNull ... contents) {
        return (DataGUI<T>) super.setRightSide(contents);
    }

    @Override
    public @NotNull DataGUI<T> setRightSide(@NotNull Collection<GUIContent> contents) {
        return (DataGUI<T>) super.setRightSide(contents);
    }

    @Override
    public @NotNull DataGUI<T> unsetRightSide() {
        return (DataGUI<T>) super.unsetRightSide();
    }

    @Override
    public @NotNull DataGUI<T> setNorthWest(Item @NotNull ... contents) {
        return (DataGUI<T>) super.setNorthWest(contents);
    }

    @Override
    public @NotNull DataGUI<T> setNorthWest(ItemGUIContent @NotNull ... contents) {
        return (DataGUI<T>) super.setNorthWest(contents);
    }

    @Override
    public @NotNull DataGUI<T> setNorthWest(GUIContent @NotNull ... contents) {
        return (DataGUI<T>) super.setNorthWest(contents);
    }

    @Override
    public @NotNull DataGUI<T> unsetNorthWest() {
        return (DataGUI<T>) super.unsetNorthWest();
    }

    @Override
    public @NotNull DataGUI<T> setNorth(Item @NotNull ... contents) {
        return (DataGUI<T>) super.setNorth(contents);
    }

    @Override
    public @NotNull DataGUI<T> setNorth(ItemGUIContent @NotNull ... contents) {
        return (DataGUI<T>) super.setNorth(contents);
    }

    @Override
    public @NotNull DataGUI<T> setNorth(GUIContent @NotNull ... contents) {
        return (DataGUI<T>) super.setNorth(contents);
    }

    @Override
    public @NotNull DataGUI<T> unsetNorth() {
        return (DataGUI<T>) super.unsetNorth();
    }

    @Override
    public @NotNull DataGUI<T> setNorthEast(Item @NotNull ... contents) {
        return (DataGUI<T>) super.setNorthEast(contents);
    }

    @Override
    public @NotNull DataGUI<T> setNorthEast(ItemGUIContent @NotNull ... contents) {
        return (DataGUI<T>) super.setNorthEast(contents);
    }

    @Override
    public @NotNull DataGUI<T> setNorthEast(GUIContent @NotNull ... contents) {
        return (DataGUI<T>) super.setNorthEast(contents);
    }

    @Override
    public @NotNull DataGUI<T> unsetNorthEast() {
        return (DataGUI<T>) super.unsetNorthEast();
    }

    @Override
    public @NotNull DataGUI<T> setMiddleWest(Item @NotNull ... contents) {
        return (DataGUI<T>) super.setMiddleWest(contents);
    }

    @Override
    public @NotNull DataGUI<T> setMiddleWest(ItemGUIContent @NotNull ... contents) {
        return (DataGUI<T>) super.setMiddleWest(contents);
    }

    @Override
    public @NotNull DataGUI<T> setMiddleWest(GUIContent @NotNull ... contents) {
        return (DataGUI<T>) super.setMiddleWest(contents);
    }

    @Override
    public @NotNull DataGUI<T> unsetMiddleWest() {
        return (DataGUI<T>) super.unsetMiddleWest();
    }

    @Override
    public @NotNull DataGUI<T> setMiddle(Item @NotNull ... contents) {
        return (DataGUI<T>) super.setMiddle(contents);
    }

    @Override
    public @NotNull DataGUI<T> setMiddle(ItemGUIContent @NotNull ... contents) {
        return (DataGUI<T>) super.setMiddle(contents);
    }

    @Override
    public @NotNull DataGUI<T> setMiddle(GUIContent @NotNull ... contents) {
        return (DataGUI<T>) super.setMiddle(contents);
    }

    @Override
    public @NotNull DataGUI<T> unsetMiddle() {
        return (DataGUI<T>) super.unsetMiddle();
    }

    @Override
    public @NotNull DataGUI<T> setMiddleEast(Item @NotNull ... contents) {
        return (DataGUI<T>) super.setMiddleEast(contents);
    }

    @Override
    public @NotNull DataGUI<T> setMiddleEast(ItemGUIContent @NotNull ... contents) {
        return (DataGUI<T>) super.setMiddleEast(contents);
    }

    @Override
    public @NotNull DataGUI<T> setMiddleEast(GUIContent @NotNull ... contents) {
        return (DataGUI<T>) super.setMiddleEast(contents);
    }

    @Override
    public @NotNull DataGUI<T> unsetMiddleEast() {
        return (DataGUI<T>) super.unsetMiddleEast();
    }

    @Override
    public @NotNull DataGUI<T> setSouthWest(Item @NotNull ... contents) {
        return (DataGUI<T>) super.setSouthWest(contents);
    }

    @Override
    public @NotNull DataGUI<T> setSouthWest(ItemGUIContent @NotNull ... contents) {
        return (DataGUI<T>) super.setSouthWest(contents);
    }

    @Override
    public @NotNull DataGUI<T> setSouthWest(GUIContent @NotNull ... contents) {
        return (DataGUI<T>) super.setSouthWest(contents);
    }

    @Override
    public @NotNull DataGUI<T> unsetSouthWest() {
        return (DataGUI<T>) super.unsetSouthWest();
    }

    @Override
    public @NotNull DataGUI<T> setSouth(Item @NotNull ... contents) {
        return (DataGUI<T>) super.setSouth(contents);
    }

    @Override
    public @NotNull DataGUI<T> setSouth(ItemGUIContent @NotNull ... contents) {
        return (DataGUI<T>) super.setSouth(contents);
    }

    @Override
    public @NotNull DataGUI<T> setSouth(GUIContent @NotNull ... contents) {
        return (DataGUI<T>) super.setSouth(contents);
    }

    @Override
    public @NotNull DataGUI<T> unsetSouth() {
        return (DataGUI<T>) super.unsetSouth();
    }

    @Override
    public @NotNull DataGUI<T> setSouthEast(Item @NotNull ... contents) {
        return (DataGUI<T>) super.setSouthEast(contents);
    }

    @Override
    public @NotNull DataGUI<T> setSouthEast(ItemGUIContent @NotNull ... contents) {
        return (DataGUI<T>) super.setSouthEast(contents);
    }

    @Override
    public @NotNull DataGUI<T> setSouthEast(GUIContent @NotNull ... contents) {
        return (DataGUI<T>) super.setSouthEast(contents);
    }

    @Override
    public @NotNull DataGUI<T> unsetSouthEast() {
        return (DataGUI<T>) super.unsetSouthEast();
    }

    @Override
    public @NotNull DataGUI<T> fill(@NotNull Item content) {
        return (DataGUI<T>) super.fill(content);
    }

    @Override
    public @NotNull DataGUI<T> fill(@NotNull ItemGUIContent content) {
        return (DataGUI<T>) super.fill(content);
    }

    @Override
    public @NotNull DataGUI<T> fill(@NotNull GUIContent content) {
        return (DataGUI<T>) super.fill(content);
    }

    @Override
    public @NotNull DataGUI<T> clear() {
        return (DataGUI<T>) super.clear();
    }

    @Override
    public @NotNull DataGUI<T> onClickOutside(@NotNull String command) {
        return (DataGUI<T>) super.onClickOutside(command);
    }

    @Override
    public @NotNull DataGUI<T> onOpenGUI(@NotNull String command) {
        return (DataGUI<T>) super.onOpenGUI(command);
    }

    @Override
    public @NotNull DataGUI<T> onCloseGUI(@NotNull String command) {
        return (DataGUI<T>) super.onCloseGUI(command);
    }

    @Override
    public @NotNull DataGUI<T> onChangeGUI(@NotNull String command) {
        return (DataGUI<T>) super.onChangeGUI(command);
    }

    @Override
    public @NotNull DataGUI<T> setVariable(@NotNull String name, @NotNull String value) {
        return (DataGUI<T>) super.setVariable(name, value);
    }

    @Override
    public @NotNull DataGUI<T> unsetVariable(@NotNull String name) {
        return (DataGUI<T>) super.unsetVariable(name);
    }

    @Override
    public @NotNull DataGUI<T> copyAll(@NotNull GUI other, boolean replace) {
        return (DataGUI<T>) super.copyAll(other, replace);
    }

    @Override
    public @NotNull DataGUI<T> copyFrom(@NotNull GUI other, boolean replace) {
        return (DataGUI<T>) super.copyFrom(other, replace);
    }

    @Override
    public DataGUI<T> copy() {
        return (DataGUI<T>) super.copy();
    }

    @Override
    public @NotNull DataGUI<T> copyAll(@NotNull Metadatable other, boolean replace) {
        return (DataGUI<T>) super.copyAll(other, replace);
    }

    @Override
    public @NotNull DataGUI<T> copyFrom(@NotNull Metadatable other, boolean replace) {
        return (DataGUI<T>) super.copyFrom(other, replace);
    }

    /**
     * Creates a new {@link DataGUI} with the given size and converter.
     *
     * @param <T>           the type of the data
     * @param size          the size
     * @param dataConverter the data converter
     * @return the data gui
     */
    public static <T> @NotNull DataGUI<T> newGUI(final int size, final @NotNull Function<T, GUIContent> dataConverter) {
        return new DataGUI<>(size, dataConverter);
    }

    /**
     * Creates a new {@link DataGUI} with the given size, converter and data.
     *
     * @param <T>           the type of the data
     * @param size          the size
     * @param dataConverter the data converter
     * @param data          the data
     * @return the data gui
     */
    @SafeVarargs
    public static <T> @NotNull DataGUI<T> newGUI(final int size, final @NotNull Function<T, GUIContent> dataConverter,
                                        final T @NotNull ... data) {
        return new DataGUI<>(size, dataConverter).setData(data);
    }

    /**
     * Creates a new {@link DataGUI} with the given size, converter and data.
     *
     * @param <T>           the type of the data
     * @param size          the size
     * @param dataConverter the data converter
     * @param data          the data
     * @return the data gui
     */
    public static <T> @NotNull DataGUI<T> newGUI(final int size, final @NotNull Function<T, GUIContent> dataConverter,
                                        final @NotNull Collection<T> data) {
        return new DataGUI<>(size, dataConverter).setData(data);
    }

    /**
     * Creates a new {@link DataGUI} with the given type and converter.
     *
     * @param <T>           the type of the data
     * @param type          the type
     * @param dataConverter the data converter
     * @return the data gui
     */
    public static <T> @NotNull DataGUI<T> newGUI(final @NotNull GUIType type, final @NotNull Function<T, GUIContent> dataConverter) {
        return new DataGUI<>(type, dataConverter);
    }

    /**
     * Creates a new {@link DataGUI} with the given type, converter and data.
     *
     * @param <T>           the type of the data
     * @param type          the type
     * @param dataConverter the data converter
     * @param data          the data
     * @return the data gui
     */
    @SafeVarargs
    public static <T> @NotNull DataGUI<T> newGUI(final @NotNull GUIType type, final @NotNull Function<T, GUIContent> dataConverter,
                                        final T @NotNull ... data) {
        return new DataGUI<>(type, dataConverter).setData(data);
    }

    /**
     * Creates a new {@link DataGUI} with the given type, converter and data.
     *
     * @param <T>           the type of the data
     * @param type          the type
     * @param dataConverter the data converter
     * @param data          the data
     * @return the data gui
     */
    public static <T> @NotNull DataGUI<T> newGUI(final @NotNull GUIType type, final @NotNull Function<T, GUIContent> dataConverter,
                                        final @NotNull Collection<T> data) {
        return new DataGUI<>(type, dataConverter).setData(data);
    }

}
