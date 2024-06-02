package ru.practicum.ewmServer.users.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "users")
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    @Column(name = "is_admin")
    private Boolean isAdmin;
}
