package com.github.skriptness.unsafeskript.util;

import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.function.*;
import org.eclipse.jdt.annotation.Nullable;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

// Bonus points if you get the reference
public final class Reflectness {

    // Naming format: %CLASSNAME%_%FIELDNAME%
    // Group fields of the same class together; leave a blank line between each group
    private static final Field FUNCTIONS_JAVA_NAMESPACE;
    private static final Field FUNCTIONS_GLOBAL_FUNCTIONS;

    private static final Field FUNCTION_SIGN;

    private static final Field SCRIPT_FUNCTION_TRIGGER;

    private static final Field SIGNATURE_PARAMETERS;
    private static final Field SIGNATURE_CALLS;

    private static final Field FUNCTION_REFERENCE_FUNCTION;

    static {
        try {
            FUNCTIONS_JAVA_NAMESPACE = Functions.class.getDeclaredField("javaNamespace");
            FUNCTIONS_GLOBAL_FUNCTIONS = Functions.class.getDeclaredField("globalFunctions");

            FUNCTION_SIGN = Function.class.getDeclaredField("sign");

            SCRIPT_FUNCTION_TRIGGER = ScriptFunction.class.getDeclaredField("trigger");

            SIGNATURE_PARAMETERS = Signature.class.getDeclaredField("parameters");
            SIGNATURE_CALLS = Signature.class.getDeclaredField("calls");

            FUNCTION_REFERENCE_FUNCTION = FunctionReference.class.getDeclaredField("function");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static Namespace getJavaNamespace() {
        return get(FUNCTIONS_JAVA_NAMESPACE, null);
    }

    public static Map<String, Namespace> getGlobalFunctions() {
        return get(FUNCTIONS_GLOBAL_FUNCTIONS, null);
    }

    public static <T> void setSignature(Function<T> function, Signature<T> signature) {
        set(FUNCTION_SIGN, function, signature);
    }

    public static void setTrigger(ScriptFunction<?> function, Trigger trigger) {
        set(SCRIPT_FUNCTION_TRIGGER, function, trigger);
    }

    public static void setParameters(Signature<?> signature, Parameter<?>[] parameters) {
        set(SIGNATURE_PARAMETERS, signature, parameters);
    }

    public static Collection<FunctionReference<?>> getCalls(Signature<?> signature) {
        return get(SIGNATURE_CALLS, signature);
    }

    public static void setFunction(FunctionReference<?> reference, Function<?> function) {
        set(FUNCTION_REFERENCE_FUNCTION, reference, function);
    }

    @SuppressWarnings("unchecked")
    private static <T> T get(Field field, @Nullable Object object) {
        try {
            field.setAccessible(true);
            return (T) field.get(object);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static void set(Field field, @Nullable Object object, Object value) {
        try {
            field.setAccessible(true);
            field.set(object, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
