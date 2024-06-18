package ru.cargaman.rbserver.request;

public record CommentEditRequest(
        Integer id,
        String message,
        Integer mark
) {
}
