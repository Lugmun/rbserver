package ru.cargaman.rbserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.cargaman.rbserver.model.Ingredient;
import ru.cargaman.rbserver.request.IngredientAddRequest;
import ru.cargaman.rbserver.request.IngredientEditRequest;
import ru.cargaman.rbserver.response.IngredientResponse;
import ru.cargaman.rbserver.response.ProductResponse;
import ru.cargaman.rbserver.service.IngredientService;
import ru.cargaman.rbserver.status.ServiceStatus;

import java.util.List;

@RestController
@RequestMapping("/ingredient")
public class IngredientController {
    @Autowired
    private IngredientService ingredientService;

    @GetMapping
    public ResponseEntity<?> GetAllIngredientsByRecipe(
            @RequestParam Integer recipeId
    ){
        List<Ingredient> ingredients = ingredientService.GetByRecipe(recipeId);
        return ResponseEntity.ok(ingredients
                .stream()
                .map(i -> new IngredientResponse(
                        i.getId(),
                        i.getAmount(),
                        new ProductResponse(
                                i.getProduct().getId(),
                                i.getProduct().getName(),
                                i.getProduct().getMeasure(),
                                i.getProduct().isPublic(),
                                i.getProduct().getAuthor().getLogin()
                        )
                ))
                .toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> GetIngredientById(
            @PathVariable("id") Integer ingredientId
    ){
        Ingredient ingredient = ingredientService.GetById(ingredientId);
        if(ingredient == null){
            return ResponseEntity.status(404).body("There is no such ingredient");
        }
        return ResponseEntity.ok(new IngredientResponse(
                ingredient.getId(),
                ingredient.getAmount(),
                new ProductResponse(
                        ingredient.getProduct().getId(),
                        ingredient.getProduct().getName(),
                        ingredient.getProduct().getMeasure(),
                        ingredient.getProduct().isPublic(),
                        ingredient.getProduct().getAuthor().getLogin()
                )
        ));
    }

    @PostMapping
    public ResponseEntity<?> PostIngredient(
            @RequestParam Integer userId,
            @RequestParam Integer recipeId,
            @RequestBody IngredientAddRequest ingredientRequest
            ){
        if(ingredientRequest.amount() == null){
            return ResponseEntity.badRequest().body("There is no product amount in request body");
        }
        if(ingredientRequest.productId() == null){
            return ResponseEntity.badRequest().body("There is no product id in request body");
        }
        ServiceStatus code = ingredientService.Add(
                userId,
                recipeId,
                ingredientRequest.amount(),
                ingredientRequest.productId()
        );
        return ChooseAnswer(code);
    }

    @PostMapping("/all")
    public ResponseEntity<?> PostAllIngredients(
            @RequestParam Integer userId,
            @RequestParam Integer recipeId,
            @RequestBody List<IngredientAddRequest> ingredientRequests
    ){
        for(IngredientAddRequest ingredientRequest: ingredientRequests){
            if(ingredientRequest.amount() == null){
                return ResponseEntity.badRequest().body("There is no product amount in request body");
            }
            if(ingredientRequest.productId() == null){
                return ResponseEntity.badRequest().body("There is no product id in request body");
            }
        }
        ServiceStatus code = ingredientService.AddAll(userId, recipeId, ingredientRequests);
        return ChooseAnswer(code);
    }
    @PutMapping
    public ResponseEntity<?> PutIngredient(
            @RequestParam Integer userId,
            @RequestBody IngredientEditRequest ingredientRequest
            ){
        if(ingredientRequest.id() == null){
            return ResponseEntity.badRequest().body("There is no ingredient id in request body");
        }
        ServiceStatus code = ingredientService.update(
                userId,
                ingredientRequest.id(),
                ingredientRequest.amount(),
                ingredientRequest.productId()
        );
        return ChooseAnswer(code);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> DeleteIngredient(
            @RequestParam Integer userId,
            @PathVariable("id") Integer ingredientId
    ){
        ServiceStatus code = ingredientService.Delete(userId, ingredientId);
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
                return ResponseEntity.status(404).body("There is no such ingredient");
            }
            case NotAllowed -> {
                return ResponseEntity.status(403).body("It's looks like you don't have access to the recipe");
            }
        }
        return ResponseEntity.status(418).body("-_-");
    }
}
