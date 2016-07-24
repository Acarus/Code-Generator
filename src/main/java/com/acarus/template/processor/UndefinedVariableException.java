package com.acarus.template.processor;

public class UndefinedVariableException extends RuntimeException {
    private String variableName;

    public UndefinedVariableException(String variableName) {
        this.variableName = variableName;
    }
}
