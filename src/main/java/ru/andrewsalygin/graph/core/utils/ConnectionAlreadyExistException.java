package ru.andrewsalygin.graph.core.utils;

/**
 * @author Andrew Salygin
 */
public class ConnectionAlreadyExistException extends RuntimeException {
    public ConnectionAlreadyExistException(String message) {
        super(message);
    }
}
