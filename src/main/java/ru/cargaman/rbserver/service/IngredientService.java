package ru.cargaman.rbserver.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.cargaman.rbserver.model.Ingredient;
import ru.cargaman.rbserver.model.Product;
import ru.cargaman.rbserver.model.Recipe;
import ru.cargaman.rbserver.model.User;
import ru.cargaman.rbserver.repository.IngredientRepository;
import ru.cargaman.rbserver.repository.ProductRepository;
import ru.cargaman.rbserver.repository.RecipeRepository;
import ru.cargaman.rbserver.repository.UserRepository;
import ru.cargaman.rbserver.request.IngredientAddRequest;
import ru.cargaman.rbserver.status.ServiceStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class IngredientService {

    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private ProductRepository productRepository;

    public List<Ingredient> GetByRecipe(Integer recipeId){
        return ingredientRepository.findAll()
                .stream()
                .filter(i -> Objects.equals(i.getRecipe().getId(), recipeId))
                .toList();
    }

    public Ingredient GetById(Integer ingredientId){
        return ingredientRepository.findById(ingredientId).orElse(null);
    }

    public ServiceStatus Add(Integer userId, Integer recipeId, Integer amount, Integer productId){
        User user = userRepository.findById(userId).orElse(null);
        if(user == null){
            return ServiceStatus.UserNotFound;
        }
        Recipe recipe = recipeRepository.findById(recipeId).orElse(null);
        if(recipe == null){
            return ServiceStatus.RecipeNotFound;
        }
        if(!Objects.equals(recipe.getAuthor().getId(), userId)){
            return ServiceStatus.NotAllowed;
        }
        Product product = productRepository.findById(productId).orElse(null);
        if(product == null){
            return ServiceStatus.EntityNotFound;
        }
        Ingredient ingredient = new Ingredient();
        ingredient.setAmount(amount);
        ingredient.setProduct(product);
        ingredient.setRecipe(recipe);
        ingredientRepository.save(ingredient);
        return ServiceStatus.success;
    }

    public ServiceStatus AddAll(Integer userId, Integer recipeId, List<IngredientAddRequest> ingredientRequests){
        User user = userRepository.findById(userId).orElse(null);
        if(user == null){
            return ServiceStatus.UserNotFound;
        }
        Recipe recipe = recipeRepository.findById(recipeId).orElse(null);
        if(recipe == null){
            return ServiceStatus.RecipeNotFound;
        }
        if(!Objects.equals(recipe.getAuthor().getId(), userId)){
            return ServiceStatus.NotAllowed;
        }
        List<Ingredient> ingredients = new ArrayList<>();
        for(IngredientAddRequest ingredientReq: ingredientRequests){
            Product product = productRepository.findById(ingredientReq.productId()).orElse(null);
            if(product == null){
                return ServiceStatus.EntityNotFound;
            }
            Ingredient ingredient = new Ingredient();
            ingredient.setAmount(ingredientReq.amount());
            ingredient.setProduct(product);
            ingredient.setRecipe(recipe);
            ingredients.add(ingredient);
        }
        ingredientRepository.saveAll(ingredients);
        return ServiceStatus.success;
    }

    public ServiceStatus update(Integer userId, Integer ingredientId, Integer amount, Integer productId){
        User user = userRepository.findById(userId).orElse(null);
        if(user == null){
            return ServiceStatus.UserNotFound;
        }
        Ingredient ingredient = ingredientRepository.findById(ingredientId).orElse(null);
        if(ingredient == null){
            return ServiceStatus.EntityNotFound;
        }
        if(!Objects.equals(ingredient.getRecipe().getAuthor().getId(), userId)){
            return ServiceStatus.NotAllowed;
        }
        if(amount != null){
            ingredient.setAmount(amount);
        }
        if(productId != null){
            Product product = productRepository.findById(productId).orElse(null);
            if(product == null){
                return ServiceStatus.EntityNotFound;
            }
            ingredient.setProduct(product);
        }
        ingredientRepository.save(ingredient);
        return ServiceStatus.success;
    }

    public ServiceStatus Delete(Integer userId, Integer ingredientId){
        User user = userRepository.findById(userId).orElse(null);
        if(user == null){
            return ServiceStatus.UserNotFound;
        }
        Ingredient ingredient = ingredientRepository.findById(ingredientId).orElse(null);
        if(ingredient == null){
            return ServiceStatus.EntityNotFound;
        }
        if(!Objects.equals(ingredient.getRecipe().getAuthor().getId(), userId)){
            return ServiceStatus.NotAllowed;
        }
        ingredientRepository.delete(ingredient);
        return ServiceStatus.success;
    }
}
