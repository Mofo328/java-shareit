package ru.practicum.shareit.error;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;

public class ErrorHandlerTest {

    private ErrorHandler errorHandler = new ErrorHandler();

    @Test
    public void testHandleNotFoundException() {
        NotFoundException exception = new NotFoundException("Resource not found");
        ErrorHandler.ErrorResponse response = errorHandler.handleNotFoundException(exception);
        assertEquals("Resource not found", response.getError());
    }

    @Test
    public void testHandleRequestConflictException() {
        RequestConflictException exception = new RequestConflictException("Request conflict");
        ErrorHandler.ErrorResponse response = errorHandler.handleRequestConflictException(exception);
        assertEquals("Request conflict", response.getError());
    }

    @Test
    public void testHandleBadRequestException() {
        BadRequestException exception = new BadRequestException("Bad request");
        ErrorHandler.ErrorResponse response = errorHandler.handleBadRequestException(exception);
        assertEquals("Bad request", response.getError());
    }

    @Test
    public void testHandleUserNotOwnerException() {
        UserNotOwnerException exception = new UserNotOwnerException("User is not owner");
        ErrorHandler.ErrorResponse response = errorHandler.handleUserNotOwnerException(exception);
        assertEquals("User is not owner", response.getError());
    }

    @Test
    public void testHandleRuntimeException() {
        Throwable exception = new RuntimeException("Internal error");
        ErrorHandler.ErrorResponse response = errorHandler.handleRuntimeException(exception);
        assertEquals("Internal error", response.getError());
    }

}