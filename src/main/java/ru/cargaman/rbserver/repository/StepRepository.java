package ru.cargaman.rbserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.cargaman.rbserver.model.Step;

@Repository
public interface StepRepository extends JpaRepository<Step, Integer> {
}
