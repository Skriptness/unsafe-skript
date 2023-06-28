package com.github.skriptness.unsafeskript.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.github.skriptness.unsafeskript.elements.classes.FunctionHandle;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

public class ExprFunction extends SimpleExpression<FunctionHandle> {

    static {
        Skript.registerExpression(ExprFunction.class, FunctionHandle.class, ExpressionType.COMBINED,
                "[the] function[ reference][s] %strings%",
                "[the|a] reference[s] to [the] function[s] %strings%");
    }

    @SuppressWarnings("NotNullFieldNotInitialized")
    private Expression<String> names;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        names = (Expression<String>) exprs[0];
        return true;
    }

    @Override
    @Nullable
    protected FunctionHandle<?>[] get(Event event) {
        return names.stream(event)
                .map(FunctionHandle::of)
                .toArray(FunctionHandle[]::new);
    }

    @Override
    public boolean isSingle() {
        return names.isSingle();
    }

    @Override
    public Class<? extends FunctionHandle> getReturnType() {
        return FunctionHandle.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "the function" + (isSingle() ? " " : "s ") + names.toString(event, debug);
    }

}
