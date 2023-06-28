package com.github.skriptness.unsafeskript.elements.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Section;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.function.FunctionEvent;
import ch.njol.util.Kleenean;
import com.github.skriptness.unsafeskript.elements.classes.FunctionHandle;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import java.util.List;

public class SecSwapFunctionCode extends Section {

    static {
        Skript.registerSection(SecSwapFunctionCode.class,
                "(swap|set|overwrite|override) [the] code of %function%",
                "(swap|set|overwrite|override) %function%'s code",
                "(overwrite|override) %function%");
    }

    private Expression<? extends FunctionHandle<?>> function;
    private Trigger trigger;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult,
                        SectionNode sectionNode, List<TriggerItem> triggerItems) {
        function = (Expression<? extends FunctionHandle<?>>) exprs[0];
        trigger = loadCode(sectionNode, "swap function code", FunctionEvent.class);
        return true;
    }

    @Override
    @Nullable
    protected TriggerItem walk(Event event) {
        FunctionHandle<?> handle = function.getSingle(event);
        if (handle != null)
            handle.swapCode(trigger);
        return getNext();
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return "swap code of " + function.toString(e, debug);
    }

}
