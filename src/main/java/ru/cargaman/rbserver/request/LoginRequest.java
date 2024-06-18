package ru.cargaman.rbserver.request;

public record LoginRequest(
        String login,
        String password
) {
}
