package ru.andrewsalygin.graph.utils;

/**
 * @author Andrew Salygin
 */
public class ConnectionAlreadyExistException extends RuntimeException {
    public ConnectionAlreadyExistException(String message) {
        super(message);
    }
}
