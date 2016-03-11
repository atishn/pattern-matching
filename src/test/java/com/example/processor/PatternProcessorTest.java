package com.example.processor;

import com.example.model.PatternNode;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.collections4.MapUtils.isEmpty;
import static org.apache.commons.collections4.MapUtils.isNotEmpty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by anarlawar on 3/7/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestApplicationConfiguration.class)
public class PatternProcessorTest {

    /**
     * Expected exception.
     */
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Autowired
    private PatternProcessor patternProcessor;

    @Test
    public void testProcessPattern() {

        List<String> patterns = newArrayList();

        patterns.add("*,b,*");
        patterns.add("a,b,c");
        patterns.add("d,b,a");
        patterns.add("*,c,*");

        PatternNode masterPattern = patternProcessor.buildPatternTree(patterns);

        assertNotNull(masterPattern);
        assertNull(masterPattern.getPattern());
        assertTrue(masterPattern.getPosition() == 0);

        Map<String, PatternNode> masterPatternMap = masterPattern.getChildPatterns();
        assertTrue(isNotEmpty(masterPatternMap));
        assertTrue(masterPatternMap.size() == 3);

        PatternNode starPattern = masterPatternMap.get("*");
        assertNotNull(starPattern);
        assertEquals(starPattern.getPosition(), 1);

        assertTrue(isNotEmpty(starPattern.getChildPatterns()));
        assertNull(starPattern.getPattern());
        assertTrue(starPattern.getChildPatterns().size() == 2);

        PatternNode starBPattern = starPattern.getChildPatterns().get("b");
        assertNull(starBPattern.getPattern());
        assertTrue(starBPattern.getChildPatterns().size() == 1);
        assertEquals(starBPattern.getPosition(), 2);


        PatternNode starCPattern = starPattern.getChildPatterns().get("c");
        assertNull(starCPattern.getPattern());
        assertTrue(starCPattern.getChildPatterns().size() == 1);


        PatternNode starBStarPattern = starBPattern.getChildPatterns().get("*");
        assertEquals(starBStarPattern.getPattern(), "*,b,*");
        assertTrue(starBStarPattern.getChildPatterns().size() == 0);
        assertEquals(starBStarPattern.getPosition(), 3);


        PatternNode starCStarPattern = starCPattern.getChildPatterns().get("*");
        assertEquals(starCStarPattern.getPattern(), "*,c,*");
        assertTrue(starCStarPattern.getChildPatterns().size() == 0);
    }

    @Test
    public void testProcessPatternBadPatterns() {

        List<String> patterns = newArrayList();

        patterns.add(",,");
        patterns.add(",A,B,,,");
        PatternNode masterPattern = patternProcessor.buildPatternTree(patterns);

        Map<String, PatternNode> masterPatternModel = masterPattern.getChildPatterns();
        assertNotNull(masterPatternModel);
        assertTrue(isEmpty(masterPatternModel));

        patterns = newArrayList();

        patterns.add(",,");
        patterns.add("C,,B,,,");
        patterns.add(",,B,,,");

        masterPattern = patternProcessor.buildPatternTree(patterns);
        masterPatternModel = masterPattern.getChildPatterns();

        assertNotNull(masterPatternModel);

        assertTrue(isNotEmpty(masterPatternModel));
        assertTrue(masterPatternModel.size() == 1);

        // In this case its not empty. as "C,,B,,," starts with valid character.
        // But insider PatternModel will be null and useless.
        assertNotNull(masterPatternModel.get("C"));
        PatternNode patternCModel = masterPatternModel.get("C");
        assertNull(patternCModel.getPattern());
        assertTrue(isEmpty(patternCModel.getChildPatterns()));


        patterns = newArrayList();
        patterns.add(",,");
        patterns.add("C,  ,B,,,");

        masterPattern = patternProcessor.buildPatternTree(patterns);
        masterPatternModel = masterPattern.getChildPatterns();

        assertNotNull(masterPatternModel);

        assertTrue(isNotEmpty(masterPatternModel));
        assertTrue(masterPatternModel.size() == 1);

        // In this case its not empty. as "C,  ,B,,," starts with valid character.
        // But insider PatternModel will be null and useless.
        assertNotNull(masterPatternModel.get("C"));
        patternCModel = masterPatternModel.get("C");
        assertNull(patternCModel.getPattern());
        assertTrue(isEmpty(patternCModel.getChildPatterns()));
    }


