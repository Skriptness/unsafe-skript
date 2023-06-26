package com.github.skriptness.unsafeskript.elements.classes;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionList;
import ch.njol.skript.lang.function.Function;
import ch.njol.skript.lang.function.Parameter;
import ch.njol.skript.lang.function.Signature;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.Utils;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FunctionHandle<ReturnType> {

    private final Function<ReturnType> function;
    @Nullable
    private Expression<?> rawParameters;

    public FunctionHandle(Function<ReturnType> function) {
        this.function = function;
        this.rawParameters = null;
    }

    public FunctionHandle(Function<ReturnType> function, Expression<?> rawParameters) {
        this.function = function;
        this.rawParameters = rawParameters;
    }

    public Function<ReturnType> getFunction() {
        return function;
    }

    public boolean hasParameters() {
        return rawParameters != null;
    }

    public FunctionHandle<ReturnType> appendParameters(Expression<?> delta) {
        Class<?> superType = Utils.getSuperType(rawParameters.getReturnType(), delta.getReturnType());
        rawParameters = new ExpressionList<>(new Expression[] {rawParameters, delta}, superType, true);
        return this;
    }

    public ReturnType[] execute(Event event) {
        return execute(event, prepareArguments(event, rawParameters));
    }

    public ReturnType[] execute(Event event, Expression<?> rawParameters) {
        return execute(event, prepareArguments(event, rawParameters));
    }

    public ReturnType[] execute(Event event, Object[][] arguments) {
        // Fill with null
        Parameter<?>[] parameters = function.getParameters();
        Object[][] args = arguments.length < parameters.length ? Arrays.copyOf(arguments, parameters.length) : arguments;

        // Check if there are null parameters without default values
        for (int i = 0; i < parameters.length; i++)
            if (args[i] == null && parameters[i].getDefaultExpression() == null)
                return null; // Abort on invalid function call

        ReturnType[] result = function.execute(arguments);
        function.resetReturnValue();
        return result;
    }

    protected Object[][] prepareArguments(Event event, @Nullable Expression<?> rawParameters) {
        // Split-up parameters
        Expression<?>[] parameters;
        if (rawParameters == null)
            parameters = new Expression[0];
        else if (rawParameters instanceof ExpressionList<?>)
            parameters =  ((ExpressionList<?>) rawParameters).getExpressions();
        else
            parameters = new Expression[] {rawParameters};

        Signature<ReturnType> signature = function.getSignature();
        boolean singleListParam =  signature.getMaxParameters() == 1 && !signature.getParameter(0).isSingleValue();

        // Prepare parameter values for calling
        Object[][] params = new Object[singleListParam ? 1 : parameters.length][];
        if (singleListParam && parameters.length > 1) { // All parameters to one list
            List<Object> l = new ArrayList<>();
            for (Expression<?> parameter : parameters)
                l.addAll(Arrays.asList(parameter.getArray(event)));
            params[0] = l.toArray();

            // Don't allow mutating across function boundary; same hack is applied to variables
            for (int i = 0; i < params[0].length; i++) {
                params[0][i] = Classes.clone(params[0][i]);
            }
        } else { // Use parameters in normal way
            for (int i = 0; i < parameters.length; i++) {
                Object[] array = parameters[i].getArray(event);
                params[i] = Arrays.copyOf(array, array.length);
                // Don't allow mutating across function boundary; same hack is applied to variables
                for (int j = 0; j < params[i].length; j++) {
                    params[i][j] = Classes.clone(params[i][j]);
                }
            }
        }
        return params;
    }

}
