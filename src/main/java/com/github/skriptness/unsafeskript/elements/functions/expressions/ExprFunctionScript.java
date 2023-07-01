package com.github.skriptness.unsafeskript.elements.functions.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.github.skriptness.unsafeskript.elements.functions.classes.handles.FunctionHandle;
import com.github.skriptness.unsafeskript.elements.functions.classes.handles.ScriptFunctionHandle;
import org.eclipse.jdt.annotation.Nullable;

@Name("Function Script")
@Description("Returns the declaring script of a given function.")
@Examples("send \"Function %{_function}% was defined in %script of {_function}%\"")
@Since("INSERT VERSION")
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
