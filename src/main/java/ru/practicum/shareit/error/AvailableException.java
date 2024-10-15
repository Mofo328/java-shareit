package ru.practicum.shareit.error;

public class AvailableException extends RuntimeException {
    public AvailableException(String message) {
        super(message);
    }
}