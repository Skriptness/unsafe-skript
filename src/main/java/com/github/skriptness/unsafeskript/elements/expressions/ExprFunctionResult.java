package com.github.skriptness.unsafeskript.elements.expressions;

import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.github.skriptness.unsafeskript.elements.classes.FunctionHandle;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import java.util.Arrays;

public class ExprFunctionResult extends PropertyExpression<FunctionHandle<?>, Object> {

    static {
        register(ExprFunctionResult.class, Object.class, "[function] [call|execution] result", "functions");
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        setExpr((Expression<? extends FunctionHandle<?>>) exprs[0]);
        return true;
    }

    @Override
    protected Object[] get(Event event, FunctionHandle<?>[] source) {
        return Arrays.stream(source)
                .map(handle -> handle.execute(event))
                .flatMap(Arrays::stream)
                .toArray(Object[]::new);
    }

    @Override
    public Class<?> getReturnType() {
        return Object.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "the result of " + getExpr().toString(event, debug);
    }

}
