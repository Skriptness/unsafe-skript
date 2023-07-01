package com.github.skriptness.unsafeskript.elements.functions.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.effects.Delay;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.function.Function;
import ch.njol.skript.lang.function.FunctionEvent;
import ch.njol.skript.lang.function.ScriptFunction;
import ch.njol.skript.sections.SecLoop;
import ch.njol.skript.sections.SecWhile;
import ch.njol.skript.util.LiteralUtils;
import ch.njol.util.Kleenean;
import com.github.skriptness.unsafeskript.elements.functions.classes.handles.JavaFunctionHandle.DelegatingJavaFunction;
import com.github.skriptness.unsafeskript.elements.functions.classes.handles.JavaFunctionHandle;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.skriptlang.skript.lang.converter.Converters;

public class EffReturn extends Effect {

    static {
        Skript.registerEffect(EffReturn.class, "return %objects%");
    }

    private Expression<?> value;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!getParser().isCurrentEvent(FunctionEvent.class)) {
            Skript.error("The return statement can only be used in a function");
            return false;
        }
        if (!isDelayed.isFalse()) {
            Skript.error("A return statement after a delay is useless, as the calling trigger will resume when the delay starts (and won't get any returned value)");
            // Do not return false otherwise Skript's EffReturn will be attempted and will throw the wrong error
            return true;
        }
        value = LiteralUtils.defendExpression(exprs[0]);
        return LiteralUtils.canInitSafely(value);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected TriggerItem walk(Event event) {
        FunctionEvent<?> evt = (FunctionEvent<?>) event;
        Function<?> function = evt.getFunction();

        if (function.getReturnType() != null && !Delay.isDelayed(event)) {
            Object[] result = Converters.convert(this.value.getArray(event), function.getReturnType().getC());
            if (function instanceof ScriptFunction<?>) {
                ((ScriptFunction) function).setReturnValue(result);
            } else if (function instanceof JavaFunctionHandle.DelegatingJavaFunction) {
                ((DelegatingJavaFunction) function).setReturnValue(result);
            }
        }

        // Exit all loops
        while (parent != null) {
            if (parent instanceof SecLoop) {
                ((SecLoop) parent).exit(event);
            } else if (parent instanceof SecWhile) {
                ((SecWhile) parent).reset();
            }
            parent = parent.getParent();
        }

        return null;
    }

    @Override
    protected void execute(Event event) {
        // Shouldn't be called
        throw new UnsupportedOperationException();
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public String toString(@Nullable Event event, boolean debug) {
        if (debug)
            return "(swapped) return " + value.toString(event, debug);
        return "return " + value.toString(event, debug);
    }

}
