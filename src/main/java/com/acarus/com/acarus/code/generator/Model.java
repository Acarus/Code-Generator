package com.acarus.com.acarus.code.generator;

import com.acarus.Utils;
import com.acarus.template.processor.UndefinedVariableException;

import java.util.*;

public class Model implements Cloneable {
    private Map<String, Object> localModel;
    private Map<String, Object> globalModel;

    public Model() {}

    public Model(Map<String, Object> localModel, Map<String, Object> globalModel) {
        this.localModel = localModel;
        this.globalModel = globalModel;
    }

    public Model(Model model) {
        localModel = new HashMap<>();
        localModel.putAll(model.getLocalModel());
        globalModel = model.getGlobalModel();
    }

    public Map<String, Object> getLocalModel() {
        return localModel;
    }

    public void setLocalModel(Map<String, Object> localModel) {
        this.localModel = localModel;
    }

    public Map<String, Object> getGlobalModel() {
        return globalModel;
    }

    public void setGlobalModel(Map<String, Object> globalModel) {
        this.globalModel = globalModel;
    }

    public Object get(String key) {
        if (isGlobalVariable(key)) {
            return Utils.getDataFromModel(globalModel, key);
        } else {
            return Utils.getDataFromModel(localModel, key);
        }
    }

    public void putGlobal(String key, Object value) {
        globalModel.put(key, value);
    }

    public void putLocal(String key, Object value) {
        localModel.put(key, value);
    }

    public void removeLocal(String key) {
        localModel.remove(key);
    }

    public void removeGlobal(String key) {
        globalModel.remove(key);
    }

    public boolean containsKey(String key) {
        try {
            get(key);
            return true;
        } catch (UndefinedVariableException e) {
            return false;
        }
    }

    public Set<String> keys() {
        Set<String> keys = new HashSet<>();
        keys.addAll(getKeysDeeply(localModel, ""));
        keys.addAll(getKeysDeeply(globalModel, ""));
        return keys;
    }

    private Set<String> getKeysDeeply(Map<String, Object> map, String prefix) {
        Set<String> keys = new HashSet<>();
        for (String key: map.keySet()) {
            if (map.get(key) instanceof Map) {
                keys.addAll(getKeysDeeply((Map<String, Object>) map.get(key), prefix + key + "."));
            } else {
                keys.add(prefix + key);
            }
        }
        return keys;
    }

    private boolean isGlobalVariable(String var) {
        return  var.startsWith("_global_") ||
                var.startsWith("_define_") ||
                var.startsWith("_const_");
    }
}
