package ru.yandex.practicum.filmorate.exceptions;

public class AlreadyCreatedException extends RuntimeException {

    public AlreadyCreatedException(String message) {
        super(message);
    }
}
