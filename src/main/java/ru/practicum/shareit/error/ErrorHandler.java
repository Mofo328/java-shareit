package ru.practicum.shareit.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.lang.reflect.InvocationTargetException;
@RestControllerAdvice()
public class ErrorHandler {
    @AllArgsConstructor
    @Getter
    public class ErrorResponse {
        private String error;
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler(RequestConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleRequestConflictException(final RequestConflictException e) {
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestException(final BadRequestException e) {
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRuntimeException(final Throwable e) {
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler(InvocationTargetException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleInvocationTargetException(final InvocationTargetException e) {
        return new ErrorResponse(
                e.getMessage()
        );
    }
}
