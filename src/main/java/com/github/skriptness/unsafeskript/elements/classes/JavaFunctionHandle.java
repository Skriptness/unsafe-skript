package com.github.skriptness.unsafeskript.elements.classes;

import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.function.Function;
import ch.njol.skript.lang.function.JavaFunction;

public class JavaFunctionHandle<T> implements FunctionHandle<T> {

    private final JavaFunction<T> function;

    public JavaFunctionHandle(Function<T> function) {
        if (!(function instanceof JavaFunction))
            throw new IllegalArgumentException("Function is not a JavaFunction");
        this.function = (JavaFunction<T>) function;
    }

    public JavaFunction<T> getFunction() {
        return function;
    }

    @Override
    public void swapCode(Trigger trigger) {
    }

}
