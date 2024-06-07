package ru.cargaman.rbserver.repository;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.cargaman.rbserver.model.Recipe;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Integer> {
}
