package ru.yandex.practicum.filmorate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class test {
    public static final long today = LocalDate.now().toEpochDay();
    public static void main(String[] args) {
        GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting().serializeNulls();
        Gson gson = gsonBuilder.create();
        User user = new User("ggg@mail.ru","Gsdsf sdfsd", LocalDate.of(2022,8,7).toEpochDay());
        String stringUser = gson.toJson(user);
        System.out.println(stringUser);
        System.out.println(gson.fromJson(stringUser,User.class));

    }
}
