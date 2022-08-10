package ru.yandex.practicum.filmorate.annotations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.NotNull;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class IsCorrectReleaseDate implements ConstraintValidator<IsCorrectLocalData, LocalDate> {
    String checkDate;

    @Override
    public void initialize(IsCorrectLocalData isCorrectLocalData) {
        this.checkDate = isCorrectLocalData.date();
    }

    @Override
    public boolean isValid(@NotNull LocalDate value, ConstraintValidatorContext context) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (value == null) {
            return false;
        } else {
            try {
                LocalDate parseCheckDate = LocalDate.parse(checkDate,dtf);
                return value.isAfter(parseCheckDate);
            } catch (DateTimeException e) {
                return false;
            }
        }

    }

}
