package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.time.LocalDate;

@Data
public class Film {
    private int id;

    @NotBlank(message = "Имя не должно быть пустым")
    private String name;
    @Size(max = 200,message = "Описание не должно превышать 200 символов.")
    private String description;
    @Min(value = -27032,message = "Дата релиза фильма не должна быть ранее 28 декабря 1895 года.")
    private Long releaseData;
    @Min(value = 1)
    private Long duration;//сукунды

    public Film(String name,LocalDate localDate,String description,Duration duration) {
        this.name=name;
        this.releaseData=localDate.toEpochDay();
        this.description=description;
        this.duration=duration.getSeconds();
    }

    public Film(String name,Long releaseData,String description,Long duration) {
        this.name=name;
        this.releaseData=releaseData;
        this.description=description;
        this.duration=duration;
    }

    public Film() {
    }
}

