package ru.andrewsalygin.graph.core.utils;

/**
 * @author Andrew Salygin
 */
public class ConnectionNotExistException extends RuntimeException {
    public ConnectionNotExistException(String message) {
        super(message);
    }
}
