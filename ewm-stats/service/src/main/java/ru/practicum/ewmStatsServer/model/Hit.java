package ru.practicum.ewmStatsServer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity(name = "ewm_hits")
@Table(name = "ewm_hits")
@Builder
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
public class Hit {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "app", nullable = false)
    private String app;
    @Column(name = "uri", nullable = false)
    private String uri;
    @Column(name = "ip", nullable = false)
    private String ip;
    @Column(name = "hit_time", nullable = false)
    private LocalDateTime timestamp;
}
