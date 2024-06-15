package ru.cargaman.rbserver.response;

public record RecipeResponse(
        Integer id,
        String name,
        String description,
        Integer time,
        Integer portions,
        boolean isPublic,
        String author
) {
}
