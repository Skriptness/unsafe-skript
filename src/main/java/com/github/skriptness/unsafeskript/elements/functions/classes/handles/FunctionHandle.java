package com.github.skriptness.unsafeskript.elements.functions.classes.handles;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionList;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.function.Function;
import ch.njol.skript.lang.function.FunctionReference;
import ch.njol.skript.lang.function.Functions;
import ch.njol.skript.lang.function.JavaFunction;
import ch.njol.skript.lang.function.Parameter;
import ch.njol.skript.lang.function.ScriptFunction;
import ch.njol.skript.lang.function.Signature;
import ch.njol.skript.registrations.Classes;
import com.github.skriptness.unsafeskript.util.Reflectness;
import org.bukkit.event.Event;
import org.skriptlang.skript.lang.converter.Converters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface FunctionHandle<T> {

    static FunctionHandle<?> of(String name) {
        return of(name, null);
    }

    static FunctionHandle<?> of(String name, String script) {
        if (Functions.getFunction(name, script) == null)
            return null;
        return of(Functions.getFunction(name, script));
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

    default void setParameters(Parameter<?>[] parameters) {
        Reflectness.setSignatureParameters(getFunction().getSignature(), parameters);
        updateReferences();
    }

    default void setReturnType(ClassInfo<?> classInfo) {
        Reflectness.setReturnType(getFunction().getSignature(), classInfo);
        updateReferences();
    }

    default void updateReferences() {
        linkReferences(getFunction().getSignature());
    }

    default void linkReferences(Signature<T> oldSignature) {
        for (FunctionReference<T> call : Reflectness.getCalls(oldSignature))
            Reflectness.setReferencedFunction(call, getFunction());
    }

    default T[] execute(Object[][] arguments) {
        Parameter<?>[] parameters = getFunction().getParameters();
        Object[][] args = arguments.length < parameters.length ? Arrays.copyOf(arguments, parameters.length) : arguments;

        // Check if there are invalid parameters without default values
        for (int i = 0; i < parameters.length; i++) {

            // Attempt conversion
            Class<?> type = parameters[i].getType().getC();
            if (args[i] != null && !type.isAssignableFrom(args[i].getClass()))
                args[i] = Converters.convert(args[i], type);

            // Abort on invalid function call
            if ((args[i] == null || args[i].length == 0) && parameters[i].getDefaultExpression() == null)
                return null;

        }

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
        int maxParameters = signature.getMaxParameters();
        boolean singleListParam = maxParameters == 1 && !signature.getParameter(0).isSingleValue();

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

            // Don't allow more parameters than max
            if (params.length > maxParameters)
                return null;

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
