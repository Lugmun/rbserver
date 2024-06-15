package ru.cargaman.rbserver.response;

import java.util.List;

public record RecipeFullResponse(
        Integer id,
        String name,
        String description,
        Integer time,
        Integer portions,
        boolean isPublic,
        String author,
        List<IngredientResponse> ingredients,
        List<StepResponse> steps
) {
}
