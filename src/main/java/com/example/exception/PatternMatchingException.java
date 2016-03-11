package com.example.exception;


import org.slf4j.Logger;
import org.springframework.boot.ExitCodeGenerator;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Generic PatternMatching exception .
 */
public class PatternMatchingException extends RuntimeException
        implements ExitCodeGenerator {

    /**
     * The constant LOG.
     */
    private static final Logger LOG = getLogger(PatternMatchingException.class);

    /**
     * Gets exit code.
     *
     * @return the exit code
     */
    @Override
    public int getExitCode() {
        return 500;
    }
}
