package com.github.skriptness.unsafeskript.elements.classes;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionList;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.function.Function;
import ch.njol.skript.lang.function.Functions;
import ch.njol.skript.lang.function.JavaFunction;
import ch.njol.skript.lang.function.Parameter;
import ch.njol.skript.lang.function.ScriptFunction;
import ch.njol.skript.lang.function.Signature;
import ch.njol.skript.registrations.Classes;
import org.bukkit.event.Event;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface FunctionHandle<T> {

    static FunctionHandle<?> of(String name) {
        if (Functions.getFunction(name, null) == null)
            return null;
        return of(Functions.getFunction(name, null));
    }

    static <T> FunctionHandle<T> of(Function<T> function) {
        if (function instanceof ScriptFunction<?>)
            return new ScriptFunctionHandle<>(function);
        else if (function instanceof JavaFunction<?>)
            return new JavaFunctionHandle<>(function);
        return null;
    }

    Function<T> getFunction();

    void swapCode(Trigger trigger);

    default T[] execute(Object[][] arguments) {
        Parameter<?>[] parameters = getFunction().getParameters();
        Object[][] args = arguments.length < parameters.length ? Arrays.copyOf(arguments, parameters.length) : arguments;

        // Check if there are null parameters without default values
        for (int i = 0; i < parameters.length; i++)
            if (args[i] == null && parameters[i].getDefaultExpression() == null)
                return null; // Abort on invalid function call

        T[] result = getFunction().execute(arguments);
        getFunction().resetReturnValue();
        return result;
    }

    default T[] execute(Event event, Expression<?> rawParameters) {
        // Split-up parameters
        Expression<?>[] parameters;
        if (rawParameters == null) {
            parameters = new Expression[0];
        } else if (rawParameters instanceof ExpressionList<?>) {
            parameters = ((ExpressionList<?>) rawParameters).getExpressions();
        } else {
            parameters = new Expression[] {rawParameters};
        }

        Signature<T> signature = getFunction().getSignature();
        boolean singleListParam =  signature.getMaxParameters() == 1 && !signature.getParameter(0).isSingleValue();

        // Prepare parameter values for calling
        Object[][] params = new Object[singleListParam ? 1 : parameters.length][];
        if (singleListParam && parameters.length > 1) { // All parameters to one list
            List<Object> l = new ArrayList<>();
            for (Expression<?> parameter : parameters)
                l.addAll(Arrays.asList(parameter.getArray(event)));
            params[0] = l.toArray();

            // Don't allow mutating across function boundary; same hack is applied to variables
            for (int i = 0; i < params[0].length; i++)
                params[0][i] = Classes.clone(params[0][i]);

        } else { // Use parameters in normal way
            for (int i = 0; i < parameters.length; i++) {
                Object[] array = parameters[i].getArray(event);
                params[i] = Arrays.copyOf(array, array.length);

                // Don't allow mutating across function boundary; same hack is applied to variables
                for (int j = 0; j < params[i].length; j++)
                    params[i][j] = Classes.clone(params[i][j]);

            }
        }
        return execute(params);
    }

}
