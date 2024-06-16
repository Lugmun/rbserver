package ru.cargaman.rbserver.request;

public record IngredientAddRequest(
        Integer amount,
        Integer productId
) {
}
