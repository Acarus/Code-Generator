package com.acarus;

import com.acarus.com.acarus.code.generator.Block;
import com.acarus.com.acarus.code.generator.Model;
import com.acarus.template.processor.BlockFactory;
import com.acarus.ui.Window;

import javax.swing.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Application {

    public static String compress(String str) {
        StringBuilder buff = new StringBuilder();
        Character prev = null;
        int cnt = 0;
        for (Character item: str.toCharArray()) {
            if (item == prev) {
                cnt++;
                if (prev != null) {
                    buff.append(cnt + prev.toString());
                    prev = item;
                }
            } else {
                prev = item;
                cnt = 1;
            }
        }

        if (cnt > 0 && prev != null) {
            buff.append(cnt + prev.toString());
        }
        return buff.toString();
    }

    public static void main(String[] args) {
        System.out.println(compress("aabbc"));
        BlockFactory blockFactory = new BlockFactory();

        /* -------------- [ global vars, constants ] ---------------------- */
        Map<String, Object> globalModel = new HashMap<>();
        globalModel.put("_global_variables", Collections.emptyList());

        Block reader = new Block("ConsoleReader");
        Map<String, Object> readerModel = new HashMap<>();
        readerModel.put("__vars__", Arrays.asList(Arrays.asList("var", "Variable")));
        reader.setModel(new Model(readerModel, globalModel));
        blockFactory.add(reader);

        Block program = new Block("Program");
        Map<String, Object> programModel = new HashMap<>();
        programModel.put("programName", "Generated");
        programModel.put("__children__", Arrays.asList(Arrays.asList("body", "Entry point")));
        program.setModel(new Model(programModel, globalModel));
        blockFactory.add(program);

        Block writer = new Block("ConsoleWriter");
        Map<String, Object> writerModel = new HashMap<>();
        writerModel.put("__vars__", Arrays.asList(Arrays.asList("var", "To be written")));
        writer.setModel(new Model(writerModel, globalModel));
        blockFactory.add(writer);

        Block assign = new Block("AssignOperator");
        Map<String, Object> assignModel = new HashMap<>();
        assignModel.put("__vars__", Arrays.asList(Arrays.asList("dest", "To")));
        assignModel.put("__children__", Arrays.asList(Arrays.asList("source", "Expression")));
        assign.setModel(new Model(assignModel, globalModel));
        blockFactory.add(assign);

        Block addOperator = new Block("AddOperator");
        Map<String, Object> addModel = new HashMap<>();
        addModel.put("__vars__", Arrays.asList(Arrays.asList("left", "Left"), Arrays.asList("right", "Right")));
        addOperator.setModel(new Model(addModel, globalModel));
        blockFactory.add(addOperator);

        Window wnd = new Window(blockFactory, globalModel);
        wnd.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        wnd.setVisible(true);
    }
}
