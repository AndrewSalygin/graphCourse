package ru.andrewsalygin.graph.utils;

public class ConnectionAlreadyExistException extends RuntimeException {
    public ConnectionAlreadyExistException(String message) {
        super(message);
    }
}
