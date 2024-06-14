package ru.cargaman.rbserver.request;

import jakarta.persistence.criteria.CriteriaBuilder;

public record RecipeAddRequest(
        String name,
        String description,
        Integer time,
        Integer portions
){
}
