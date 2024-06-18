package ru.cargaman.rbserver.request;

public record StepEditRequest(
        Integer id,
        String name,
        Integer number
) {
}
