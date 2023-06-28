package com.github.skriptness.unsafeskript.elements.classes;

import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.function.Function;
import ch.njol.skript.lang.function.FunctionEvent;
import ch.njol.skript.lang.function.FunctionReference;
import ch.njol.skript.lang.function.Functions;
import ch.njol.skript.lang.function.JavaFunction;
import ch.njol.skript.lang.function.Namespace;
import ch.njol.skript.lang.function.Parameter;
import ch.njol.skript.lang.function.Signature;
import ch.njol.skript.variables.Variables;
import org.eclipse.jdt.annotation.Nullable;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

public class JavaFunctionHandle<T> implements FunctionHandle<T> {

    private static final Namespace javaNamespace;
    private static final Map<String, Namespace> globalFunctions;
    private static final Field CALLS;
    private static final Field REFERENCE_FUNCTION;

    static {
        try {
            // Obtain Namespace of JavaFunctions
            Field javaNamespace0 = Functions.class.getDeclaredField("javaNamespace");
            javaNamespace0.setAccessible(true);
            javaNamespace = (Namespace) javaNamespace0.get(null);

            // Obtain Map of all global Functions
            Field globalFunctions0 = Functions.class.getDeclaredField("globalFunctions");
            globalFunctions0.setAccessible(true);
            //noinspection unchecked
            globalFunctions = (Map<String, Namespace>) globalFunctions0.get(null);

            CALLS = Signature.class.getDeclaredField("calls");
            CALLS.setAccessible(true);
            REFERENCE_FUNCTION = FunctionReference.class.getDeclaredField("function");
            REFERENCE_FUNCTION.setAccessible(true);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private JavaFunction<T> function;

    public JavaFunctionHandle(Function<T> function) {
        if (!(function instanceof JavaFunction))
            throw new IllegalArgumentException("Function is not a JavaFunction");
        this.function = (JavaFunction<T>) function;
    }

    public JavaFunction<T> getFunction() {
        return function;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void swapCode(Trigger trigger) {
        // Function's code was already overwritten, so swap its Trigger directly
        if (function instanceof DelegatingJavaFunction) {
            ((DelegatingJavaFunction<T>) function).trigger = trigger;
            return;
        }

        JavaFunction<T> swappedFunction = new DelegatingJavaFunction<>(function.getSignature(), trigger);

        // Replace Function in static collections
        javaNamespace.addFunction(swappedFunction);
        globalFunctions.put(swappedFunction.getName(), javaNamespace);

        // Replace Function inside old FunctionReferences
        try {
            Collection<FunctionReference<?>> calls = (Collection<FunctionReference<?>>) CALLS.get(function.getSignature());
            for (FunctionReference<?> call : calls)
                REFERENCE_FUNCTION.set(call, swappedFunction);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        // Update this handle's Function
        function = swappedFunction;
    }

    public static class DelegatingJavaFunction<T> extends JavaFunction<T> {

        private Trigger trigger;
        @Nullable
        private T[] returnValue;

        public DelegatingJavaFunction(Signature<T> sign, Trigger trigger) {
            super(sign);
            this.trigger = trigger;
        }

        public void setReturnValue(T[] value) {
            returnValue = value;
        }

        @Override
        public boolean resetReturnValue() {
            returnValue = null;
            return true;
        }

        @Override
        @Nullable
        public T[] execute(FunctionEvent<?> event, Object[][] arguments) {
            Parameter<?>[] parameters = getSignature().getParameters();
            for (int i = 0; i < parameters.length; i++) {
                Parameter<?> param = parameters[i];
                Object[] value = arguments[i];
                if (param.isSingleValue() && value.length > 0) {
                    Variables.setVariable(param.getName(), value[0], event, true);
                } else {
                    for (int j = 0; j < value.length; j++) {
                        Variables.setVariable(param.getName() + "::" + (j + 1), value[j], event, true);
                    }
                }
            }
            trigger.execute(event);
            return returnValue;
        }

    }

}
