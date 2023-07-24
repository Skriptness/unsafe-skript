package com.github.skriptness.unsafeskript.elements.functions.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import com.github.skriptness.unsafeskript.elements.functions.classes.handles.FunctionHandle;

@Name("Is Single/Plural")
@Description("Checks whether a function returns a single value or a list.")
@Examples("function \"hello\" is plural")
@Since("INSERT VERSION")
public class CondIsPlural extends PropertyCondition<FunctionHandle<?>> {

    static {
        register(CondIsPlural.class, "(:single|plural)", "functions");
    }

    private boolean single;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        single = parseResult.hasTag("single");
        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }

    @Override
    public boolean check(FunctionHandle<?> handle) {
        return handle.getFunction().getReturnType() != null && handle.getFunction().isSingle() == single;
    }

    @Override
    protected String getPropertyName() {
        return single ? "single" : "plural";
    }

}
