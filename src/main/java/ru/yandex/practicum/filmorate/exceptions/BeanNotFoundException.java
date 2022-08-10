package ru.yandex.practicum.filmorate.exceptions;

public class BeanNotFoundException extends RuntimeException{
    public BeanNotFoundException(String message){
        super(message);
    }
}
