package com.example.processor;

import com.example.exception.FileHandlingException;
import com.example.exception.InvalidDataException;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by anarlawar on 3/7/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestApplicationConfiguration.class)
public class InputProcessorTest {

    /**
     * Expected exception.
     */
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Autowired
    private InputProcessor inputProcessor;

    private String filePath = "./src/test/resources/";

    @Test
    public void testProcessInputFileHappy() throws Exception{
        Pair<List<String>, List<String>> inputs = inputProcessor.processInputFile(filePath + "input-happy.txt");
        assertNotNull(inputs);

        List<String> patterns = inputs.getLeft();
        List<String> paths = inputs.getRight();

        assertTrue(isNotEmpty(patterns));
        assertTrue(isNotEmpty(paths));

        assertTrue(patterns.size() == 6);
        assertTrue(paths.size() == 5);
    }

    @Test
    public void testProcessInputBad() throws Exception{
        exception.expect(InvalidDataException.class);
        inputProcessor.processInputFile(filePath + "input-blank.txt");

        exception.expect(InvalidDataException.class);
        inputProcessor.processInputFile(filePath + "input-bad.txt");

        exception.expect(InvalidDataException.class);
        inputProcessor.processInputFile(filePath + "input-blank-pattern.txt");

        exception.expect(InvalidDataException.class);
        inputProcessor.processInputFile(filePath + "input-less-pattern.txt");

        exception.expect(FileHandlingException.class);
        inputProcessor.processInputFile(filePath + "xxxxxx");

    }

}
