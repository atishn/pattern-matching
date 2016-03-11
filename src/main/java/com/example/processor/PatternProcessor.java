package com.example.processor;

import com.example.model.PatternNode;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.split;
import static org.apache.commons.lang3.StringUtils.splitByWholeSeparatorPreserveAllTokens;

/**
 * Processor class, manages the processing of the patterns and paths.
 * Created by anarlawar on 3/7/16.
 */
@Service
public class PatternProcessor {


    /**
     * This method returns the list of matching pattern for the given paths.
     * If No Match found, it returns "NO MATCH"
     *
     * @param patterns the patterns
     * @param paths    the paths
     *
     * @return the list
     */
    public List<String> getMatchingPatterns(final List<String> patterns,
                                            final List<String> paths) {

        PatternNode patternNode = buildPatternTree(patterns);

        List<String> pathPatterns = newArrayList();

        for (String path : paths) {
            String[] pathList = splitPath(path);
            String pattern = findTheNearestMatch(patternNode, pathList);
            if (isBlank(pattern)) {
                pathPatterns.add("NO MATCH");
            } else {
                pathPatterns.add(pattern);
            }
        }

        return pathPatterns;
    }

    /**
     * Find the pattern.
     *
     * @param patternNode the pattern model
     * @param pathList    the path list
     *
     * @return the string
     */
    public String findTheNearestMatch(final PatternNode patternNode,
                                      final String[] pathList) {

        String matchingPattern = null;
        int pos = patternNode.getPosition();

        if (pathList.length > pos) {
            int wildCardsInPattern = 0;

            List<PatternNode> alternateMatches = newArrayList();
            Map<String, PatternNode> patternMap = patternNode.getChildPatterns();

            for (String pathBlock : Arrays.copyOfRange(pathList, pos, pathList.length)) {
                if (isNotBlank(pathBlock)) {
                    if (patternMap.containsKey(pathBlock)) {
                        matchingPattern = patternMap.get(pathBlock).getPattern();
                        if (patternMap.containsKey("*")) {
                            // Look and Record any possible alternates
                            alternateMatches.add(0, patternMap.get("*"));
                        }
                        patternMap = patternMap.get(pathBlock).getChildPatterns();
                    } else if (patternMap.containsKey("*")) {
                        matchingPattern = patternMap.get("*").getPattern();
                        patternMap = patternMap.get("*").getChildPatterns();
                        wildCardsInPattern++;
                    } else {
                        break;
                    }
                }
            }

            // 1. If No match found or
            // 2. Wildcards in the solution greater than 1
            // Find the better solution if any alternatives exists.
            if (isBlank(matchingPattern) || wildCardsInPattern > 1) {
                if (CollectionUtils.isNotEmpty(alternateMatches)) {
                    String alternatePatternWithLessWildcards = null;
                    int minWildCardsInAlternate = Integer.MAX_VALUE;

                    for (PatternNode model : alternateMatches) {
                        String alternatePattern = findTheNearestMatch(model, pathList);
                        if (isNotBlank(alternatePattern)) {
                            int wildCardNum = calculateWildCards(alternatePattern, "*");
                            if (wildCardNum < minWildCardsInAlternate) {
                                minWildCardsInAlternate = wildCardNum;
                                alternatePatternWithLessWildcards = alternatePattern;
                            }
                        }
                    }

                    if (wildCardsInPattern > minWildCardsInAlternate
                            || isBlank(matchingPattern)) {
                        matchingPattern = alternatePatternWithLessWildcards;
                    }
                }
            }
        }
        return matchingPattern;
    }

    /**
     * This method creates the Tree data structure for the given list of patterns.
     * Every block of pattern will be an individual node.
     * for Eg. the pattern "a,b,c" will have three nodes with root as "a"
     * Every other pattern either will be unique or shares
     * some part of the tree branch as per common blocks.
     * It also records the position from the root.
     * <p>
     * For "a,b,c" and "a,b,d"
     * <p>
     * PatternNode will be
     * <p>
     * root 0
     *      - a   1
     *           - b  2
     *               -c 3
     *               -d 3
     * <p>
     * This is one time process.
     *
     * @param patterns the list of patterns
     *
     * @return pattern model
     */
    public PatternNode buildPatternTree(final List<String> patterns) {
        PatternNode rootNode = new PatternNode(0);

        if (CollectionUtils.isNotEmpty(patterns)) {
            for (int index = 0; index < patterns.size(); index++) {
                String pattern = patterns.get(index);
                String[] splitedPattern = splitPattern(pattern);
                if (isNotEmpty(splitedPattern)) {

                    PatternNode patternNode = null;
                    Map<String, PatternNode> patternMap =
                            rootNode.getChildPatterns();

                    boolean validStatus = true;
                    int position = 0;
                    for (String patKey : splitedPattern) {
                        if (StringUtils.isBlank(patKey)) {
                            validStatus = false;
                            break;
                        }
                        position++;
                        if (!patternMap.containsKey(patKey)) {
                            patternNode = new PatternNode(position);
                            patternMap.put(patKey, patternNode);
                            patternMap = patternNode.getChildPatterns();
                        } else {
                            patternNode = patternMap.get(patKey);
                            patternMap = patternNode.getChildPatterns();
                        }
                    }
                    //Blank Entry in Pattern. Ignore and Move to next pattern.
                    if (!validStatus) {
                        continue;
                    }
                    patternNode.setPattern(pattern);
                }
            }
        }
        return rootNode;
    }

    /**
     * Split the pattern line by comma separated.
     * If unnecessary comma comes, it considers as "".
     *
     * @param pattern the pattern
     *
     * @return string [ ] Pattern Blocks
     */
    public String[] splitPattern(final String pattern) {
        String[] splits = splitByWholeSeparatorPreserveAllTokens(pattern, ",");
        return splits;

    }

    /**
     * Split the path by "/". If additional "/" comes,
     * just ignore it as path may come with "///"
     *
     * @param path the path
     *
     * @return string [ ] Path blocks.
     */
    public String[] splitPath(final String path) {
        String[] splits = split(path, "/");
        return splits;
    }


    /**
     * Calculate the number of wild cards from pattern.
     *
     * @param pattern  the pattern
     * @param wildcard the wildcard
     *
     * @return the int
     */
    public int calculateWildCards(final String pattern, final String wildcard) {
        int wildCards = 0;
        String[] splits = splitPattern(pattern);
        if (isNotEmpty(splits)) {
            for (String patternItem : splits) {
                if (StringUtils.equals(patternItem, wildcard)) {
                    wildCards++;
                }
            }
        }
        return wildCards;
    }


}