    @Test
    public void testProcessNullPattern() {
        PatternNode masterPattern = patternProcessor.buildPatternTree(null);

        assertTrue(isEmpty(masterPattern.getChildPatterns()));
        List<String> patterns = newArrayList();

        patterns.add(null);
        patterns.add(null);

        masterPattern = patternProcessor.buildPatternTree(patterns);
        assertTrue(isEmpty(masterPattern.getChildPatterns()));

        patterns = newArrayList();

        patterns.add("*,b,*");
        patterns.add("a,b,c");
        patterns.add("");
        patterns.add(null);

        masterPattern = patternProcessor.buildPatternTree(patterns);
        Map<String, PatternNode> patternModelMap = masterPattern.getChildPatterns();
        assertNotNull(patternModelMap);
        assertTrue(isNotEmpty(patternModelMap));
        assertTrue(patternModelMap.size() == 2);
    }

    @Test
    public void testSplitThePattern() {
        assertNull(patternProcessor.splitPattern(null));
        String[] patterns = patternProcessor.splitPattern("*,b,*");
        assertNotNull(patterns);
        assertEquals(patterns.length, 3);
        assertEquals(patterns[0], "*");
        assertEquals(patterns[1], "b");
        assertEquals(patterns[2], "*");

        patterns = patternProcessor.splitPattern("*,,,,,,");
        assertNotNull(patterns);
        assertEquals(patterns.length, 7);
        assertEquals(patterns[0], "*");
        assertEquals(patterns[6], "");

        patterns = patternProcessor.splitPattern(",,,,,,");
        assertNotNull(patterns);
        assertEquals(patterns.length, 7);
        assertEquals(patterns[0], "");
        assertEquals(patterns[6], "");

    }


    @Test
    public void testProcessPath() {
        String[] pathBlocks = patternProcessor.splitPath("/a/b/c/");
        assertNotNull(pathBlocks);
        assertTrue(pathBlocks.length == 3);

        pathBlocks = patternProcessor.splitPath("a/b/c");
        assertNotNull(pathBlocks);
        assertTrue(pathBlocks.length == 3);

        pathBlocks = patternProcessor.splitPath("/a//////b//////c/////");
        assertNotNull(pathBlocks);
        assertTrue(pathBlocks.length == 3);

        pathBlocks = patternProcessor.splitPath("/");
        assertTrue(ArrayUtils.isEmpty(pathBlocks));

        pathBlocks = patternProcessor.splitPath("");
        assertTrue(ArrayUtils.isEmpty(pathBlocks));

        pathBlocks = patternProcessor.splitPath(null);
        assertTrue(ArrayUtils.isEmpty(pathBlocks));

        pathBlocks = patternProcessor.splitPath("   ");
        assertTrue(ArrayUtils.isNotEmpty(pathBlocks));
    }

    @Test
    public void testMatchThePatternToPath() {
        List<String> patterns = newArrayList();

        patterns.add("*,*,c");
        patterns.add("*,b,*");
        patterns.add("a,b,c");
        patterns.add("d,b,a");
        patterns.add("*,c,*");

        List<String> paths = newArrayList();
        paths.add("a/b/c");
        List<String> match = patternProcessor.getMatchingPatterns(patterns, paths);

        assertEquals(match.get(0), "a,b,c");

        // Test the preference over left aligned specifics.
        patterns = newArrayList();
        patterns.add("*,*,c");
        patterns.add("*,b,*");

        paths = newArrayList();
        paths.add("a/b/c");
        match = patternProcessor.getMatchingPatterns(patterns, paths);

        assertEquals(match.get(0), "*,b,*");

        paths = newArrayList();
        paths.add("a/b/d");
        match = patternProcessor.getMatchingPatterns(patterns, paths);

        assertEquals(match.get(0), "*,b,*");


    }


