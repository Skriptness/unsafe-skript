package com.github.skriptness.unsafeskript.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.github.skriptness.unsafeskript.elements.classes.FunctionHandle;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

public class EffExecuteFunction extends Effect {

    static {
        Skript.registerEffect(EffExecuteFunction.class, "(call|execute|run) [function[s]] %functions%");
    }

    private Expression<? extends FunctionHandle> functions;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        functions = (Expression<? extends FunctionHandle>) exprs[0];
        return true;
    }

    @Override
    protected void execute(Event event) {
        functions.stream(event).forEach(function -> function.execute(event));
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "execute " + functions.toString(event, debug);
    }

}
