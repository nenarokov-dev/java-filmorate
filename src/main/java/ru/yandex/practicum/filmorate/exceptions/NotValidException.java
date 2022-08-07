package ru.yandex.practicum.filmorate.exceptions;

import javax.validation.ValidationException;

public class NotValidException extends ValidationException {
    public NotValidException(String message){
        super(message);
    }

}