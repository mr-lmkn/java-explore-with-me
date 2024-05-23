package ru.practicum.ewmStatsServer.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewmStatsServer.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

@EnableJpaRepositories
public interface EwmStatsRepository extends JpaRepository<Hit, Long> {
    @Query(value = "\n SELECT max(id) as id, max(ip) as ip, max(hit_time) as hit_time "
            + "          , e.app, e.uri \n"
            + "          , CASE WHEN :uniqueIp = true THEN count(distinct e.ip) ELSE count(e.ip) END as hits \n"
            + " FROM ewm_hits e \n"
            + " WHERE e.hit_time BETWEEN :from AND :to \n"
            + "   AND (e.uri IN (:uriList) OR :uriList IS NULL)\n"
            + " GROUP BY e.app, e.uri, e.ip \n",
            nativeQuery = true)
    List<Hit> getStats(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("uriList") List<String> uriList,
            @Param("uniqueIp") Boolean uniqueIp);
}
