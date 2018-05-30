package io.pivotal.pal.tracker;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

@Repository
public class JdbcTimeEntryRepository implements TimeEntryRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcTimeEntryRepository(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement statement = con.prepareStatement(
                    "insert into time_entries (id, project_id, user_id, date, hours) values (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            statement.setLong(1, timeEntry.getId());
            statement.setLong(2, timeEntry.getProjectId());
            statement.setLong(3, timeEntry.getUserId());
            statement.setDate(4, Date.valueOf(timeEntry.getDate()));
            statement.setInt(5, timeEntry.getHours());
            return statement;
        }, keyHolder);
        long id = keyHolder.getKey().longValue();
        return new TimeEntry(id, timeEntry.getProjectId(), timeEntry.getUserId(), timeEntry.getDate(), timeEntry.getHours());
    }

    @Override
    public TimeEntry find(long id) {
        try {
            TimeEntry timeEntry = jdbcTemplate.queryForObject(
                    "select project_id, user_id, date, hours from time_entries where id=?",
                    (rs, rowNum) -> new TimeEntry(
                            id,
                            rs.getLong(1),
                            rs.getLong(2),
                            LocalDate.parse(rs.getDate(3).toString()),
                            rs.getInt(4)),
                    id);
            return timeEntry;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<TimeEntry> list() {
        return jdbcTemplate.query(
                "select id, project_id, user_id, date, hours from time_entries",
                (rs, rowNum) -> new TimeEntry(
                        rs.getLong(1),
                        rs.getLong(2),
                        rs.getLong(3),
                        LocalDate.parse(rs.getDate(4).toString()),
                        rs.getInt(5)));
    }

    @Override
    public TimeEntry update(long id, TimeEntry timeEntry) {
        jdbcTemplate.update(con -> {
            PreparedStatement statement = con.prepareStatement(
                    "update time_entries set project_id=?, user_id=?, date=?, hours=? where id=?"
            );
            statement.setLong(1, timeEntry.getProjectId());
            statement.setLong(2, timeEntry.getUserId());
            statement.setDate(3, Date.valueOf(timeEntry.getDate()));
            statement.setInt(4, timeEntry.getHours());
            statement.setLong(5, id);
            return statement;
        });
        return new TimeEntry(id, timeEntry.getProjectId(), timeEntry.getUserId(), timeEntry.getDate(), timeEntry.getHours());
    }

    @Override
    public TimeEntry delete(long id) {
        jdbcTemplate.update("delete from time_entries where id=?", id);
        return null;
    }
}
