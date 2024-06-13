package ru.cargaman.rbserver.request;

public record ProductEditRequest(
        Integer productId,
        String name,
        String measure
) {
}
