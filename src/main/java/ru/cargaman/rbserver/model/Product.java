package ru.cargaman.rbserver.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String measure;

    @Column(nullable = false)
    private boolean isPublic;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User author;

    @OneToMany(mappedBy = "product")
    private List<Ingredient> ingredients;


}
