package com.github.skriptness.unsafeskript.elements.functions.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.github.skriptness.unsafeskript.elements.functions.classes.handles.FunctionHandle;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

@Name("Function")
@Description("Returns a reference to the provided function.")
@Examples("run (function \"hello_there\" from \"bob.sk\")")
@Since("1.0-alpha1")
public class ExprFunction extends SimpleExpression<FunctionHandle> {

    static {
        Skript.registerExpression(ExprFunction.class, FunctionHandle.class, ExpressionType.COMBINED,
                "[the] [:global] function[ reference][s] %strings% [(in|from) [script] [file] %-string%]",
                "[the|a] reference[s] to [the] [:global] function[s] %strings% [(in|from) [script] [file] %-string%]");
    }

    private Expression<String> names;
    @Nullable
    private Expression<String> script;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        names = (Expression<String>) exprs[0];
        if (exprs[1] != null) {
            if (parseResult.hasTag("global")) {
                Skript.error("A global function cannot be referenced from a specific script");
                return false;
            }
            script = (Expression<String>) exprs[1];
        }
        return true;
    }

    @Override
    @Nullable
    protected FunctionHandle<?>[] get(Event event) {
        String rawFile = script != null ? script.getSingle(event) : null;
        String file = ((rawFile != null) && !rawFile.endsWith(".sk")) ? (rawFile + ".sk") : rawFile;
        return names.stream(event)
                .map(name -> FunctionHandle.of(name, file))
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
        return "the function" + (isSingle() ? " " : "s ") + names.toString(event, debug) +
               (script != null ? (" from script " + script.toString(event, debug)) : "");
    }

}
