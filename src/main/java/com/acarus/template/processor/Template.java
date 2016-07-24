package com.acarus.template.processor;

import com.acarus.com.acarus.code.generator.Model;

import java.util.*;

public class Template {
    private String template;
    private Model model;
    private TagFactory tagFactory;
    private String TAG_ARG_PREFIX = "('";
    private String TAG_ARG_SUFFIX = "')";
    private String TAG_BODY_END = "#end";

    private State currentState = State.READ_TAG;
    private StringBuilder generatedTemplate = new StringBuilder();
    private Trie trie = new Trie();
    private Tag currentTag = null;
    private String readTagPrefix = "";
    private String readTagSuffix = "";
    private String readTagArg = "";
    private String readTagBodyEnd = "";
    private StringBuilder readTagBody;
    private int nestedTags = 0;

    public Template(TagFactory tagFactory) {
        this.tagFactory = tagFactory;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    void readTag(char nextSymbol) {
        generatedTemplate.append(nextSymbol);
        String tagName = trie.go(nextSymbol);
        if (tagName != null) {
            currentTag = tagFactory.get(tagName);
            currentState = State.READ_ARG_PREFIX;
            generatedTemplate.delete(generatedTemplate.length() - tagName.length(), generatedTemplate.length());
        }
    }

    void readArgPrefix(char nextSymbol) {
        readTagPrefix += nextSymbol;
        if (!TAG_ARG_PREFIX.startsWith(readTagPrefix)) {
            throw new InvalidTagException();
        }

        if (TAG_ARG_PREFIX.equals(readTagPrefix)) {
            currentState = State.READ_ARG_BODY;
        }
    }

    private void readArgBody(char nextSymbol) {
        if (!TAG_ARG_SUFFIX.startsWith(readTagSuffix + nextSymbol)) {
            readTagArg += nextSymbol;
        } else {
            currentState = State.READ_ARG_SUFFIX;
            readArgSuffix(nextSymbol);
        }
    }

    private void readArgSuffix(char nextSymbol) {
        readTagSuffix += nextSymbol;
        if (!TAG_ARG_SUFFIX.startsWith(readTagSuffix)) {
            throw new InvalidTagException();
        }

        if (TAG_ARG_SUFFIX.equals(readTagSuffix)) {
            if (!currentTag.hasBody()) {
                generate();
                currentState = State.READ_TAG;
            } else {
                currentState = State.READ_TAG_BODY;
            }
            trie.reset();
        }
    }

    private void generate() {
        currentTag.setArg(readTagArg);
        currentTag.setModel(model);
        if (currentTag.hasBody()) {
            currentTag.setBody(readTagBody.toString());
        }
        String tagResult = currentTag.eval();
        generatedTemplate.append(tagResult);
        initTag();
    }

    public String eval() {
        init();
        trie.addTags(tagFactory.getRegisteredTagsNames());
        for (char nextSymbol : template.toCharArray()) {
            switch (currentState) {
                case READ_TAG:
                    readTag(nextSymbol);
                    break;

                case READ_ARG_PREFIX:
                    readArgPrefix(nextSymbol);
                    break;

                case READ_ARG_BODY:
                    readArgBody(nextSymbol);
                    break;

                case READ_ARG_SUFFIX:
                    readArgSuffix(nextSymbol);
                    break;

                case READ_TAG_BODY:
                    doReadTagBody(nextSymbol);
                    break;

                case READ_TAG_BODY_END:
                    doReadTagBodyEnd(nextSymbol);
                    break;
            }
        }
        return generatedTemplate.toString();
    }

    private void doReadTagBody(char nextSymbol) {
        String tagName = trie.go(nextSymbol);
        if (tagName != null) {
            if (tagFactory.get(tagName).hasBody()) {
                nestedTags++;
            }
            trie.reset();
        }

        if (!TAG_BODY_END.startsWith(readTagBodyEnd + nextSymbol)) {
            readTagBody.append(nextSymbol);
        } else {
            currentState = State.READ_TAG_BODY_END;
            doReadTagBodyEnd(nextSymbol);
        }
    }

    private void doReadTagBodyEnd(char nextSymbol) {
        readTagBodyEnd += nextSymbol;
        if (!TAG_BODY_END.startsWith(readTagBodyEnd)) {
            throw new InvalidTagException();
        }

        if (nestedTags > 0) {
            readTagBody.append(nextSymbol);
        }

        if (TAG_BODY_END.equals(readTagBodyEnd)) {
            if (nestedTags == 0) {
                generate();
                currentState = State.READ_TAG;
            } else {
                nestedTags--;
                readTagBodyEnd = "";
                currentState = State.READ_TAG_BODY;
            }
        }
    }

    private void init() {
        currentState = State.READ_TAG;
        generatedTemplate = new StringBuilder();
        trie = new Trie();
        initTag();
    }

    private void initTag() {
        currentTag = null;
        readTagPrefix = "";
        readTagSuffix = "";
        readTagArg = "";
        readTagBody = new StringBuilder();
        readTagBodyEnd = "";
        nestedTags = 0;
    }

    enum State {
        READ_TAG, READ_ARG_BODY, READ_ARG_PREFIX, READ_ARG_SUFFIX, READ_TAG_BODY, READ_TAG_BODY_END
    }

    class Vertex {
        private Map<Character, Integer> nextVertices = new HashMap<>();
        private String tagName;

        public Map<Character, Integer> getNextVertices() {
            return nextVertices;
        }

        public void setNextVertices(Map<Character, Integer> nextVertices) {
            this.nextVertices = nextVertices;
        }

        public String getTagName() {
            return tagName;
        }

        public void setTagName(String tagName) {
            this.tagName = tagName;
        }
    }

    class Trie {
        private List<Vertex> vertices = new ArrayList<>();
        private int pos = 0;

        public Trie() {
            vertices.add(new Vertex());
        }

        public void reset() {
            pos = 0;
        }

        void addTag(String tagName) {
            int currentVertex = 0;
            for (char item : tagName.toCharArray()) {
                if (!vertices.get(currentVertex).getNextVertices().containsKey(item)) {
                    vertices.add(new Vertex());
                    vertices.get(currentVertex).getNextVertices().put(item, vertices.size() - 1);
                }
                currentVertex = vertices.get(currentVertex).getNextVertices().get(item);
            }
            vertices.get(currentVertex).tagName = tagName;
        }

        void addTags(Collection<String> tags) {
            for (String tag : tags) {
                addTag(tag);
            }
        }

        private String go(char nextSymbol) {
            if (vertices.get(pos).getNextVertices().containsKey(nextSymbol)) {
                pos = vertices.get(pos).getNextVertices().get(nextSymbol);
                return vertices.get(pos).tagName;
            } else {
                pos = 0;
                return null;
            }
        }
    }
}

