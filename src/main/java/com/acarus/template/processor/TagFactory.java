package com.acarus.template.processor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TagFactory {

    private Map<String, Class<? extends Tag>> tags = new HashMap<>();

    public void add(String tagName, Class<? extends Tag> tagClass) {
        if (!tagName.startsWith("@")) {
            tagName = "@" + tagName;
        }
        tags.put(tagName, tagClass);
    }

    public Tag get(String tagName) {
        if (!tags.containsKey(tagName)) {
            throw new UnregisteredTagException();
        }

        Tag tag = null;
        try {
            tag = (Tag) tags.get(tagName).newInstance();
            tag.setTagFactory(this);
        } catch (Exception e) {
            // TODO: handler exception
            e.printStackTrace();
        }
        return tag;
    }

    public Set<String> getRegisteredTagsNames() {
        return tags.keySet();
    }
}
