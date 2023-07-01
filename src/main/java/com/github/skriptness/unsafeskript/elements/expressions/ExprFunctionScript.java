package com.github.skriptness.unsafeskript.elements.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.github.skriptness.unsafeskript.elements.classes.FunctionHandle;
import com.github.skriptness.unsafeskript.elements.classes.ScriptFunctionHandle;
import org.eclipse.jdt.annotation.Nullable;

public class ExprFunctionScript extends SimplePropertyExpression<FunctionHandle<?>, String> {

    static {
        register(ExprFunctionScript.class, String.class, "[declaring] script", "functions");
    }

    @Override
    @Nullable
    public String convert(FunctionHandle<?> handle) {
        if (!(handle instanceof ScriptFunctionHandle<?>))
            return null;
        return ((ScriptFunctionHandle<?>) handle).getScript();
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    protected String getPropertyName() {
        return "script";
    }

}
