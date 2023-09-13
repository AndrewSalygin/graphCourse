package ru.andrewsalygin.graph.utils;

public class ConnectionNotExistException extends RuntimeException {
    public ConnectionNotExistException(String message) {
        super(message);
    }
}
