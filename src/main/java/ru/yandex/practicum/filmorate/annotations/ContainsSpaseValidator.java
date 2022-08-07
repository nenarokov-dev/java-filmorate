package ru.yandex.practicum.filmorate.annotations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ContainsSpaseValidator implements ConstraintValidator<IsContainsSpase, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value != null && !value.isEmpty()) {
            return !value.contains(" ");
        }
        return true;
    }

}
