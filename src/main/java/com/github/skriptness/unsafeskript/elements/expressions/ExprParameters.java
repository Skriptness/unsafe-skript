package com.github.skriptness.unsafeskript.elements.expressions;

import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.function.Parameter;
import ch.njol.util.Kleenean;
import com.github.skriptness.unsafeskript.elements.classes.FunctionHandle;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import java.util.Arrays;

public class ExprParameters extends PropertyExpression<FunctionHandle, Parameter> {

    static {
        register(ExprParameters.class, Parameter.class, "parameters", "functions");
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        setExpr((Expression<? extends FunctionHandle<?>>) exprs[0]);
        return true;
    }

    @Override
    protected Parameter<?>[] get(Event event, FunctionHandle[] source) {
        return Arrays.stream(source)
                .map(function -> function.getFunction().getParameters())
                .flatMap(Arrays::stream)
                .toArray(Parameter[]::new);
    }

    @Override
    public Class<? extends Parameter> getReturnType() {
        return Parameter.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "all of the parameters of " + getExpr().toString(event, debug);
    }

}
