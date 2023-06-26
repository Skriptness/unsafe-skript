package com.github.skriptness.unsafeskript.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.function.Functions;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.LiteralUtils;
import ch.njol.util.Kleenean;
import com.github.skriptness.unsafeskript.elements.classes.FunctionHandle;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

public class ExprFunction extends SimpleExpression<FunctionHandle> {

    static {
        Skript.registerExpression(ExprFunction.class, FunctionHandle.class, ExpressionType.COMBINED,
                "[the] function[ reference][s] %strings% [(with|using) [(argument|parameter)[s]] %-objects%]");
    }

    private Expression<String> names;
    private Expression<Object> rawParameters;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        names = (Expression<String>) exprs[0];
        if (exprs[1] != null) {
            rawParameters = LiteralUtils.defendExpression(exprs[1]);
            return LiteralUtils.canInitSafely(this.rawParameters);
        }
        return true;
    }

    @Override
    @Nullable
    protected FunctionHandle[] get(Event event) {
        return names.stream(event)
                .filter(name -> Functions.getFunction(name, null) != null)
                .map(name -> new FunctionHandle(Functions.getFunction(name, null), rawParameters))
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
        StringBuilder string = new StringBuilder();
        string.append("the function")
                .append(isSingle() ? "s " : " ")
                .append(names.toString(event, debug));
        if (rawParameters != null)
            string.append(" with parameters ")
                    .append(rawParameters.toString(event, debug));
        return string.toString();
    }

}
