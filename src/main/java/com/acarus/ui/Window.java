package com.acarus.ui;

import com.acarus.com.acarus.code.generator.Block;
import com.acarus.com.acarus.code.generator.SourceBuilder;
import com.acarus.template.processor.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Window extends JFrame {

    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    public static final int BLOCK_WIDTH = 200;
    public static final int BLOCK_HEIGHT = 50;
    private static final String ENTRY_POINT = "Program";

    private JPopupMenu popupMenu;
    private int clickX = 100;
    private int clickY = 100;
    private Point anchorPoint;
    private BlockFactory blockFactory;
    private List<Block> blockList = new ArrayList<>();
    private Map<String, Object> globalModel;
    private Map<JLabel, Boolean> isBlockSelected = new HashMap<>();
    private Map<JLabel, Block> blocks = new HashMap<>();
    private TagFactory tagFactory;

    public Window(BlockFactory blockFactory, Map<String, Object> globalModel) {
        this.blockFactory = blockFactory;
        this.globalModel = globalModel;
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setLayout(null);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                System.out.println("Clicked........!!!!!!!!!!!.......");
                System.out.println(mouseEvent.getButton());
                if (mouseEvent.getButton() == MouseEvent.BUTTON3) {
                    clickX = mouseEvent.getX();
                    clickY = mouseEvent.getY();
                    popupMenu.show(Window.this, clickX, clickY);
                }
            }
        });
        createMenu();
        createPopupMenu();
        tagFactory = new TagFactory();
        tagFactory.add("echo", EchoTag.class);
        tagFactory.add("if", ConditionTag.class);
        tagFactory.add("noIf", ConditionWithElseTag.class);
        tagFactory.add("define", DefineTag.class);
        tagFactory.add("foreach", ForeachTag.class);
    }

    private void createPopupMenu() {
        popupMenu = new JPopupMenu();
        JMenu addBlockMenu = new JMenu("Add block");
        for (final String blockName : blockFactory.availableBlocks()) {
            JMenuItem item = new JMenuItem(blockName);
            item.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    blockList.add(new Block(blockName));
                    final JLabel block = new JLabel(blockName);
                    block.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                    block.setVerticalAlignment(JLabel.CENTER);
                    block.setHorizontalAlignment(JLabel.CENTER);
                    block.setLocation(clickX, clickY);
                    block.setSize(new Dimension(BLOCK_WIDTH, BLOCK_HEIGHT));
                    block.addMouseMotionListener(new MouseAdapter() {
                        @Override
                        public void mouseMoved(MouseEvent mouseEvent) {
                            anchorPoint = mouseEvent.getPoint();
                        }

                        @Override
                        public void mouseDragged(MouseEvent e) {
                            int anchorX = anchorPoint.x;
                            int anchorY = anchorPoint.y;
                            Point parentOnScreen = block.getParent().getLocationOnScreen();
                            Point mouseOnScreen = e.getLocationOnScreen();
                            Point position = new Point(mouseOnScreen.x - parentOnScreen.x -
                                    anchorX, mouseOnScreen.y - parentOnScreen.y - anchorY);
                            block.setLocation(position);
                        }
                    });

                    block.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent mouseEvent) {
                            JLabel block = (JLabel) mouseEvent.getSource();
                            if (mouseEvent.getButton() == MouseEvent.BUTTON3) {
                                if (isBlockSelected.get(block)) {
                                    block.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                                    isBlockSelected.put(block, false);
                                } else {
                                    block.setBorder(BorderFactory.createLineBorder(Color.RED));
                                    isBlockSelected.put(block, true);
                                    JLabel secondSelectedBlock = findSecondSelectedBlock(block);
                                    if (secondSelectedBlock != null) {
                                        System.out.println("Two blocks selected...");
                                        Block secondBlockObject = blocks.get(block);
                                        Block firstBlockObject = blocks.get(secondSelectedBlock);
                                        System.out.println(firstBlockObject.getModel().getLocalModel());
                                        System.out.println(secondBlockObject.getModel().getLocalModel());
                                        BlocksBindingWindow blocksBindingWindow = new BlocksBindingWindow(firstBlockObject, secondBlockObject);
                                        blocksBindingWindow.setVisible(true);
                                    }
                                }
                            } else if (mouseEvent.getButton() == MouseEvent.BUTTON1 && isBlockSelected.get(block)) {
                                System.out.println("bind variables");
                                Map<String, Object> blockLocalModel = blocks.get(block).getModel().getLocalModel();
                                VariablesBindingWindow variablesBindingWindow = new VariablesBindingWindow(globalModel, blockLocalModel);
                                variablesBindingWindow.setVisible(true);
                            }
                        }
                    });
                    isBlockSelected.put(block, false);
                    blocks.put(block, blockFactory.get(blockName));
                    Window.this.add(block);
                    Window.this.repaint();
                    System.out.println(blockList);
                }
            });
            addBlockMenu.add(item);
        }
        popupMenu.add(addBlockMenu);
    }

    private void createMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem newMenuItem = new JMenuItem("New");
        JMenuItem exportMenuItem = new JMenuItem("Export");
        exportMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.out.println("Export program...");
                export();
            }
        });
        fileMenu.add(newMenuItem);
        fileMenu.add(exportMenuItem);
        menuBar.add(fileMenu);

        JMenu functionMenu = new JMenu("Function");
        menuBar.add(functionMenu);

        JMenu variableMenu = new JMenu("Variable");
        JMenuItem newGlobalVariableMenuItem = new JMenuItem("Global");
        newGlobalVariableMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.out.println("Create global variable");
                JFrame variablesWindow = new VariablesWindow(globalModel);
                variablesWindow.setVisible(true);
            }
        });
        variableMenu.add(newGlobalVariableMenuItem);
        menuBar.add(variableMenu);

        setJMenuBar(menuBar);
    }

    private JLabel findSecondSelectedBlock(JLabel firstSelectedBlock) {
        for (JLabel block : isBlockSelected.keySet()) {
            if (!block.equals(firstSelectedBlock) && isBlockSelected.get(block)) {
                return block;
            }
        }
        return null;
    }

    private void export() {
        Block entryPoint = null;
        for (Block block : blocks.values()) {
            if (block.getName().equals(ENTRY_POINT)) {
                entryPoint = block;
                break;
            }
        }

        if (entryPoint != null) {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                System.out.println("Selected file: " + selectedFile.getAbsolutePath());

                String extension = "";
                int i = selectedFile.getAbsolutePath().lastIndexOf('.');
                if (i > 0) {
                    extension = selectedFile.getAbsolutePath().substring(i + 1);
                }
                System.out.println("Generating code...");

                TemplateResolver templateResolver = null;
                if ("cpp".equals(extension)) {
                    templateResolver = new TemplateResolver(tagFactory, "resource:cpp", ".tpl");
                } else if ("java".equals(extension)) {
                    templateResolver = new TemplateResolver(tagFactory, "resource:java", ".tpl");
                } else if ("pas".equals(extension)) {
                    templateResolver = new TemplateResolver(tagFactory, "resource:pascal", ".tpl");
                } else {
                    System.out.println("Invalid extension!");
                }
                String output = SourceBuilder.build(entryPoint, templateResolver);
                System.out.println(output);

                try {
                    if (!selectedFile.exists()) {
                        selectedFile.createNewFile();
                    }
                    FileOutputStream out = new FileOutputStream(selectedFile);
                    out.write(output.getBytes());
                    out.close();
                } catch (IOException e) {
                }
            }
        } else {
            System.out.println("Error: Can not find entry point");
        }
    }
}
