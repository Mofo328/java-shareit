package ru.practicum.shareit.error;

public class RequestConflictException extends RuntimeException{
    public RequestConflictException(String message) {
        super(message);
    }
}
