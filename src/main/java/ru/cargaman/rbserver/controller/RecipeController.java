package ru.cargaman.rbserver.controller;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.cargaman.rbserver.model.Recipe;
import ru.cargaman.rbserver.service.RecipeService;

import java.util.List;

@RestController
@RequestMapping("/recipe")
public class RecipeController {

    @Autowired
    private RecipeService recipeService;

    @GetMapping("/all")
    public ResponseEntity<?> getAll(
            @RequestParam Integer userId,
            @RequestParam boolean publicOnly
            ){
        List<Recipe> recipe = recipeService.getAll();
        return ResponseEntity.ok(recipe);
    }

}
