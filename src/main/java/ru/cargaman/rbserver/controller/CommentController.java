package ru.cargaman.rbserver.controller;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.cargaman.rbserver.model.Comment;
import ru.cargaman.rbserver.request.CommentAddRequest;
import ru.cargaman.rbserver.request.CommentEditRequest;
import ru.cargaman.rbserver.response.CommentResponse;
import ru.cargaman.rbserver.service.CommentService;
import ru.cargaman.rbserver.status.ServiceStatus;

@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @GetMapping
    public ResponseEntity<?> GetCommentsByRecipe(
            @RequestParam Integer recipeId
    ){
        return ResponseEntity.ok(commentService.GetAllByRecipe(recipeId)
                .stream()
                .map(c -> new CommentResponse(
                        c.getId(),
                        c.getMessage(),
                        c.getUser().getLogin(),
                        c.getMark(),
                        c.getRecipe().getId()
                ))
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> GetCommentById(
            @PathVariable("id") Integer commentId
    ){
        Comment comment = commentService.GetById(commentId);
        if(comment == null){
            return ResponseEntity.status(404).body("There is no such comment");
        }
        return ResponseEntity.ok(new CommentResponse(
                comment.getId(),
                comment.getMessage(),
                comment.getUser().getLogin(),
                comment.getMark(),
                comment.getRecipe().getId()
        ));
    }

    @PostMapping
    public ResponseEntity<?> PostComment(
            @RequestParam Integer userId,
            @RequestParam Integer recipeId,
            @RequestBody CommentAddRequest commentRequest
            ){
        if(commentRequest.message() == null){
            return ResponseEntity.badRequest().body("There is no message in request body");
        }
        if(commentRequest.mark() == null){
            return ResponseEntity.badRequest().body("There is no mark in request body");
        }
        ServiceStatus code = commentService.Add(userId, recipeId, commentRequest.message(), commentRequest.mark());
        return ChooseAnswer(code);
    }

    @PutMapping
    public ResponseEntity<?> PutComment(
            @RequestParam Integer userId,
            @RequestBody CommentEditRequest commentRequest
            ){
        if(commentRequest.id() == null){
            return ResponseEntity.badRequest().body("There is no comment id in request body");
        }
        ServiceStatus code = commentService.Update(userId, commentRequest.id(), commentRequest.message(), commentRequest.mark());
        if(code == ServiceStatus.NotAllowed){
            return ResponseEntity.status(403).body("This comment is not yours");
        }
        return ChooseAnswer(code);
    }

    private ResponseEntity<?> ChooseAnswer(ServiceStatus code){
        switch (code){
            case success -> {
                return ResponseEntity.ok("Success");
            }
            case NotUnique -> {
                return ResponseEntity.status(409).body("?");
            }
            case UserNotFound -> {
                return ResponseEntity.status(404).body("There is no such user");
            }
            case RecipeNotFound -> {
                return ResponseEntity.status(404).body("There is no such recipe");
            }
            case EntityNotFound -> {
                return ResponseEntity.status(404).body("There is no such comment");
            }
            case NotAllowed -> {
                return ResponseEntity.status(403).body("It's looks like this recipe is not public");
            }
        }
        return ResponseEntity.status(418).body("-_-");
    }
}
