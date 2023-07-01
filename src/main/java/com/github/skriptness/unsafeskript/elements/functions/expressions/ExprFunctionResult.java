package com.github.skriptness.unsafeskript.elements.functions.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.LiteralUtils;
import ch.njol.util.Kleenean;
import com.github.skriptness.unsafeskript.elements.functions.classes.handles.FunctionHandle;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import java.util.Arrays;
import java.util.Objects;

public class ExprFunctionResult extends PropertyExpression<FunctionHandle<?>, Object> {

    static {
        Skript.registerExpression(ExprFunctionResult.class, Object.class, ExpressionType.PROPERTY,
                "[the] [call|execution] result[s] of %functions% [with [[the] (argument|parameter)[s]] %-objects%]");
    }

    @Nullable
    private Expression<?> arguments;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        setExpr((Expression<? extends FunctionHandle<?>>) exprs[0]);
        if (exprs[1] != null) {
            arguments = LiteralUtils.defendExpression(exprs[1]);
            return LiteralUtils.canInitSafely(arguments);
        }
        return true;
    }

    @Override
    protected Object[] get(Event event, FunctionHandle<?>[] source) {
        return Arrays.stream(source)
                .map(handle -> handle.execute(event, arguments))
                .filter(Objects::nonNull)
                .flatMap(Arrays::stream)
                .toArray(Object[]::new);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<?> getReturnType() {
        return Object.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "the result of " + getExpr().toString(event, debug) +
               (arguments == null ? "" : " with the arguments " + arguments.toString(event, debug));
    }

}
