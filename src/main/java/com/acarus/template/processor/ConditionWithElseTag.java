package com.acarus.template.processor;

import com.acarus.Utils;

public class ConditionWithElseTag extends Tag {

    private static final String ELSE = "@else";

    @Override
    public boolean hasBody() {
        return true;
    }

    @Override
    public String eval() {
        evalNestedTags();
        injectData();
        String result = "";
        if (Utils.evalCondition(arg)) {
            result = body.substring(0, body.indexOf(ELSE));
        } else {
            result = body.substring(body.indexOf(ELSE) + ELSE.length(), body.length());
        }
        return result;
    }
}
