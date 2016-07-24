package com.acarus.ui;

import com.acarus.com.acarus.code.generator.Block;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlocksBindingWindow extends JFrame {

    public static final int WIDTH = 300;
    public static final int HEIGHT = 200;

    private static final int NAME = 0;
    private static final int ALIAS = 1;

    private static final String NEXT = "Next";

    private Block firstBlock;
    private Block secondBlock;
    private JComboBox pinList;
    private JButton saveBtn = new JButton("Save");

    public BlocksBindingWindow(final Block firstBlock, final Block secondBlock) {
        this.firstBlock = firstBlock;
        this.secondBlock = secondBlock;

        List<String> pins = new ArrayList<>();
        pins.addAll(Arrays.asList(NEXT));
        List<List<String>> children = (List<List<String>>) firstBlock.getModel().getLocalModel().get("__children__");
        if (children != null) {
            for (List<String> child: children) {
                pins.add(child.get(ALIAS));
            }
        }
        pinList = new JComboBox(pins.toArray());

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        panel.add(new JLabel("Pin:"));
        panel.add(pinList);
        panel.add(new JLabel(""));
        panel.add(saveBtn);

        saveBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String selectedPin = (String) pinList.getSelectedItem();
                if (NEXT.equals(selectedPin)) {
                    firstBlock.setNext(secondBlock);
                } else {
                    firstBlock.addChild(secondBlock, findNameByAlias(selectedPin));
                }
                System.out.println(firstBlock.getModel().getLocalModel());
                System.out.println(secondBlock.getModel().getLocalModel());
            }
        });

        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(panel);
        pack();
    }

    private String findNameByAlias(String alias) {
        List<List<String>> children = (List<List<String>>) firstBlock.getModel().getLocalModel().get("__children__");
        for (List<String> child: children) {
            if (alias.equals(child.get(ALIAS))) {
                return child.get(NAME);
            }
        }
        return null;
    }
}
