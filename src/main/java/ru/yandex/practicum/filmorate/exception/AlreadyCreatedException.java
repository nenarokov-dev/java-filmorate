package ru.yandex.practicum.filmorate.exception;

public class AlreadyCreatedException extends RuntimeException {

    public AlreadyCreatedException(String message) {
        super(message);
    }
}
