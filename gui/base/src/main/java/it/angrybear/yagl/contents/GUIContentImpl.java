package it.angrybear.yagl.contents;

import it.angrybear.yagl.actions.GUIItemAction;
import it.angrybear.yagl.contents.requirements.RequirementChecker;
import it.angrybear.yagl.items.Item;
import it.angrybear.yagl.utils.ObjectUtils;
import it.angrybear.yagl.viewers.Viewer;
import it.angrybear.yagl.wrappers.Sound;
import it.fulminazzo.fulmicollection.objects.FieldEquable;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A basic implementation for {@link GUIContent}.
 */
abstract class GUIContentImpl extends FieldEquable implements GUIContent {
    @Getter
    protected int priority = 0;
    @Getter
    protected Sound clickSound;
    protected RequirementChecker requirements;
    protected GUIItemAction clickAction;
    protected final Map<String, String> variables = new HashMap<>();

    @Override
    public @NotNull Item render() {
        return apply(internalRender().copy());
    }

    /**
     * Executes the real {@link #render()} function.
     *
     * @return the item
     */
    protected abstract @NotNull Item internalRender();

    @Override
    public @NotNull GUIContent setPriority(int priority) {
        this.priority = priority;
        return this;
    }

    @Override
    public @NotNull GUIContent setClickSound(Sound sound) {
        this.clickSound = sound;
        return this;
    }

    @Override
    public @NotNull GUIContent setViewRequirements(@NotNull RequirementChecker requirements) {
        this.requirements = requirements;
        return this;
    }

    @Override
    public boolean hasViewRequirements(@NotNull Viewer viewer) {
        return this.requirements == null || this.requirements.test(viewer);
    }

    @Override
    public @NotNull GUIContent onClickItem(@NotNull GUIItemAction action) {
        this.clickAction = action;
        return this;
    }

    @Override
    public @NotNull Map<String, String> variables() {
        return this.variables;
    }

    @Override
    public @NotNull Optional<GUIItemAction> clickItemAction() {
        return Optional.of((v, g, c) -> {
            if (this.clickSound != null) v.playSound(this.clickSound);
            if (this.clickAction != null) this.clickAction.execute(v, g, c);
        });
    }

    @Override
    public @NotNull String toString() {
        return ObjectUtils.printAsJSON(this);
    }
}
