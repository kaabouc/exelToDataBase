package com.example.exeltodatabase.repositories;

import com.example.exeltodatabase.domain.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProduitRepository  extends JpaRepository<Produit,Long> {
}
