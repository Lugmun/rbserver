package ru.cargaman.rbserver.response;

import jakarta.persistence.criteria.CriteriaBuilder;

public record StepResponse(
        Integer id,
        Integer number,
        String name
) {
}
