package ru.yandex.practicum.filmorate.dao.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.FilmRating;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RatingMapper implements RowMapper<FilmRating> {

    @Override
    public FilmRating mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new FilmRating(rs.getInt("rating_id"), rs.getString("rating"));
    }
}
