package com.example;

import com.example.exception.FileHandlingException;
import com.example.exception.InvalidDataException;
import com.example.processor.InputProcessor;
import com.example.processor.OutputProcessor;
import com.example.processor.PatternProcessor;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.List;

import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.slf4j.LoggerFactory.getLogger;


/**
 * Pattern Matching Boot application configuration and main class.
 */
@SpringBootApplication
public class PatternMatchingApplication implements CommandLineRunner {

    /**
     * The constant LOG.
     */
    private static final Logger LOG = getLogger(PatternMatchingApplication.class);

    /**
     * The Pattern processor.
     */
    @Autowired
    private PatternProcessor patProcessor;

    /**
     * The Input processor.
     */
    @Autowired
    private InputProcessor inProcessor;

    /**
     * The Output processor.
     */
    @Autowired
    private OutputProcessor outProcessor;

    /**
     * Main Method to start Spring Boot application.
     *
     * @param args String Main Args
     */
    public static void main(final String[] args) {
        SpringApplication.run(PatternMatchingApplication.class, args);
    }

    /**
     * Run void.
     *
     * @param args the args
     */
    @Override
    public void run(final String... args) {

        LOG.info("Start-----------------------------------------");
        if (isNotEmpty(args) && args.length == 2) {
            String inputFile = args[0];
            String outputFile = args[1];
            if (isNotBlank(inputFile) && isNotBlank(outputFile)) {
                try {
                    // Get the patterns and Paths
                    Pair<List<String>, List<String>> input =
                            inProcessor.processInputFile(inputFile);

                    List<String> patterns = input.getLeft();
                    List<String> paths = input.getRight();

                    // Find the matches
                    List<String> matches = patProcessor.
                            getMatchingPatterns(patterns, paths);

                    // Dump the result to the output file.
                    outProcessor.processOutput(outputFile, matches);

                } catch (InvalidDataException | FileHandlingException | IOException ex) {
                    LOG.error("Unknown exception occured.");
                }
            } else {
                LOG.error("Please include valid input and output file path.");
            }
        } else {
            LOG.error("Program expects exactly two agruments. "
                    + "Input file and Output file path.");
        }
        LOG.info("End-----------------------------------------");

    }


}
