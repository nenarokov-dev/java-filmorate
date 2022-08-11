package ru.yandex.practicum.filmorate.annotations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.NotNull;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class IsCorrectReleaseDate implements ConstraintValidator<IsCorrectLocalData, LocalDate> {
    int year;
    int day;
    int month;

    @Override
    public void initialize(IsCorrectLocalData isCorrectLocalData) {
        this.year = isCorrectLocalData.year();
        this.day = isCorrectLocalData.day();
        this.month = isCorrectLocalData.month();
    }

    @Override
    public boolean isValid(@NotNull LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        } else {
            try {
                return value.isAfter(LocalDate.of(year, month, day));
            } catch (DateTimeException e) {
                return false;
            }
        }

    }

}
