package com.example.exception;


import org.springframework.boot.ExitCodeGenerator;

/**
 * Generic InvalidDataException exception .
 */
public class InvalidDataException extends RuntimeException implements ExitCodeGenerator {

    /**
     * Gets exit code.
     *
     * @return the exit code
     */
    @Override
    public int getExitCode() {
        return 403;
    }
}
