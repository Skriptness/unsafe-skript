package com.github.skriptness.unsafeskript.elements.functions.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.function.Parameter;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.github.skriptness.unsafeskript.elements.functions.classes.handles.FunctionHandle;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import java.util.Arrays;

public class ExprParameters extends PropertyExpression<FunctionHandle<?>, Parameter> {

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
    protected Parameter<?>[] get(Event event, FunctionHandle<?>[] source) {
        return Arrays.stream(source)
                .map(function -> function.getFunction().getParameters())
                .flatMap(Arrays::stream)
                .toArray(Parameter[]::new);
    }

    @Override
    @Nullable
    public Class<?>[] acceptChange(ChangeMode mode) {
        switch (mode) {
            case SET:
            case DELETE:
                return CollectionUtils.array(Parameter[].class);
        }
        return null;
    }

    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        delta = delta == null ? new Parameter<?>[0] : delta;
        for (FunctionHandle<?> function : getExpr().getArray(event))
            function.setParameters((Parameter<?>[]) delta);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends Parameter> getReturnType() {
        return Parameter.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "all the parameters of " + getExpr().toString(event, debug);
    }

}
