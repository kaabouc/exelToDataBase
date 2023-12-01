package com.example.exeltodatabase.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;


import java.math.BigDecimal;
@Entity
@Getter
@Setter
@Table(name = "Produit")
public class Produit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotNull
    private String nom;
    private String description;
    private BigDecimal prix;

    public Produit() {

    }
    public Produit(Long id, @NotNull String nom, String description, BigDecimal prix) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.prix = prix;
    }


}
