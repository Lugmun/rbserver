package ru.cargaman.rbserver.response;

public record ProductResponse(
        Integer id,
        String name,
        String measure,
        Boolean isPublic,
        String author
) {
}
