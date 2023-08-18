package com.github.skriptness.unsafeskript.elements.functions.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.function.Functions;
import ch.njol.util.Kleenean;
import com.github.skriptness.unsafeskript.elements.functions.classes.handles.FunctionHandle;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

@Name("Unregister Function")
@Description("Dynamically unregisters the given functions.")
@Examples("unregister function \"sum\"")
@Since("INSERT VERSION")
public class EffUnregisterFunction extends Effect {

    static {
        Skript.registerEffect(EffUnregisterFunction.class, "unregister %functions%");
    }

    private Expression<FunctionHandle<?>> functions;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.functions = (Expression<FunctionHandle<?>>) exprs[0];
        return true;
    }

    @Override
    protected void execute(Event event) {
        for (FunctionHandle<?> handle : functions.getArray(event)) {
            Functions.unregisterFunction(handle.getFunction().getSignature());
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "unregister " + functions.toString(event, debug);
    }

}
