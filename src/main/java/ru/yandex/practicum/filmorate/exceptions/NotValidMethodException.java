package ru.yandex.practicum.filmorate.exceptions;

public class NotValidMethodException extends RuntimeException{
    public NotValidMethodException(String message){
        super(message);
    }
}
