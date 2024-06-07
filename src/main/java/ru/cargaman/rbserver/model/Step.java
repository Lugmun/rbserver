package ru.cargaman.rbserver.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "step")
public class Step {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer number;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Recipe recipe;
}
