package ru.practicum.ewmServer.compilations.model;

import lombok.Data;
import ru.practicum.ewmServer.events.model.EventModel;

import javax.persistence.*;
import java.util.List;

@Data
@Entity(name = "compilations")
public class CompilationModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToMany
    @JoinTable(name = "compilations_to_event", joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    private List<EventModel> events;
    @Column(name = "pinned")
    private Boolean pinned;
    @Column(name = "title", nullable = false, length = 50)
    private String title;
}
