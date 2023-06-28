package com.github.skriptness.unsafeskript.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.lang.*;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.function.Parameter;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

public class ExprNewParameter extends SimpleExpression<Parameter> {

    static {
        Skript.registerExpression(ExprNewParameter.class, Parameter.class, ExpressionType.COMBINED,
                "[a] [new] %*classinfo% parameter %~objects% [(with default value|defaulting to) %-objects%]");
    }

    private VariableString name;
    private ClassInfo<?> type;
    private boolean single;
    private Expression<?> defaultExpr;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!(exprs[1] instanceof Variable))
            return false;
        name = ((Variable<?>) exprs[1]).getName();
        type = ((Literal<ClassInfo<?>>) exprs[0]).getSingle();
        return true;
    }

    @Override
    @Nullable
    protected Parameter[] get(Event event) {
        return new Parameter[] {new Parameter(name.getSingle(event), type, single, defaultExpr)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Parameter> getReturnType() {
        return Parameter.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return null;
    }

}
