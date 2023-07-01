package com.github.skriptness.unsafeskript.util;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.function.ExprFunctionCall;
import ch.njol.skript.lang.function.Function;
import ch.njol.skript.lang.function.FunctionReference;
import ch.njol.skript.lang.function.Functions;
import ch.njol.skript.lang.function.Namespace;
import ch.njol.skript.lang.function.Parameter;
import ch.njol.skript.lang.function.ScriptFunction;
import ch.njol.skript.lang.function.Signature;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

// Bonus points if you get the reference
public final class Reflectness {

    private static final Field FUNCTIONS_JAVANAMESPACE = getField(Functions.class, "javaNamespace");
    private static final Field FUNCTIONS_GLOBALFUNCTIONS = getField(Functions.class, "globalFunctions");
    private static final Field FUNCTIONS_NAMESPACES = getField(Functions.class, "namespaces");

    private static final Field FUNCTION_SIGN = getField(Function.class, "sign");

    private static final Field SCRIPTFUNCTION_TRIGGER = getField(ScriptFunction.class, "trigger");

    private static final Field SIGNATURE_PARAMETERS = getField(Signature.class, "parameters");
    private static final Field SIGNATURE_CALLS = getField(Signature.class, "calls");
    private static final Field SIGNATURE_SCRIPT = getField(Signature.class, "script");

    private static final Field FUNCTIONREFERENCE_FUNCTION = getField(FunctionReference.class, "function");

    public static Namespace getJavaNamespace() {
        return get(FUNCTIONS_JAVANAMESPACE, null);
    }

    public static Map<String, Namespace> getGlobalFunctions() {
        return get(FUNCTIONS_GLOBALFUNCTIONS, null);
    }

    public static Map<Namespace.Key, Namespace> getNamespaces() {
        return get(FUNCTIONS_NAMESPACES, null);
    }

    public static <T> void setSignature(Function<T> function, Signature<T> signature) {
        set(FUNCTION_SIGN, function, signature);
    }

    public static void setTrigger(ScriptFunction<?> function, Trigger trigger) {
        set(SCRIPTFUNCTION_TRIGGER, function, trigger);
    }

    public static <T> void setSignatureParameters(Signature<T> signature, Parameter<?>[] parameters) {
        set(SIGNATURE_PARAMETERS, signature, parameters);
    }

    public static <T> Collection<FunctionReference<T>> getCalls(Signature<T> signature) {
        return get(SIGNATURE_CALLS, signature);
    }

    public static String getScript(Signature<?> signature) {
        return get(SIGNATURE_SCRIPT, signature);
    }

    public static <T> void setReferencedFunction(FunctionReference<T> reference, Function<T> function) {
        set(FUNCTIONREFERENCE_FUNCTION, reference, function);
    }

    private static Field getField(Class<?> clazz, String name) {
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException ex) {
            Skript.warning("Couldn't get '" + name + "' field from " + clazz.getName());
            return null;
        }
    }

    private static <T> T get(Field field, Object object) {
        if (field == null)
            return null;
        try {
            //noinspection unchecked
            return (T) field.get(object);
        } catch (IllegalAccessException ignored) {
            return null;
        }
    }

    private static void set(Field field, Object object, Object value) {
        if (field != null) {
            try {
                field.set(object, value);
            } catch (IllegalAccessException ignored) {
            }
        }
    }

}
