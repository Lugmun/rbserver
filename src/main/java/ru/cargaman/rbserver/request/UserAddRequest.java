package ru.cargaman.rbserver.request;

public record UserAddRequest(
        String login,
        String password
) {
}
