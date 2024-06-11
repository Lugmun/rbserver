package ru.cargaman.rbserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.cargaman.rbserver.model.User;
import ru.cargaman.rbserver.request.UserAddRequest;
import ru.cargaman.rbserver.response.UserResponse;
import ru.cargaman.rbserver.service.UserService;
import ru.cargaman.rbserver.status.ServiceStatus;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAll() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users.stream().map(user -> new UserResponse(
                user.getLogin(),
                user.getPassword(),
                user.getRole().getName()
        )).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getByID(@PathVariable("id") Integer id) {
        User user = userService.getUserById(id);
        if(user != null){
            return ResponseEntity.ok(new UserResponse(
                    user.getLogin(),
                    user.getPassword(),
                    user.getRole().getName()
            ));
        }
        else {
            return ResponseEntity.status(400).body("There is not user with ID: " + id);
        }
    }

    @PostMapping
    public ResponseEntity<?> AddUser(@RequestBody UserAddRequest requestData){
        if(requestData.login() == null){
            return ResponseEntity.badRequest().body("There is no login in request");
        }
        if(requestData.password() == null){
            return ResponseEntity.badRequest().body("There is no password in request");
        }
        ServiceStatus status = userService.addUser(requestData.login(), passwordEncoder.encode(requestData.password()));
        switch (status){
            case success -> {
                return ResponseEntity.ok("User saved successfully");
            }
            case NotUnique -> {
                return ResponseEntity.status(409).body("Login is not unique");
            }
        }
        return ResponseEntity.status(418).body("-_-");
    }
}
