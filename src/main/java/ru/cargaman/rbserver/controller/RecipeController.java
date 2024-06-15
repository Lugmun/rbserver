package ru.cargaman.rbserver.controller;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.cargaman.rbserver.model.Recipe;
import ru.cargaman.rbserver.request.RecipeAddRequest;
import ru.cargaman.rbserver.response.*;
import ru.cargaman.rbserver.service.RecipeService;
import ru.cargaman.rbserver.status.ServiceStatus;

import java.util.List;

@RestController
@RequestMapping("/recipe")
public class RecipeController {

    @Autowired
    private RecipeService recipeService;

    @GetMapping("/all")
    public ResponseEntity<?> getAll(
            @RequestParam Integer userId,
            @RequestParam(required = false) Boolean publicOnly,
            @RequestParam(required = false) Boolean full
            ){
        if(publicOnly == null){
            publicOnly = false;
        }
        if(full == null){
            full = false;
        }
        List<Recipe> recipes;
        if(publicOnly){
            recipes = recipeService.getAllPublic();
        }
        else {
            recipes = recipeService.getAll();
        }
        if(full){
            return ResponseEntity.ok(recipes
                    .stream()
                    .map(this::recipeFullParse)
                    .toList());
        }
        return ResponseEntity.ok(recipes.stream()
                .map(this::recipeParse)
                .toList());
    }

    @PostMapping
    public ResponseEntity<?> postRecipe(
            @RequestParam Integer userId,
            @RequestBody RecipeAddRequest recipeRequest
            ){
        if(recipeRequest.name() == null){
            return ResponseEntity.badRequest().body("There is no recipe name in request body");
        }
        ServiceStatus code = recipeService.add(userId, recipeRequest.name(), recipeRequest.description(), recipeRequest.time(), recipeRequest.portions());
        switch(code){
            case success -> {
                return ResponseEntity.ok("Success");
            }
            case UserNotFound -> {
                return ResponseEntity.ok("There is no such user");
            }
            case NotUnique -> {
                return ResponseEntity.ok("It's looks like recipe with such name already exists");
            }
        }
        return ResponseEntity.status(418).body("-_-");
    }

    private RecipeFullResponse recipeFullParse(Recipe recipe){
        return new RecipeFullResponse(
                recipe.getId(),
                recipe.getName(),
                recipe.getDescription(),
                recipe.getTime(),
                recipe.getPortions(),
                recipe.isPublic(),
                recipe.getAuthor().getLogin(),
                recipe.getIngredients().stream().map(
                        i -> new IngredientResponse(
                                i.getId(),
                                i.getAmount(),
                                new ProductResponse(
                                        i.getProduct().getId(),
                                        i.getProduct().getName(),
                                        i.getProduct().getMeasure(),
                                        i.getProduct().isPublic(),
                                        i.getProduct().getAuthor().getLogin()
                                )
                        )
                ).toList(),
                recipe.getSteps().stream().map(
                        s -> new StepResponse(
                                s.getId(),
                                s.getNumber(),
                                s.getName()
                        )
                ).toList()
        );
    }
    private RecipeResponse recipeParse(Recipe recipe){
        return new RecipeResponse(
                recipe.getId(),
                recipe.getName(),
                recipe.getDescription(),
                recipe.getTime(),
                recipe.getPortions(),
                recipe.isPublic(),
                recipe.getAuthor().getLogin()
        );
    }
}
