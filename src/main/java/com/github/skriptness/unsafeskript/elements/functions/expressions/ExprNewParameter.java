package com.github.skriptness.unsafeskript.elements.functions.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.lang.*;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.function.Parameter;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.LiteralUtils;
import ch.njol.skript.util.Utils;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

public class ExprNewParameter extends SimpleExpression<Parameter> {

    static {
        Skript.registerExpression(ExprNewParameter.class, Parameter.class, ExpressionType.COMBINED,
                "[a] [new] %*classinfo% parameter %~objects% [(with default value|defaulting to) %-objects%]");
    }

    private ClassInfo<?> type;
    private Variable<?> variable;
    private boolean single;
    @Nullable
    private Expression<?> defaultExpr;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        if (!(exprs[1] instanceof Variable))
            return false;
        variable = (Variable<?>) exprs[1];
        if (!variable.isLocal()) {
            Skript.error("The parameter must be stored in a local variable");
            return false;
        }
        UnparsedLiteral unparsed = (UnparsedLiteral) exprs[0].getSource();
        if ((single = variable.isSingle()) && Utils.getEnglishPlural(unparsed.getData()).getSecond()) {
            Skript.error("A list type cannot be stored in a single variable");
            return false;
        }
        type = ((Literal<ClassInfo<?>>) exprs[0]).getSingle();
        if (exprs[2] != null) {
            defaultExpr = LiteralUtils.defendExpression(exprs[2]);
            return LiteralUtils.canInitSafely(defaultExpr);
        }
        return true;
    }

    @Override
    @Nullable
    protected Parameter[] get(Event event) {
        String name = variable.getName().toUnformattedString(event);
        if (!single)
            name = name.substring(0, name.length() - (Variable.SEPARATOR + "*").length());
        return new Parameter[] {new Parameter(name, type, single, defaultExpr)};
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
    public String toString(@Nullable Event event, boolean debug) {
        return "a new " + type.getName().toString(!single) + " parameter " + variable.toString(event, debug) +
                (defaultExpr == null ? "" : " defaulting to " + defaultExpr.toString(event, debug));
    }

}
