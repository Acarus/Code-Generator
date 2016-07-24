package com.acarus.template.processor;

import com.acarus.Utils;
import com.acarus.com.acarus.code.generator.Block;

import java.io.File;
import java.io.IOException;

public class TemplateResolver {

    private final static String RESOURCE_PREFIX = "resource:";

    private String prefix;
    private String suffix;
    private TagFactory tagFactory;

    public TemplateResolver(TagFactory tagFactory, String prefix, String suffix) {
        if (!prefix.endsWith(File.separator)) {
            prefix += File.separator;
        }
        this.prefix = prefix;
        this.suffix = suffix;
        this.tagFactory = tagFactory;
    }

    public Template resolve(Block block) {
        Template template = new Template(tagFactory);
        template.setTemplate(getTemplateContent(block));
        template.setModel(block.getModel());
        return template;
    }

    private String getTemplateContent(Block block) {
        String content = null;
        try {
            if (prefix.startsWith(RESOURCE_PREFIX)) {
                content = Utils.getResourceContent(getTemplateResourcePath(block.getName()));
            } else {
                content = Utils.getFileContent(getTemplateFilePath(block.getName()));
            }
        } catch (IOException e) {
            // TODO: handle IOException
            e.printStackTrace();
        }
        return content;
    }

    private String getTemplateFilePath(String blockName) {
        return prefix + getTemplateFileName(blockName);
    }

    private String getTemplateResourcePath(String blockName) {
        return prefix.substring(RESOURCE_PREFIX.length(), prefix.length()) + getTemplateFileName(blockName);
    }

    private String getTemplateFileName(String blockName) {
        return Utils.camelcaseToDash(blockName) + suffix;
    }
}
