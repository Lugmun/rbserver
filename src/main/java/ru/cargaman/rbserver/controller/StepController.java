package ru.cargaman.rbserver.controller;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.cargaman.rbserver.model.Step;
import ru.cargaman.rbserver.request.StepAddRequest;
import ru.cargaman.rbserver.request.StepEditRequest;
import ru.cargaman.rbserver.response.StepResponse;
import ru.cargaman.rbserver.service.StepService;
import ru.cargaman.rbserver.status.ServiceStatus;

import java.util.List;

@RestController
@RequestMapping("/step")
public class StepController {
    @Autowired
    private StepService stepService;

    @GetMapping
    public ResponseEntity<?> GetStepsByRecipe(
            @RequestParam Integer recipeId
    ){
        return ResponseEntity.ok(stepService.GetAllByRecipe(recipeId)
                .stream()
                .map(s -> new StepResponse(
                        s.getId(),
                        s.getNumber(),
                        s.getName()
                ))
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> GetStepById(
            @PathVariable("id") Integer stepId
    ){
        Step step = stepService.GetById(stepId);
        if(step == null){
            return ResponseEntity.status(404).body("There is no such step");
        }
        return ResponseEntity.ok(
                new StepResponse(
                        step.getId(),
                        step.getNumber(),
                        step.getName()
                )
        );
    }

    @PostMapping
    public ResponseEntity<?> AddStep(
            @RequestParam Integer userId,
            @RequestParam Integer recipeId,
            @RequestBody StepAddRequest stepRequest
            ){
        if(stepRequest.name() == null){
            return ResponseEntity.badRequest().body("There is no step name in request body");
        }
        if(stepRequest.number() == null){
            return ResponseEntity.badRequest().body("There is no step number in request body");
        }
        ServiceStatus code = stepService.AddOne(userId, recipeId, stepRequest.name(), stepRequest.number());
        return ChooseAnswer(code);
    }

    @PostMapping("/all")
    public ResponseEntity<?> AddAllSteps(
            @RequestParam Integer userId,
            @RequestParam Integer recipeId,
            @RequestBody List<StepAddRequest> stepRequests
            ){
        for(StepAddRequest stepRequest: stepRequests){
            if(stepRequest.name() == null){
                return ResponseEntity.badRequest().body("There is no step name in request body");
            }
            if(stepRequest.number() == null){
                return ResponseEntity.badRequest().body("There is no step number in request body");
            }
        }
        ServiceStatus code = stepService.AddAll(userId, recipeId, stepRequests);
        return ChooseAnswer(code);
    }

    @PutMapping
    public ResponseEntity<?> PutStep(
            @RequestParam Integer userId,
            @RequestBody StepEditRequest stepRequest
            ){
        if(stepRequest.id() == null){
            return ResponseEntity.badRequest().body("There is no step id in request body");
        }
        ServiceStatus code = stepService.Update(userId, stepRequest.id(), stepRequest.name(), stepRequest.number());
        return ChooseAnswer(code);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> DeleteStep(
            @RequestParam Integer userId,
            @PathVariable("id") Integer stepId
    ){
        ServiceStatus code = stepService.Delete(userId, stepId);
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
                return ResponseEntity.status(404).body("There is no such step");
            }
            case NotAllowed -> {
                return ResponseEntity.status(403).body("It's looks like you don't have access to this recipe");
            }
        }
        return ResponseEntity.status(418).body("-_-");
    }
}
