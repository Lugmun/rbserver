package ru.cargaman.rbserver.request;

public record RecipeEditRequest(
        Integer id,
        String name,
        String description,
        Integer time,
        Integer portions
) {
}
