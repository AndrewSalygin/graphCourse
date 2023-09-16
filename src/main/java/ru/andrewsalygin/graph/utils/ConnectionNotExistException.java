package ru.andrewsalygin.graph.utils;

/**
 * @author Andrew Salygin
 */
public class ConnectionNotExistException extends RuntimeException {
    public ConnectionNotExistException(String message) {
        super(message);
    }
}
