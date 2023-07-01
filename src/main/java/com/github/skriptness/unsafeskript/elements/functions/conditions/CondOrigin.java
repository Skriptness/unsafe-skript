package com.github.skriptness.unsafeskript.elements.functions.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.github.skriptness.unsafeskript.elements.functions.classes.handles.FunctionHandle;
import com.github.skriptness.unsafeskript.elements.functions.classes.handles.JavaFunctionHandle;
import com.github.skriptness.unsafeskript.elements.functions.classes.handles.ScriptFunctionHandle;

@Name("Is Java/Script Function")
@Description({"Checks whether a function is a Java function or a script function."})
@Examples("{_function} is a script function")
@Since("1.0-alpha1")
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
