package com.acarus.com.acarus.code.generator;

import com.acarus.template.processor.Template;
import com.acarus.template.processor.TemplateResolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SourceBuilder {
    public static String build(Block entryPoint, TemplateResolver templateResolver) {
        StringBuilder result = new StringBuilder();
        Block currentBlock = entryPoint;
        while (currentBlock != null) {
            Model model = currentBlock.getModel();
            for (Block child : currentBlock.getChildren()) {
                model.putLocal(currentBlock.getChildName(child), build(child, templateResolver));
            }

            for(List<Block> nestedChild: currentBlock.getNestedChildren()) {
                List<Map<String, Object>> nestedResults = new ArrayList<>();
                for (Block child: nestedChild) {
                    Map<String, Object> childResult = new HashMap<>();
                    childResult.put(currentBlock.getNestedChildOutput(nestedChild), build(child, templateResolver));
                    nestedResults.add(childResult);
                }
                model.putLocal(currentBlock.getChildName(nestedChild), nestedResults);
            }

            Template template = templateResolver.resolve(currentBlock);
            template.setModel(currentBlock.getModel());
            result.append(template.eval());
            currentBlock = currentBlock.getNext();
        }
        return result.toString();
    }
}
