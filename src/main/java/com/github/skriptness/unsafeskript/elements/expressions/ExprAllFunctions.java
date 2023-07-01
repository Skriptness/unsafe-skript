package com.github.skriptness.unsafeskript.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.function.Function;
import ch.njol.skript.lang.function.Namespace;
import ch.njol.skript.lang.function.Namespace.Key;
import ch.njol.skript.lang.function.Namespace.Origin;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.github.skriptness.unsafeskript.elements.classes.FunctionHandle;
import com.github.skriptness.unsafeskript.util.Reflectness;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class ExprAllFunctions extends SimpleExpression<FunctionHandle> {

    static {
        Skript.registerExpression(ExprAllFunctions.class, FunctionHandle.class, ExpressionType.SIMPLE,
                "[the|all [[of] the]] [registered] [:java|:script] functions");
    }

    @Nullable
    private Origin origin;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (parseResult.hasTag("java")) {
            origin = Origin.JAVA;
        } else if (parseResult.hasTag("script")) {
            origin = Origin.SCRIPT;
        }
        return true;
    }

    @Override
    protected FunctionHandle<?>[] get(Event e) {
        List<FunctionHandle<?>> handles = new ArrayList<>();
        for (Entry<Key, Namespace> entry : Reflectness.getNamespaces().entrySet()) {
            if (origin != null && origin != entry.getKey().getOrigin())
                continue;
            for (Function<?> function : entry.getValue().getFunctions())
                handles.add(FunctionHandle.of(function));
        }
        return handles.toArray(new FunctionHandle[0]);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends FunctionHandle> getReturnType() {
        return FunctionHandle.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return "all functions";
    }

}
