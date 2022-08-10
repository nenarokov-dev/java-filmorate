package ru.yandex.practicum.filmorate.exceptions;
//если я верно понял - под бином понимается любой создаваемый класс внутри спринга, поэтому чтобы обобщить
//FilmAlreadyCreatedException и UserAlreadyCreatedException, решил назвать BeanAlreadyCreatedException.

public class BeanAlreadyCreatedException extends RuntimeException{

    public BeanAlreadyCreatedException(String message){
        super(message);
    }
}
