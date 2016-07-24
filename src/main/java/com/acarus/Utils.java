package com.acarus;

import com.acarus.template.processor.UndefinedVariableException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class Utils {

    public static String camelcaseToDash(String str) {
        StringBuilder buf = new StringBuilder();
        char chs[] = str.toCharArray();
        for (int i = 0; i < chs.length; i++) {
            if (Character.isUpperCase(chs[i]) && i > 0 && i < chs.length) {
                buf.append("-");
            }
            buf.append(Character.toLowerCase(chs[i]));
        }
        return buf.toString();
    }

    public static String getFileContent(String filePath) throws IOException {
        byte[] data = Files.readAllBytes(Paths.get(filePath));
        return new String(data);
    }

    public static String getResourceContent(String fileName) throws IOException {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int nextByte;
        while ((nextByte = is.read()) != -1) {
            outputStream.write(nextByte);
        }
        return outputStream.toString();
    }

    public static Object getDataFromModel(Map<String, Object> model, String var) {
        Object current = model;
        String[] fields = var.split("\\.");
        try {
            for (String field : fields) {
                if (isListElement(field)) {
                    int openBracket = field.indexOf("[");
                    String property = field.substring(0, openBracket);
                    int index = Integer.parseInt(field.substring(openBracket + 1, field.length() - 1));
                    current = ((List) ((Map<String, Object>) current).get(property)).get(index);
                } else {
                    current = ((Map<String, Object>) current).get(field);
                }
            }
        } catch (Exception e) {
            throw new UndefinedVariableException(var);
        }

        if (current == null) {
            throw new UndefinedVariableException(var);
        }
        return current;
    }

    public static boolean isListElement(String var) {
        return var.matches("[a-zA-Z]+\\[\\d\\]");
    }

    public static boolean evalCondition(String condition) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("nashorn");

        Boolean returned = false;
        try {
            returned = (Boolean) engine.eval(condition);
        } catch (ScriptException e) {
            // TODO: handle ScriptException
            e.printStackTrace();
        }
        return returned;
    }
}
