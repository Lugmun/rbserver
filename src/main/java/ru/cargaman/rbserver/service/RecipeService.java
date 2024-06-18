package ru.cargaman.rbserver.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.cargaman.rbserver.model.Recipe;
import ru.cargaman.rbserver.model.User;
import ru.cargaman.rbserver.repository.*;
import ru.cargaman.rbserver.status.ServiceStatus;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class RecipeService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private StepRepository stepRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<Recipe> getAll(){
        return recipeRepository.findAll()
                .stream()
                .filter(r -> !r.isDeleted())
                .toList();
    }

    public List<Recipe> getAllPublic(){
        return recipeRepository.findAll()
                .stream()
                .filter(r -> !r.isDeleted())
                .filter(Recipe::isPublic)
                .toList();
    }

    public List<Recipe> getAllAvailable(Integer userId){
        return recipeRepository.findAll()
                .stream()
                .filter(r -> !r.isDeleted())
                .filter(r -> r.isPublic() || Objects.equals(r.getAuthor().getId(), userId))
                .toList();
    }

    public Recipe getById(Integer id){
        return recipeRepository.findById(id).orElse(null);
    }

    public ServiceStatus add(Integer userId, String name, String description, Integer time, Integer portions){
        User user = userRepository.findById(userId).orElse(null);
        if(user == null){
            return ServiceStatus.UserNotFound;
        }
        if(recipeRepository.findAll()
                .stream()
                .filter(r -> r.isPublic() || Objects.equals(r.getAuthor().getId(), userId))
                .anyMatch(r -> Objects.equals(r.getName(), name))){
            return ServiceStatus.NotUnique;
        }
        Recipe recipe = new Recipe();
        recipe.setName(name);
        recipe.setAuthor(user);
        recipe.setPublic(false);
        recipe.setDeleted(false);
        if(description != null){
            recipe.setDescription(description);
        }
        if(time != null){
            recipe.setTime(time);
        }
        if(portions != null){
            recipe.setPortions(portions);
        }
        recipeRepository.save(recipe);
        return ServiceStatus.success;
    }
    public ServiceStatus update(Integer userId, Integer recipeId, String name, String description, Integer time, Integer portions){
        User user = userRepository.findById(userId).orElse(null);
        if(user == null){
            return ServiceStatus.UserNotFound;
        }
        Recipe recipe = recipeRepository.findById(recipeId).orElse(null);
        if(recipe == null){
            return ServiceStatus.EntityNotFound;
        }
        if(!Objects.equals(recipe.getAuthor().getId(), userId)){
            return ServiceStatus.NotAllowed;
        }
        if(name != null){
            if(recipeRepository.findAll()
                    .stream()
                    .filter(r -> r.isPublic() || Objects.equals(r.getAuthor().getId(), userId))
                    .anyMatch(r -> Objects.equals(r.getName(), name))){
                return ServiceStatus.NotUnique;
            }
            recipe.setName(name);
        }
        if(recipe.isDeleted()){
            return ServiceStatus.EntityNotFound;
        }
        if(description != null){
            recipe.setDescription(description);
        }
        if(time != null){
            recipe.setTime(time);
        }
        if(portions != null){
            recipe.setPortions(portions);
        }
        recipeRepository.save(recipe);
        return ServiceStatus.success;
    }

    public ServiceStatus PublicUpdate(Integer recipeId){
        Recipe recipe = recipeRepository.findById(recipeId).orElse(null);
        if(recipe == null){
            return ServiceStatus.EntityNotFound;
        }
        recipe.setPublic(true);
        recipeRepository.save(recipe);
        return ServiceStatus.success;
    }

    public ServiceStatus delete(Integer userId, Integer recipeId, boolean value){
        User user = userRepository.findById(userId).orElse(null);
        if(user == null){
            return ServiceStatus.UserNotFound;
        }
        Recipe recipe = recipeRepository.findById(recipeId).orElse(null);
        if(recipe == null){
            return ServiceStatus.EntityNotFound;
        }
        if(!Objects.equals(recipe.getAuthor().getId(), userId)){
            return ServiceStatus.NotAllowed;
        }
        recipe.setDeleted(value);
        recipeRepository.save(recipe);
        return ServiceStatus.success;
    }
}
