package com.github.skriptness.unsafeskript.elements.classes;

import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.function.Function;
import ch.njol.skript.lang.function.ScriptFunction;
import com.github.skriptness.unsafeskript.util.Reflectness;

public class ScriptFunctionHandle<T> implements FunctionHandle<T> {

    private final ScriptFunction<T> function;
    private final String script;

    public ScriptFunctionHandle(Function<T> function) {
        if (!(function instanceof ScriptFunction))
            throw new IllegalArgumentException("Function is not a ScriptFunction");
        this.function = (ScriptFunction<T>) function;
        this.script = Reflectness.getScript(function.getSignature());
    }

    public ScriptFunction<T> getFunction() {
        return function;
    }

    public String getScript() {
        return script;
    }

    @Override
    public void swapCode(Trigger trigger) {
        Reflectness.setTrigger(function, trigger);
    }

}
