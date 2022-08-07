package ru.yandex.practicum.filmorate.annotations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class IsCorrectReleaseDate implements ConstraintValidator<IsCorrectLocalData, LocalDate> {

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value != null) {
            return value.isAfter(LocalDate.of(1895,12,28));
        } else {
            return false;
        }

    }

}
