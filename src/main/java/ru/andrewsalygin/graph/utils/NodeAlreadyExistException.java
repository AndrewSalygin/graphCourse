package ru.andrewsalygin.graph.utils;

/**
 * @author Andrew Salygin
 */
public class NodeAlreadyExistException extends RuntimeException {
    public NodeAlreadyExistException(String message) {
        super(message);
    }
}
