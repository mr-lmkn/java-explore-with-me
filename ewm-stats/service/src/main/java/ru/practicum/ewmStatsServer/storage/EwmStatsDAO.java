package ru.practicum.ewmStatsServer.storage;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;
import ru.practicum.ewmStatsDto.StatDto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class EwmStatsDAO implements EvmStatsStorage {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public List<StatDto> getStats(LocalDateTime from, LocalDateTime to
            , List<String> uriList, Boolean emptyList, Boolean uniqueIp) {
        String sqlQuery = "\n SELECT "
                + "            e.app, e.uri \n"
                + "          , CASE WHEN :uniqueIp = true THEN count(distinct e.ip) ELSE count(e.ip) END as hits \n"
                + " FROM ewm_hits e \n"
                + " WHERE e.hit_time BETWEEN :from AND :to \n"
                + "   AND (e.uri IN (:uriList) OR :emptyList = true)\n"
                + " GROUP BY e.app, e.uri, e.ip \n";
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("from", from)
                .addValue("to", to)
                .addValue("uriList", uriList)
                .addValue("emptyList", emptyList)
                .addValue("uniqueIp", uniqueIp);
        log.info("SQL PARAM: {}", parameters.toString());
        List<StatDto> statList = jdbcTemplate.query(sqlQuery
                , parameters
                , this::mapRowToStatDto
        );
        return statList;
    }

    public StatDto mapRowToStatDto(ResultSet resultSet, int i) throws SQLException {
        return StatDto.builder()
                .app(resultSet.getString("APP"))
                .uri(resultSet.getString("URI"))
                .hits(resultSet.getLong("HITS"))
                .build();
    }

}
