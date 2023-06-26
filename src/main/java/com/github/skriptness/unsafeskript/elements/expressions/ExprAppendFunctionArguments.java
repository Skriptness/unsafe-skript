package com.github.skriptness.unsafeskript.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.LiteralUtils;
import ch.njol.util.Kleenean;
import com.github.skriptness.unsafeskript.elements.classes.FunctionHandle;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

public class ExprAppendFunctionArguments extends SimpleExpression<FunctionHandle> {

    static {
        Skript.registerExpression(ExprAppendFunctionArguments.class, FunctionHandle.class, ExpressionType.PATTERN_MATCHES_EVERYTHING,
                "%functions% (with|using) [(argument|parameter)[s]] %objects%");
    }

    private Expression<? extends FunctionHandle> functions;
    private Expression<Object> parameters;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        functions = (Expression<? extends FunctionHandle<?>>) exprs[0];
        if (functions instanceof ExprFunction)
            return false;
        parameters = LiteralUtils.defendExpression(exprs[0]);
        return LiteralUtils.canInitSafely(parameters);
    }

    @Override
    @Nullable
    protected FunctionHandle[] get(Event event) {
        return functions.stream(event)
                .filter(function -> !function.hasParameters())
                .map(function -> function.appendParameters(parameters))
                .toArray(FunctionHandle[]::new);
    }

    @Override
    public boolean isSingle() {
        return functions.isSingle();
    }

    @Override
    public Class<? extends FunctionHandle> getReturnType() {
        return FunctionHandle.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return functions.toString(event, debug) + " with parameters " + parameters.toString(event, debug);
    }

}
