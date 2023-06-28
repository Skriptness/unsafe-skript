package com.github.skriptness.unsafeskript.elements.classes;

import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.function.Function;
import ch.njol.skript.lang.function.ScriptFunction;

import java.lang.reflect.Field;

public class ScriptFunctionHandle<T> implements FunctionHandle<T> {

    private static final Field TRIGGER;

    static {
        try {
            TRIGGER = ScriptFunction.class.getDeclaredField("trigger");
            TRIGGER.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private final ScriptFunction<T> function;

    public ScriptFunctionHandle(Function<T> function) {
        if (!(function instanceof ScriptFunction))
            throw new IllegalArgumentException("Function is not a ScriptFunction");
        this.function = (ScriptFunction<T>) function;
    }

    public ScriptFunction<T> getFunction() {
        return function;
    }

    @Override
    public void swapCode(Trigger trigger) {
        try {
            TRIGGER.set(function, trigger);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