    @Test
    public void testMatchThePatternToPathForDuplicates() {
        List<String> patterns = newArrayList();

        patterns.add("*,*,c");
        patterns.add("*,*,c");
        patterns.add("*,*,c");
        patterns.add("*,b,*");
        patterns.add("a,b,c");
        patterns.add("a,b,c");
        patterns.add("d,b,a");
        patterns.add("*,c,*");

        List<String> paths = newArrayList();
        paths.add("a/b/c");
        List<String> match = patternProcessor.getMatchingPatterns(patterns, paths);

        assertEquals(match.get(0), "a,b,c");

    }

    @Test
    public void testMatchThePatternRaceCondition() {
        List<String> patterns = newArrayList();

        patterns.add("a,b,c,d,e,g");
        patterns.add("a,b,c,*,e,f");
        patterns.add("a,b,c,d,*,f");


        List<String> paths = newArrayList();
        paths.add("a/b/c/d/e/f");
        List<String> match = patternProcessor.getMatchingPatterns(patterns, paths);

        assertEquals(match.get(0), "a,b,c,d,*,f");
    }


    @Test
    public void testMatchThePatternVariousSizes() {
        List<String> patterns = newArrayList();

        patterns.add("a,b,c");
        patterns.add("*,*");
        patterns.add("a,b,c,d,e,g");
        patterns.add("a,b,c,*,e,f");
        patterns.add("a,b,c,d,*,f");
        patterns.add("*,*,c");


        List<String> paths = newArrayList();
        paths.add("a/b");
        List<String> match = patternProcessor.getMatchingPatterns(patterns, paths);

        assertEquals(match.get(0), "*,*");

        paths = newArrayList();
        paths.add("a/");
        match = patternProcessor.getMatchingPatterns(patterns, paths);

        assertEquals(match.get(0), "NO MATCH");
    }

    @Test
    public void testByTheConditionGivenAtREADME() {

        List<String> patterns = newArrayList();

        patterns.add("*,b,*");
        patterns.add("a,*,*");
        patterns.add("*,*,c");
        patterns.add("foo,bar,baz");
        patterns.add("w,x,*,*");
        patterns.add("*,x,y,z");


        List<String> paths = newArrayList();
        paths.add("/w/x/y/z/");
        paths.add("a/b/c");
        paths.add("foo/");
        paths.add("foo/bar/");
        paths.add("foo/bar/baz/");

        List<String> match = patternProcessor.getMatchingPatterns(patterns, paths);

        assertEquals(match.get(0), "*,x,y,z");
        assertEquals(match.get(1), "a,*,*");
        assertEquals(match.get(2), "NO MATCH");
        assertEquals(match.get(3), "NO MATCH");
        assertEquals(match.get(4), "foo,bar,baz");


    }

    @Test
    public void calculateWildCardFromPattern() {
        assertTrue(patternProcessor.calculateWildCards(null, null) == 0);
        assertTrue(patternProcessor.calculateWildCards("", "*") == 0);
        assertTrue(patternProcessor.calculateWildCards(" ", "*") == 0);
        assertTrue(patternProcessor.calculateWildCards("a", "*") == 0);
        assertTrue(patternProcessor.calculateWildCards("a,b", "*") == 0);
        assertTrue(patternProcessor.calculateWildCards("a,b,*", "*") == 1);
        assertTrue(patternProcessor.calculateWildCards("*,*", "*") == 2);
        assertTrue(patternProcessor.calculateWildCards(",*,*,", "*") == 2);

        assertTrue(patternProcessor.calculateWildCards(",,,,*,,,,", "*") == 1);


    }
}
