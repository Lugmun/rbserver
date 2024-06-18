package ru.cargaman.rbserver.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.cargaman.rbserver.model.Recipe;
import ru.cargaman.rbserver.model.Step;
import ru.cargaman.rbserver.model.User;
import ru.cargaman.rbserver.repository.RecipeRepository;
import ru.cargaman.rbserver.repository.StepRepository;
import ru.cargaman.rbserver.repository.UserRepository;
import ru.cargaman.rbserver.request.StepAddRequest;
import ru.cargaman.rbserver.status.ServiceStatus;

import java.util.List;
import java.util.Objects;

@Service
public class StepService {
    @Autowired
    private StepRepository stepRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RecipeRepository recipeRepository;

    public List<Step> GetAllByRecipe(Integer recipeId){
        return stepRepository.findAll()
                .stream()
                .filter(s -> Objects.equals(s.getRecipe().getId(), recipeId))
                .toList();
    }

    public Step GetById(Integer stepId){
        return stepRepository.findById(stepId).orElse(null);
    }

    public ServiceStatus AddOne(Integer userId, Integer recipeId, String name, Integer number){
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
        Step step = new Step();
        step.setName(name);
        step.setNumber(number);
        step.setRecipe(recipe);
        stepRepository.save(step);
        return ServiceStatus.success;
    }

    public ServiceStatus AddAll(Integer userId, Integer recipeId, List<StepAddRequest> stepRequests){
        User user = userRepository.findById(userId).orElse(null);
        if(user == null){
            return ServiceStatus.UserNotFound;
        }
        Recipe recipe = recipeRepository.findById(recipeId).orElse(null);
        if(recipe == null){
            return ServiceStatus.RecipeNotFound;
        }
        for(StepAddRequest stepRequest: stepRequests){
            Step step = new Step();
            step.setName(stepRequest.name());
            step.setNumber(stepRequest.number());
            step.setRecipe(recipe);
            stepRepository.save(step);
        }
        return ServiceStatus.success;
    }

    public ServiceStatus Update(Integer userId, Integer stepId, String name, Integer number){
        User user = userRepository.findById(userId).orElse(null);
        if(user == null){
            return ServiceStatus.UserNotFound;
        }
        Step step = stepRepository.findById(stepId).orElse(null);
        if(step == null){
            return ServiceStatus.EntityNotFound;
        }
        if(!Objects.equals(step.getRecipe().getAuthor().getId(), userId)){
            return ServiceStatus.NotAllowed;
        }
        if(name != null){
            step.setName(name);
        }
        if(number != null){
            step.setNumber(number);
        }
        stepRepository.save(step);
        return ServiceStatus.success;
    }

    public ServiceStatus Delete(Integer userId, Integer stepId){
        User user = userRepository.findById(userId).orElse(null);
        if(user == null){
            return ServiceStatus.UserNotFound;
        }Step step = stepRepository.findById(stepId).orElse(null);
        if(step == null){
            return ServiceStatus.EntityNotFound;
        }
        if(!Objects.equals(step.getRecipe().getAuthor().getId(), userId)){
            return ServiceStatus.NotAllowed;
        }
        stepRepository.delete(step);
        return ServiceStatus.success;
    }
}
