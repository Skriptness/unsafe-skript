package com.github.skriptness.unsafeskript.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.LiteralUtils;
import ch.njol.util.Kleenean;
import com.github.skriptness.unsafeskript.elements.classes.FunctionHandle;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

public class EffExecuteFunction extends Effect {

    static {
        Skript.registerEffect(EffExecuteFunction.class,
                "(call|execute|run) %functions% [with [[the] (argument|parameter)[s]] %-objects%]");
    }

    @SuppressWarnings("NotNullFieldNotInitialized")
    private Expression<? extends FunctionHandle<?>> functions;
    @Nullable
    private Expression<?> arguments;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        functions = (Expression<? extends FunctionHandle<?>>) exprs[0];
        if (exprs[1] != null) {
            arguments = LiteralUtils.defendExpression(exprs[1]);
            return LiteralUtils.canInitSafely(arguments);
        }
        return true;
    }

    @Override
    protected void execute(Event event) {
        for (FunctionHandle<?> function : functions.getArray(event))
            function.execute(event, arguments);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "execute " + functions.toString(event, debug) +
               (arguments == null ? "" : " with the arguments " + arguments.toString(event, debug));
    }

}
