package com.github.skriptness.unsafeskript.elements.functions.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import com.github.skriptness.unsafeskript.elements.functions.classes.handles.FunctionHandle;

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