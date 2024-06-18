package ru.cargaman.rbserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.cargaman.rbserver.request.LoginRequest;
import ru.cargaman.rbserver.response.TokenResponse;
import ru.cargaman.rbserver.service.UserService;
import ru.cargaman.rbserver.status.ServiceStatus;
import ru.cargaman.rbserver.utils.JwtUtils;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request
            ){
        if(request.login() == null){
            return ResponseEntity.badRequest().body("There is no login in request");
        }
        if(request.password() == null){
            return ResponseEntity.badRequest().body("There is no password in request");
        }
        ServiceStatus code = userService.findByLoginAndPassword(
                request.login(),
                request.password()
        );
        switch (code){
            case success -> {
                return ResponseEntity.ok(new TokenResponse(
                        jwtUtils.generateToken(request.login())
                ));
            }
            case UserNotFound -> {
                return ResponseEntity.status(404).body("User with such login not found");
            }
            case NotAllowed -> {
                return ResponseEntity.status(403).body("Incorrect password");
            }
        }
        return ResponseEntity.status(418).body("-_-");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody LoginRequest request
    ){
        if(request.login() == null){
            return ResponseEntity.badRequest().body("There is no login in request");
        }
        if(request.password() == null){
            return ResponseEntity.badRequest().body("There is no password in request");
        }
        ServiceStatus code = userService.addUser(
                request.login(),
                passwordEncoder.encode(request.password())
                );
        switch (code){
            case success -> {
                return ResponseEntity.ok("Success, you can login");
            }
            case NotUnique -> {
                return ResponseEntity.status(409).body("User with such name already exists");
            }
        }
        return ResponseEntity.status(418).body("-_-");
    }
}
