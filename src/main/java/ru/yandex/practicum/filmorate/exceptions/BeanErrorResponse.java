package ru.yandex.practicum.filmorate.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BeanErrorResponse {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(final BeanNotFoundException e) {
        return new ErrorResponse(
                "Некорректный запрос", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(final BeanAlreadyCreatedException e) {
        return new ErrorResponse(
                "Некорректный запрос", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(final FriendshipError e) {
        return new ErrorResponse(
                "Некорректный запрос", e.getMessage()
        );
    }

}

@Getter
@RequiredArgsConstructor
class ErrorResponse {

    private final String error;
    private final String description;

}
