package ru.cargaman.rbserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.cargaman.rbserver.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
}
