package ru.practicum.ewmServer.likes.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.ewmServer.events.model.EventModel;
import ru.practicum.ewmServer.users.model.UserModel;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity(name = "event_likes")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikesModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne()
    @JoinColumn(name = "event_id")
    private EventModel event;
    @ManyToOne()
    @JoinColumn(name = "user_id")
    private UserModel user;
    @Column(name = "is_like")
    private Boolean isLike;
    @CreationTimestamp
    private LocalDateTime createdDate;

}
