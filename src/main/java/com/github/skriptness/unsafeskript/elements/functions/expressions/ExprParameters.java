package com.github.skriptness.unsafeskript.elements.functions.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.function.Parameter;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.github.skriptness.unsafeskript.elements.functions.classes.handles.FunctionHandle;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.skriptlang.skript.lang.comparator.Comparators;
import org.skriptlang.skript.lang.comparator.Relation;

import java.util.Arrays;

@Name("Function Parameters")
@Description("Returns a list of all the parameters of a function. This list can be modified.")
@Examples("clear parameters of function \"floor\" # >:)")
@Since("1.0-alpha1, INSERT VERSION (add, remove)")
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
            case ADD:
            case REMOVE:
            case DELETE:
                return CollectionUtils.array(Parameter[].class);
        }
        return null;
    }

    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        switch (mode) {
            case SET:
            case DELETE:
                for (FunctionHandle<?> function : getExpr().getArray(event)) {
                    function.setParameters((Parameter<?>[]) delta);
                }
                break;
            case ADD:
                for (FunctionHandle<?> function : getExpr().getArray(event)) {
                    Parameter<?>[] oldParams = function.getFunction().getParameters();
                    Parameter<?>[] newParams = Arrays.copyOf(oldParams, oldParams.length + delta.length);
                    System.arraycopy((Parameter<?>[]) delta, 0, newParams, oldParams.length, delta.length);
                    function.setParameters(newParams);
                }
                break;
            case REMOVE:
                for (FunctionHandle<?> function : getExpr().getArray(event)) {
                    Parameter<?>[] parameters = function.getFunction().getParameters();
                    Parameter<?>[] newParameters = new Parameter[parameters.length];
                    int index = 0;
                    outer: for (Parameter<?> parameter : parameters) {
                        for (Parameter<?> toRemove : (Parameter<?>[]) delta) {
                            if (Comparators.compare(parameter, toRemove) == Relation.EQUAL)
                                continue outer;
                        }
                        newParameters[index++] = parameter;
                    }
                    function.setParameters(Arrays.copyOfRange(newParameters, 0, index));
                }
                break;
        }
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
        return "parameters of " + getExpr().toString(event, debug);
    }

}
