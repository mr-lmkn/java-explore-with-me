package ru.practicum.ewmServer.categories.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;


@Data
@Builder
@Entity
@Table(name = "categories")
@AllArgsConstructor
@NoArgsConstructor
public class CategoryModel {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", unique = true)
    private String name;
}
