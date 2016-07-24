package com.acarus.template.processor;

public class EchoTag extends Tag {

    @Override
    public String eval() {
        if (!model.containsKey(arg)) {
            if (!optional) {
                throw new UndefinedVariableException(arg);
            } else {
                return "";
            }
        }

        String result = model.get(arg).toString();
        String defineKey = DefineTag.MODEL_DEFINE + result;
        if (model.containsKey(defineKey)) {
            result = model.get(defineKey).toString();
        }
        return result;
    }
}
