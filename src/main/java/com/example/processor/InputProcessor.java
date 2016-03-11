package com.example.processor;

import com.example.constant.Messages;
import com.example.exception.FileHandlingException;
import com.example.exception.InvalidDataException;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.math.NumberUtils.createInteger;
import static org.apache.commons.lang3.math.NumberUtils.isNumber;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * The input file processor.It parses the text file, and get list of patterns and paths.
 * Created by anarlawar on 3/7/16.
 */
@Service
public class InputProcessor {

    /**
     * The constant LOG.
     */
    private static final Logger LOG = getLogger(InputProcessor.class);

    /**
     * Processes the input file and gets the list of patterns and paths.
     *
     * @param inputFile the input file
     *
     * @return the list
     *
     * @throws IOException the iO exception
     */
    public Pair<List<String>, List<String>> processInputFile(final String inputFile)
            throws IOException {

        List<String> patterns = newArrayList();
        List<String> paths = newArrayList();

        BufferedReader bReader = null;
        try {

            File file = new File(inputFile);
            Reader r = new InputStreamReader(new FileInputStream(file), "UTF-8");
            bReader = new BufferedReader(r);
            String numberOfPatterns = bReader.readLine();
            if (!isNumber(numberOfPatterns)) {
                LOG.error("Invalid data in the input file. "
                        + "First line should be a "
                        + "digit representing number of patterns."
                        + Messages.STD_INPUT);
                throw new InvalidDataException();


            }
            int numPatterns = createInteger(numberOfPatterns);
            for (int x = 0; x < numPatterns; x++) {
                String pattern = bReader.readLine();
                if (isBlank(pattern)) {
                    LOG.error("One of the incoming pattern is blank."
                            + "Please fix.");
                    throw new InvalidDataException();
                }
                patterns.add(pattern);
            }

            if (isEmpty(patterns)) {
                LOG.error("Invalid data in the input file. "
                        + "There are no patterns.");
                throw new InvalidDataException();
            }

            String numberOfPaths = bReader.readLine();
            if (!isNumber(numberOfPaths)) {
                LOG.error("Invalid data in the input file. "
                        + "The first line after the patterns should be "
                        + "digit representing number of paths."
                        + Messages.STD_INPUT);

                throw new InvalidDataException(
                );
            }
            int numPaths = createInteger(numberOfPaths);

            for (int x = 0; x < numPaths; x++) {
                String path = bReader.readLine();
                if (isBlank(path)) {
                    LOG.error("One of the incoming path is blank. "
                            + "Please fix.");

                    throw new InvalidDataException();
                }
                paths.add(path);
            }

            if (isEmpty(paths)) {
                LOG.error("Invalid data in the input file."
                        + "There are no paths.");

                throw new InvalidDataException();
            }

        } catch (IOException ex) {
            LOG.error("Issues with dealing input file"
                    + ex.getMessage());

            throw new FileHandlingException();

        } finally {
            if (bReader != null) {
                bReader.close();
            }
        }
        return Pair.of(patterns, paths);
    }

}


