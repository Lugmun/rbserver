package ru.cargaman.rbserver.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.cargaman.rbserver.model.Comment;
import ru.cargaman.rbserver.model.Recipe;
import ru.cargaman.rbserver.model.User;
import ru.cargaman.rbserver.repository.CommentRepository;
import ru.cargaman.rbserver.repository.RecipeRepository;
import ru.cargaman.rbserver.repository.UserRepository;
import ru.cargaman.rbserver.status.ServiceStatus;

import java.util.List;
import java.util.Objects;

@Service
public class CommentService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private CommentRepository commentRepository;

    public List<Comment> GetAllByRecipe(Integer recipeId){
        return commentRepository.findAll()
                .stream()
                .filter(c -> Objects.equals(c.getRecipe().getId(), recipeId))
                .toList();
    }

    public Comment GetById(Integer commentId){
        return commentRepository.findById(commentId).orElse(null);
    }

    public ServiceStatus Add(Integer userId, Integer recipeId, String message, Integer mark){
        User user = userRepository.findById(userId).orElse(null);
        if(user == null){
            return ServiceStatus.UserNotFound;
        }
        Recipe recipe = recipeRepository.findById(recipeId).orElse(null);
        if(recipe == null){
            return ServiceStatus.RecipeNotFound;
        }
        if(!recipe.isPublic()){
            return ServiceStatus.NotAllowed;
        }
        Comment comment = new Comment();
        comment.setMessage(message);
        comment.setMark(mark);
        comment.setRecipe(recipe);
        comment.setUser(user);
        commentRepository.save(comment);
        return ServiceStatus.success;
    }

    public ServiceStatus Update(Integer userId, Integer commentId, String message, Integer mark){
        User user = userRepository.findById(userId).orElse(null);
        if(user == null){
            return ServiceStatus.UserNotFound;
        }
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if(comment == null){
            return ServiceStatus.EntityNotFound;
        }
        if(!Objects.equals(comment.getUser().getId(), userId)){
            return ServiceStatus.NotAllowed;
        }
        if(message != null){
            comment.setMessage(message);
        }
        if(mark != null){
            comment.setMark(mark);
        }
        commentRepository.save(comment);
        return ServiceStatus.success;
    }
}
