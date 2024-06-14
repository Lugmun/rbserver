package ru.cargaman.rbserver.response;

public record IngredientResponse(
        Integer id,
        Integer amount,
        ProductResponse product
) {
}
