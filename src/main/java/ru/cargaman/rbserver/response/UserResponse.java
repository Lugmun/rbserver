package ru.cargaman.rbserver.response;

import lombok.AllArgsConstructor;
import lombok.Data;

public record UserResponse (
        String login,
        String password,
        String role
) {}
