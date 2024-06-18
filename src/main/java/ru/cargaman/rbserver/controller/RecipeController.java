package ru.cargaman.rbserver.controller;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.cargaman.rbserver.model.Recipe;
import ru.cargaman.rbserver.request.RecipeAddRequest;
import ru.cargaman.rbserver.request.RecipeEditRequest;
import ru.cargaman.rbserver.response.*;
import ru.cargaman.rbserver.service.RecipeService;
import ru.cargaman.rbserver.status.ServiceStatus;
import ru.cargaman.rbserver.utils.MiscUtils;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/recipe")
public class RecipeController {

    @Autowired
    private RecipeService recipeService;

    @GetMapping("/all")
    public ResponseEntity<?> getAll(
            @RequestParam Integer userId,
            @RequestParam(required = false) Boolean publicOnly,
            @RequestParam(required = false) Boolean full,
            @RequestParam(required = false) String search
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
        if(search != null){
            recipes = recipes.stream()
                    .filter(r -> MiscUtils.matchString(r.getName(), search))
                    .toList();
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

    @GetMapping("/available")
    public ResponseEntity<?> getAvailable(
            @RequestParam Integer userId,
            @RequestParam(required = false) Boolean publicOnly,
            @RequestParam(required = false) Boolean full,
            @RequestParam(required = false) String search
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
            recipes = recipeService.getAllAvailable(userId);
        }
        if(search != null){
            recipes = recipes.stream()
                    .filter(r -> MiscUtils.matchString(r.getName(), search))
                    .toList();
        }
        if(full){
            return ResponseEntity.ok(recipes
                    .stream()
                    .map(this::recipeFullParse)
                    .toList());
        }
        return ResponseEntity.ok(recipes
                .stream()
                .map(this::recipeParse)
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRecipeById(
            @PathVariable("id") Integer recipeId,
            @RequestParam Integer userId,
            @RequestParam(required = false) Boolean full
    ){
        if(full == null){
            full = false;
        }
        Recipe recipe = recipeService.getById(recipeId);
        if(full){
            return ResponseEntity.ok(recipeFullParse(recipe));
        }
        else {
            return ResponseEntity.ok(recipeParse(recipe));
        }
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
        return ChooseAnswer(code);
    }

    @PutMapping
    public ResponseEntity<?> putRecipe(
            @RequestParam Integer userId,
            @RequestBody RecipeEditRequest recipeRequest
            ){
        if(recipeRequest.id() == null){
            return ResponseEntity.badRequest().body("There is no recipe id in request body");
        }
        ServiceStatus code = recipeService.update(userId,
                recipeRequest.id(),
                recipeRequest.name(),
                recipeRequest.description(),
                recipeRequest.time(),
                recipeRequest.portions());
        return ChooseAnswer(code);
    }

    @PutMapping("/public/{id}")
    public ResponseEntity<?> publicRecipe(
            @PathVariable("id") Integer recipeId
    ){
        ServiceStatus code = recipeService.PublicUpdate(recipeId);
        return ChooseAnswer(code);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRecipe(
            @PathVariable("id") Integer recipeId,
            @RequestParam Integer userId
    ){
        ServiceStatus code = recipeService.delete(userId, recipeId, true);
        return ChooseAnswer(code);
    }

    @DeleteMapping("/restore/{id}")
    public ResponseEntity<?> restoreRecipe(
            @PathVariable("id") Integer recipeId,
            @RequestParam Integer userId
    ){
        ServiceStatus code = recipeService.delete(userId, recipeId, false);
        return ChooseAnswer(code);
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

    private ResponseEntity<?> ChooseAnswer(ServiceStatus code){
        switch (code){
            case success -> {
                return ResponseEntity.ok("Success");
            }
            case NotUnique -> {
                return ResponseEntity.status(409).body("Recipe with such name already exists");
            }
            case UserNotFound -> {
                return ResponseEntity.status(404).body("There is no such user");
            }
            case RecipeNotFound -> {
                return ResponseEntity.status(404).body("?");
            }
            case EntityNotFound -> {
                return ResponseEntity.status(404).body("There is no such recipe");
            }
            case NotAllowed -> {
                return ResponseEntity.status(403).body("It's looks like you don't have access to this product");
            }
        }
        return ResponseEntity.status(418).body("-_-");
    }
}
