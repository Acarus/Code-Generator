package com.acarus.com.acarus.code.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Block implements Cloneable {

    public static final String PARENT = "_parent_";

    private Block next;
    private List<Block> children = new ArrayList<>();
    private List<List<Block>> nestedChildren = new ArrayList<>();
    private Model model;
    private Map<Object, String> childrenNames = new HashMap<>();
    private Map<List<Block>, String> nestedChildOutput = new HashMap<>();
    private String name;
    private Block parent;

    public Block(String name) {
        this.name = name;
    }

    public Block(Block block) {
        this(block.getName());
        setModel(new Model(block.getModel()));
    }

    public String getName() {
        return name;
    }

    public List<Block> getChildren() {
        return children;
    }

    public void addChild(Block child, String childName) {
        children.add(child);
        childrenNames.put(child, childName);
        child.setParent(this);
    }

    public String getChildName(Object child) {
        return childrenNames.get(child);
    }

    public Block getNext() {
        return next;
    }

    public void setNext(Block next) {
        this.next = next;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        if (parent != null) {
            model.putLocal(PARENT, parent.getName());
        }
        this.model = model;
    }

    public void setParent(Block parent) {
        this.parent = parent;
        if (model != null) {
            model.putLocal(PARENT, parent.getName());
        }
    }

    public List<List<Block>> getNestedChildren() {
        return nestedChildren;
    }

    public void addNestedChild(List<Block> nestedChild, String name, String outputName) {
        nestedChildren.add(nestedChild);
        childrenNames.put(nestedChild, name);
        nestedChildOutput.put(nestedChild, outputName);
    }

    public String getNestedChildOutput(List<Block> nestedChild) {
        return nestedChildOutput.get(nestedChild);
    }
}
