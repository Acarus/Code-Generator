package com.acarus.template.processor;

import java.security.InvalidParameterException;

public class DefineTag extends Tag {

    public static final String MODEL_DEFINE = "_define_";

    private static final String DELIMITER = "->";
    private static final int FROM = 0;
    private static final int TO = 1;
    private static final int ARG_PARTS_SIZE = 2;

    @Override
    public String eval() {
        String[] parts = arg.split(DELIMITER);
        if (parts.length != ARG_PARTS_SIZE) {
            throw new InvalidParameterException();
        }
        String from = parts[FROM].trim();
        String to = parts[TO].trim();
        model.putGlobal(MODEL_DEFINE + from, to);
        return "";
    }
}
