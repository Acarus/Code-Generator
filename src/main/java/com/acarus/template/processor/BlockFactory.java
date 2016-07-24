package com.acarus.template.processor;

import com.acarus.com.acarus.code.generator.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BlockFactory {
    Map<String, Block> blocks = new HashMap<>();

    public void add(Block block) {
        blocks.put(block.getName(), block);
    }

    public Set<String> availableBlocks() {
        return blocks.keySet();
    }

    public Block get(String name) {
        return blocks.get(name).clone();
    }
}
