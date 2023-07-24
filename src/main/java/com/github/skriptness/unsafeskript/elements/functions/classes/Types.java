package com.github.skriptness.unsafeskript.elements.functions.classes;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.lang.function.Parameter;
import ch.njol.skript.registrations.Classes;
import com.github.skriptness.unsafeskript.elements.functions.classes.handles.FunctionHandle;
import org.eclipse.jdt.annotation.Nullable;
import org.skriptlang.skript.lang.comparator.Comparators;
import org.skriptlang.skript.lang.comparator.Relation;

import java.util.Objects;

public class Types {

    static {
        Classes.registerClass(new ClassInfo<>(FunctionHandle.class, "function")
                .user("function( (handle|reference))?s?")
                .name("Function Reference")
                .description("Represents a reference to a Skript function.")
                .examples("set {_function} to the function \"product\"")
                .since("1.0-alpha1")
                .parser(new Parser<FunctionHandle<?>>() {
                    @Override
                    @Nullable
                    public FunctionHandle<?> parse(String string, ParseContext context) {
                        return null;
                    }

                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    public String toString(FunctionHandle<?> function, int flags) {
                        return "function '" + function.getFunction().getName() + "'";
                    }

                    @Override
                    public String toVariableNameString(FunctionHandle<?> function) {
                        return "function:" + function.getFunction().getName();
                    }
                }));
        Classes.registerClass(new ClassInfo<>(Parameter.class, "parameter")
                .user("(function )parameters?")
                .name("Function Parameter")
                .description("A function parameter consists of a name, a type (singular or plural), and a default value.")
                .examples("set {_parameter} to a new string parameter {_title} defaulting to \"<no title provided>\"")
                .parser(new Parser<Parameter<?>>() {
                    @Override
                    @Nullable
                    public Parameter<?> parse(String string, ParseContext context) {
                        return null;
                    }

                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    public String toString(Parameter<?> parameter, int flags) {
                        return Classes.toString(parameter.getType()) + " parameter {_" + parameter.getName() +
                                (parameter.isSingleValue() ? "}" : "::*}") +
                                (parameter.getDefaultExpression() == null ? "" : " defaulting to " +
                                        parameter.getDefaultExpression().toString(null, false));
                    }

                    @Override
                    public String toVariableNameString(Parameter<?> parameter) {
                        return String.format("parameter(%s):%s=%s",
                                parameter.getType().getName().toString(!parameter.isSingleValue()),
                                parameter.getName(),
                                parameter.getDefaultExpression() != null ? parameter.getDefaultExpression().toString(null, false) : "null");
                    }
                }));

        Comparators.registerComparator(Parameter.class, Parameter.class, (param1, param2) ->
                Relation.get(param1.getName().equals(param2.getName())
                        && param1.isSingleValue() == param2.isSingleValue()
                        && param1.getType().equals(param2.getType())
                        && Objects.equals(param1.getDefaultExpression(), param2.getDefaultExpression())));

    }

}
