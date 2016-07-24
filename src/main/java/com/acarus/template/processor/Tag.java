package com.acarus.template.processor;

import com.acarus.com.acarus.code.generator.Model;

public abstract class Tag {

    public static final String OPTIONAL = "?";
    public static String VAR_TEMPLATE = "${%s}";
    protected String arg;
    protected String body;
    protected Model model;
    protected TagFactory tagFactory;
    protected boolean optional = false;

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public String getArg() {
        return arg;
    }

    public void setArg(String arg) {
        optional = isOptional(arg);
        if (optional) {
            this.arg = arg.substring(OPTIONAL.length(), arg.length());
        } else {
            this.arg = arg;
        }
    }

    private boolean isOptional(String arg) {
        return arg.startsWith(OPTIONAL);
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean hasBody() {
        return false;
    }

    public abstract String eval();

    protected void injectData() {
        try {
            for (String var : model.keys()) {
                if (model.get(var) instanceof String) {
                    arg = arg.replace(String.format(VAR_TEMPLATE, var), (String) model.get(var));
                }
            }
        } catch (UndefinedVariableException e) {
            e.printStackTrace();
        }
    }

    protected void evalNestedTags() {
        Template nestedTags = new Template(tagFactory);
        nestedTags.setModel(model);
        nestedTags.setTemplate(body);
        body = nestedTags.eval();
    }

    public TagFactory getTagFactory() {
        return tagFactory;
    }

    public void setTagFactory(TagFactory tagFactory) {
        this.tagFactory = tagFactory;
    }
}
