package ru.cargaman.rbserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.cargaman.rbserver.model.User;
import ru.cargaman.rbserver.response.UserResponse;
import ru.cargaman.rbserver.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/test")
public class MainController {

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/all")
    public ResponseEntity<?> showUsersList() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users.stream().map(user -> new UserResponse(
                user.getLogin(),
                user.getPassword(),
                user.getRole().getName()
        )).toList());

    }

//    @GetMapping("/byId")
//    public ResponseEntity<?> showUserById() {
//        User user = userService.getUserById(1);
//        return ResponseEntity.ok(users.stream().map(user -> new UserResponse(
//                user.getLogin(),
//                user.getPassword(),
//                user.getRole().getName()
//        )).toList());
//
//    }

}
