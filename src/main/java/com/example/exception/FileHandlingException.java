package com.example.exception;


import org.springframework.boot.ExitCodeGenerator;

/**
 * Generic FileHandlingException exception .
 */
public class FileHandlingException extends RuntimeException implements ExitCodeGenerator {


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
