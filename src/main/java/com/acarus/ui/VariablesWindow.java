package com.acarus.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.*;
import java.util.List;

public class VariablesWindow extends JFrame {

    public static final int WIDTH = 300;
    public static final int HEIGHT = 400;
    public static final String NEW_VARIABLE = "New variable";

    private JComboBox varList;
    private JComboBox typeList;
    private JTextField nameInput = new JTextField();
    private JTextField aliasInput = new JTextField();
    private JTextField valueInput = new JTextField();
    private JButton saveBtn = new JButton("Save");
    private JButton removeBtn = new JButton("Remove");
    private Map<String, Object> globalModel;

    public VariablesWindow(final Map<String, Object> globalModel) {
        this.globalModel = globalModel;
        List<Map<String,Object>> vars = (List<Map<String,Object>>) globalModel.get("_global_variables");
        System.out.println(vars);
        List<String> varNameList = new ArrayList<>();
        for (Map<String, Object> var: vars) {
            varNameList.add((String) var.get("alias"));
        }
        varNameList.add(NEW_VARIABLE);
        String[] types = {"int", "double", "string" };
        varList = new JComboBox(varNameList.toArray());
        varList.setSelectedItem(NEW_VARIABLE);
        varList.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                    String selectedVar = (String) itemEvent.getItem();
                    if (!NEW_VARIABLE.equals(selectedVar)) {
                        List<Map<String,Object>> vars = (List<Map<String,Object>>) globalModel.get("_global_variables");
                        for (Map<String, Object> item: vars) {
                            if (selectedVar.equals(item.get("alias"))) {
                                nameInput.setText((String) item.get("name"));
                                aliasInput.setText((String) item.get("alias"));
                                valueInput.setText((String) item.get("value"));
                                typeList.setSelectedItem(item.get("alias"));
                            }
                        }
                    }
                }
            }
        });
        typeList = new JComboBox(types);
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        panel.add(new JLabel("Variable:"));
        panel.add(varList);
        panel.add(new JLabel("Name:"));
        panel.add(nameInput);
        panel.add(new JLabel("Alias:"));
        panel.add(aliasInput);
        panel.add(new JLabel("Value:"));
        panel.add(valueInput);
        panel.add(new JLabel("Type:"));
        panel.add(typeList);
        panel.add(new JLabel(""));
        panel.add(saveBtn);
        panel.add(new JLabel(""));
        panel.add(removeBtn);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(panel);
        pack();

        saveBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                List<Map<String,Object>> vars = (List<Map<String,Object>>) globalModel.get("_global_variables");
                System.out.println(vars);

                String name = nameInput.getText();
                String alias = aliasInput.getText();
                String value = valueInput.getText();
                String type = (String) typeList.getSelectedItem();

                Map<String, Object> newVariable = new HashMap<>();
                newVariable.put("name", name);
                newVariable.put("type", type);
                newVariable.put("alias", alias);
                newVariable.put("value", value);

                List<Map<String, Object>> modifiedVarList = new ArrayList<>();
                modifiedVarList.addAll(vars);

                String varAlias = (String) varList.getSelectedItem();
                if (NEW_VARIABLE.equals(varAlias)) {
                    modifiedVarList.add(newVariable);
                    varList.addItem(alias);
                    System.out.println("Added new var");
                } else {
                    int idx = findVar(varAlias);
                    if (idx >= 0) {
                        modifiedVarList.set(idx, newVariable);
                        varList.removeItem(varAlias);
                        varList.addItem(alias);
                    }
                }
                globalModel.put("_global_variables", modifiedVarList);
            }
        });

        removeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String selectedVarAlias = (String) varList.getSelectedItem();
                if (!NEW_VARIABLE.equals(selectedVarAlias)) {
                    int idx = findVar(selectedVarAlias);
                    List<Map<String,Object>> vars = (List<Map<String,Object>>) globalModel.get("_global_variables");
                    List<Map<String, Object>> modifiedVarList = new ArrayList<>();
                    modifiedVarList.addAll(vars);
                    modifiedVarList.remove(idx);
                    globalModel.put("_global_variables", modifiedVarList);
                    varList.removeItem(selectedVarAlias);
                }
            }
        });
    }

    private int findVar(String varAlias) {
        List<Map<String,Object>> vars = (List<Map<String,Object>>) globalModel.get("_global_variables");
        int idx = -1;
        for (int i = 0; i < vars.size(); i++) {
            if (varAlias.equals(vars.get(i).get("alias"))) {
                idx = i;
                break;
            }
        }
        return idx;
    }
}
