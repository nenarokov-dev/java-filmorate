package ru.yandex.practicum.filmorate.dao.mappers;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class IntegerMapper implements RowMapper<Integer> {
    String columnName;

    public IntegerMapper(String columnName) {
        this.columnName = columnName;
    }

    @Override
    public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
        return rs.getInt(columnName);
    }
}
