package ru.practicum.ewmServer.events.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import ru.practicum.ewmServer.categories.model.CategoryModel;
import ru.practicum.ewmServer.events.enums.EventStatus;
import ru.practicum.ewmServer.users.model.UserModel;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Entity(name = "events")
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@SecondaryTable(name = "v_event_rating",
        pkJoinColumns = @PrimaryKeyJoinColumn(name = "event_id"))
public class EventModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "annotation", length = 2000)
    private String annotation;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private CategoryModel category;
    @Column(name = "create_date")
    @CreationTimestamp
    private LocalDateTime createdDate;
    @Column(name = "description", length = 7000)
    private String description;
    @Column(name = "event_date")
    private LocalDateTime eventDate;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "initiator_id")
    private UserModel initiator;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id")
    private LocationModel location;
    @Column(name = "paid")
    private Boolean paid;
    @Column(name = "participant_limit")
    private Integer participantLimit;
    @Column(name = "published_date")
    private LocalDateTime publisherDate;
    @Column(name = "request_moderation")
    private Boolean requestModeration;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status_id")
    private EventStatus state;
    @Column(name = "title", length = 120)
    private String title;
    @Column(name = "views")
    private Long views;
    @Column(name = "confirmed_requests")
    private int confirmedRequestsCount;
    @Column(table = "v_event_rating", name = "rating")
    private Integer rating;

    public Long getViews() {
        return Objects.requireNonNullElse(views, 0L);
    }
}
