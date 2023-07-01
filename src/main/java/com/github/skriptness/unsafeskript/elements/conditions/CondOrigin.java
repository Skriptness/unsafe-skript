package com.github.skriptness.unsafeskript.elements.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.github.skriptness.unsafeskript.elements.classes.FunctionHandle;
import com.github.skriptness.unsafeskript.elements.classes.JavaFunctionHandle;
import com.github.skriptness.unsafeskript.elements.classes.ScriptFunctionHandle;

public class CondOrigin extends PropertyCondition<FunctionHandle<?>> {

    static {
        register(CondOrigin.class, "[a] (:java|script) function", "functions");
    }

    private boolean isJava;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        isJava = parseResult.hasTag("java");
        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }

    @Override
    public boolean check(FunctionHandle<?> handle) {
        return isJava ? handle instanceof JavaFunctionHandle : handle instanceof ScriptFunctionHandle;
    }

    @Override
    protected String getPropertyName() {
        return "a " + (isJava ? "java" : "script") + " function";
    }

}
