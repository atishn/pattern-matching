package com.example.processor;

import com.example.exception.FileHandlingException;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Manages the output of the program.
 * Created by anarlawar on 3/7/16.
 */
@Service
public class OutputProcessor {

    /**
     * The constant LOG.
     */
    private static final Logger LOG = getLogger(OutputProcessor.class);

    /**
     * Process output.
     *
     * @param outputFile the output file
     * @param lines      the patterns
     *
     * @throws IOException the iO exception
     */
    public void processOutput(final String outputFile, final List<String> lines)
            throws IOException {
        BufferedWriter bWriter = null;
        try {
            /**
             * Creating output file.
             */

            File file = new File(outputFile);
            Writer w = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
            bWriter = new BufferedWriter(w);

            for (String line : lines) {
                bWriter.write(line);
                bWriter.newLine();
            }

        } catch (IOException ex) {

            LOG.info("Issues with dealing output file" + ex.getMessage());
            throw new FileHandlingException();
        } finally {
            /**
             * Cleaning up
             */
            if (bWriter != null) {
                bWriter.close();
            }
        }
    }
}
