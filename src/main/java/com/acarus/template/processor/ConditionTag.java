package com.acarus.template.processor;

import com.acarus.Utils;

public class ConditionTag extends Tag {

    @Override
    public boolean hasBody() {
        return true;
    }

    @Override
    public String eval() {
        if (optional) {
            return processOptional();
        } else {
            return processExpression();
        }
    }

    private String getData() {
        evalNestedTags();
        return body;
    }

    private String processOptional() {
        return optional && model.containsKey(arg) ? getData() : "";
    }

    private String processExpression() {
        injectData();
        return Utils.evalCondition(arg) ? getData() : "";
    }
}
