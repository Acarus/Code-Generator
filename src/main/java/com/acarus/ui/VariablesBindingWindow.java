package com.acarus.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VariablesBindingWindow extends JFrame {

    public static final int WIDTH = 300;
    public static final int HEIGHT = 200;

    private static final int NAME = 0;
    private static final int ALIAS = 1;

    private JComboBox varList;
    private JComboBox pinList;
    private JButton saveBtn = new JButton("Save");
    private Map<String, Object> globalModel;
    private Map<String, Object> localModel;

    public VariablesBindingWindow(final Map<String, Object> globalModel, final Map<String, Object> blockLocalModel) {
        System.out.println(blockLocalModel);
        this.globalModel = globalModel;
        this.localModel = blockLocalModel;
        List<Map<String,Object>> vars = (List<Map<String,Object>>) globalModel.get("_global_variables");
        System.out.println(vars);
        List<String> varNameList = new ArrayList<>();
        for (Map<String, Object> var: vars) {
            varNameList.add((String) var.get("alias"));
        }
        varList = new JComboBox(varNameList.toArray());

        List<String> pinAliasList = new ArrayList<>();
        List<List<String>> pins = (List<List<String>>) blockLocalModel.get("__vars__");
        if (pins != null) {
            for (List<String> pin : pins) {
                pinAliasList.add(pin.get(ALIAS));
            }
        }
        pinList = new JComboBox(pinAliasList.toArray());

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        panel.add(new JLabel("Pin:"));
        panel.add(pinList);
        panel.add(new JLabel("Variable:"));
        panel.add(varList);
        panel.add(new JLabel(""));
        panel.add(saveBtn);
        saveBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String selectedPin = (String) pinList.getSelectedItem();
                String selectedVarAlias = (String) varList.getSelectedItem();
                localModel.put(getParameterByAlias(selectedPin), getVarNameByAlias(selectedVarAlias));
                System.out.println(localModel);
            }
        });

        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(panel);
        pack();
    }

    private Map<String,Object> getVarNameByAlias(String varAlias) {
        List<Map<String,Object>> vars = (List<Map<String,Object>>) globalModel.get("_global_variables");
        for (Map<String, Object> var: vars) {
            if (varAlias.equals(var.get("alias"))) {
                return var;
            }
        }
        return null;
    }

    private String getParameterByAlias(String paramAlias) {
        List<List<String>> vars = (List<List<String>>) localModel.get("__vars__");
        for (List<String> var: vars) {
            if (paramAlias.equals(var.get(ALIAS))) {
                return (String) var.get(NAME);
            }
        }
        return null;
    }
}
