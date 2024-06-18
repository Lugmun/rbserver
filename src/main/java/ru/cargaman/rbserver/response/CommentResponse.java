package ru.cargaman.rbserver.response;

public record CommentResponse(
        Integer id,
        String message,
        String user,
        Integer mark,
        Integer recipeId
) {
}
