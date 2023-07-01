package com.github.skriptness.unsafeskript.elements.functions.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import com.github.skriptness.unsafeskript.elements.functions.classes.handles.FunctionHandle;

@Name("Is Local")
@Description("Checks whether a function is local.")
@Examples("set {_functions::*} to all functions where [function input is local]")
@Since("1.0-alpha1")
public class CondIsLocal extends PropertyCondition<FunctionHandle<?>> {

    static {
        register(CondIsLocal.class, "local", "functions");
    }

    @Override
    public boolean check(FunctionHandle<?> handle) {
        return handle.getFunction().getSignature().isLocal();
    }

    @Override
    protected String getPropertyName() {
        return "local";
    }

}
