package ru.cargaman.rbserver.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.cargaman.rbserver.model.User;
import ru.cargaman.rbserver.repository.RoleRepository;
import ru.cargaman.rbserver.repository.UserRepository;
import java.util.Objects;


import java.util.List;

@Service
public class UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Integer id) {
        return  userRepository.findAll().stream().filter(user -> Objects.equals(user.getId(), id)).findFirst().orElse(null);
    }

    public User findByLogin(String login) {
        return userRepository.findAll().stream().filter(p -> p.getLogin().equals(login)).findFirst().get();
    }

}
