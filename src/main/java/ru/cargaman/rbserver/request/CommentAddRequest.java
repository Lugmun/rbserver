package ru.cargaman.rbserver.request;

import jakarta.persistence.criteria.CriteriaBuilder;

public record CommentAddRequest(
        String message,
        Integer mark
) {
}
