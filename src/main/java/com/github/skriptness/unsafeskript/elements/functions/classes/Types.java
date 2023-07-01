package com.github.skriptness.unsafeskript.elements.functions.classes;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.lang.function.Parameter;
import ch.njol.skript.registrations.Classes;
import com.github.skriptness.unsafeskript.elements.functions.classes.handles.FunctionHandle;
import org.eclipse.jdt.annotation.Nullable;

public class Types {

    static {
        Classes.registerClass(new ClassInfo<>(FunctionHandle.class, "function")
                .user("function( (handle|reference))?s?")
                .name("Function Reference")
                .description("Represents a reference to a Skript function.")
                .examples("set {_function} to the function \"product\"")
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
                .description("Represents a parameter of a function.")
                .examples("")
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
                        return Classes.toString(parameter.getType()) + " parameter {_" + parameter.getName() + (parameter.isSingleValue() ? "}" : "::*}");
                    }

                    @Override
                    public String toVariableNameString(Parameter<?> parameter) {
                        return "parameter(" + Classes.toString(parameter.getType()) + "):" + parameter.getName();
                    }
                }));
    }

}
