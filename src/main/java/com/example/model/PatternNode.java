package com.example.model;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

/**
 * A unique Node entity, a part of Pattern Tree.
 * Created by anarlawar on 3/7/16.
 */
public class PatternNode {

    /**
     * The Pattern.
     */
    private String pattern;
    /**
     * The Position from the origin node.
     */
    private int position;
    /**
     * The Child patterns.
     */
    private Map<String, PatternNode> childPatterns = newHashMap();

    /**
     * Instantiates a new Pattern node.
     *
     * @param position the position
     */
    public PatternNode(final int position) {
        this.position = position;
    }

    /**
     * Gets Child patterns.
     *
     * @return the next patterns
     */
    public Map<String, PatternNode> getChildPatterns() {
        return childPatterns;
    }

    /**
     * Gets pattern.
     *
     * @return the pattern
     */
    public String getPattern() {
        return pattern;
    }

    /**
     * Sets pattern.
     *
     * @param pattern the pattern
     */
    public void setPattern(final String pattern) {
        this.pattern = pattern;
    }

    /**
     * Gets position.
     *
     * @return the position
     */
    public int getPosition() {
        return position;
    }

    /**
     * Sets position.
     *
     * @param position the position
     */
    public void setPosition(final int position) {
        this.position = position;
    }
}
