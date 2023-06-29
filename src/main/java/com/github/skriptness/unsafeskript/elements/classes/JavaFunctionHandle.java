package com.github.skriptness.unsafeskript.elements.classes;

import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.function.Function;
import ch.njol.skript.lang.function.FunctionEvent;
import ch.njol.skript.lang.function.FunctionReference;
import ch.njol.skript.lang.function.JavaFunction;
import ch.njol.skript.lang.function.Namespace;
import ch.njol.skript.lang.function.Parameter;
import ch.njol.skript.lang.function.Signature;
import ch.njol.skript.variables.Variables;
import com.github.skriptness.unsafeskript.util.Reflectness;
import org.eclipse.jdt.annotation.Nullable;

import java.util.Collection;
import java.util.Map;

public class JavaFunctionHandle<T> implements FunctionHandle<T> {

    private static final Namespace javaNamespace;
    private static final Map<String, Namespace> globalFunctions;

    static {
        javaNamespace = Reflectness.getJavaNamespace();
        globalFunctions = Reflectness.getGlobalFunctions();
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
        Collection<FunctionReference<?>> calls = Reflectness.getCalls(function.getSignature());
        for (FunctionReference<?> call : calls)
            Reflectness.setFunction(call, swappedFunction);

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
