package ru.cargaman.rbserver.request;

public record IngredientEditRequest(
        Integer id,
        Integer amount,
        Integer productId
) {
}
