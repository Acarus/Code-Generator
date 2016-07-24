package com.acarus.template.processor;

import java.util.List;

public class ForeachTag extends Tag {

    private static final String IS_FIRST_ELEMENT = "_isFirstElement_";
    private static final String IS_LAST_ELEMENT = "_isLastElement_";

    @Override
    public boolean hasBody() {
        return true;
    }

    @Override
    public String eval() {
        StringBuilder buffer = new StringBuilder();
        String[] parts = arg.split("<-");
        String itemName = parts[0].trim();
        String collectionName = parts[1].trim();
        if (!model.containsKey(collectionName)) {
            if (!optional) {
                throw new UndefinedVariableException(collectionName);
            } else {
                return "";
            }
        }

        List collection = (List) model.get(collectionName);
        for (int i = 0; i < collection.size(); i++) {
            Object item = collection.get(i);
            Template template = new Template(tagFactory);
            template.setTemplate(body);
            model.putLocal(itemName, item);
            model.putLocal(IS_FIRST_ELEMENT, Boolean.FALSE.toString());
            model.putLocal(IS_LAST_ELEMENT, Boolean.FALSE.toString());
            if (i == 0) {
                model.putLocal(IS_FIRST_ELEMENT, Boolean.TRUE.toString());
            }

            if (i == collection.size() - 1) {
                model.putLocal(IS_LAST_ELEMENT, Boolean.TRUE.toString());
            }

            template.setModel(model);
            buffer.append(template.eval());
            model.removeLocal(itemName);
            model.removeLocal(IS_FIRST_ELEMENT);
            model.removeLocal(IS_LAST_ELEMENT);
        }
        return buffer.toString();
    }
}
