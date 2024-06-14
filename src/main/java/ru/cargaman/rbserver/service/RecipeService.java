package ru.cargaman.rbserver.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.cargaman.rbserver.model.Recipe;
import ru.cargaman.rbserver.model.User;
import ru.cargaman.rbserver.repository.*;
import ru.cargaman.rbserver.status.ServiceStatus;

import java.util.List;
import java.util.Objects;

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
}
