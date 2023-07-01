package com.github.skriptness.unsafeskript.elements.functions.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.github.skriptness.unsafeskript.elements.functions.classes.handles.FunctionHandle;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

public class ExprReturnType extends SimplePropertyExpression<FunctionHandle<?>, ClassInfo> {

    static {
        register(ExprReturnType.class, ClassInfo.class, "return[ ]type", "functions");
    }

    @Override
    protected String getPropertyName() {
        return "return type";
    }

    @Override
    @Nullable
    public ClassInfo<?> convert(FunctionHandle<?> handle) {
        return handle.getFunction().getReturnType();
    }

    @Override
    @Nullable
    public Class<?>[] acceptChange(ChangeMode mode) {
        if (mode == ChangeMode.SET || mode == ChangeMode.DELETE)
            return new Class[] {ClassInfo.class};
        return null;
    }

    @Override
    public void change(Event event, @Nullable Object[] delta, ChangeMode mode) {
        FunctionHandle<?> function = getExpr().getSingle(event);
        if (function == null) {
            return;
        }
        if (mode == ChangeMode.SET) {
            function.setReturnType((ClassInfo<?>) delta[0]);
        } else {
            function.setReturnType(null);
        }
    }

    @Override
    public Class<? extends ClassInfo> getReturnType() {
        return ClassInfo.class;
    }

}