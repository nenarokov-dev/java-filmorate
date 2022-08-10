package ru.yandex.practicum.filmorate.annotations;

import org.yaml.snakeyaml.scanner.Constant;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.time.LocalDate;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = IsCorrectReleaseDate.class)
@Documented
public @interface IsCorrectLocalData {

    String message() default "{IsCorrectLocalData.invalid}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    String date();

}