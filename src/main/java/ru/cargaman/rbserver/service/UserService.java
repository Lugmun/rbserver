package ru.cargaman.rbserver.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.cargaman.rbserver.model.User;
import ru.cargaman.rbserver.repository.RoleRepository;
import ru.cargaman.rbserver.repository.UserRepository;
import ru.cargaman.rbserver.status.ServiceStatus;

import java.util.Objects;


import java.util.List;

@Service
public class UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Integer id) {
//        return  userRepository.findAll().stream().filter(user -> Objects.equals(user.getId(), id)).findFirst().orElse(null);
        return userRepository.findById(id).orElse(null);
    }

    public User findByLogin(String login) {
        return userRepository.findAll().stream().filter(p -> p.getLogin().equals(login)).findFirst().get();
    }

    public ServiceStatus addUser(String login, String password){
        if(userRepository.findAll().stream().noneMatch(u -> Objects.equals(u.getLogin(), login))){
            User user = new User();
            user.setLogin(login);
            user.setPassword(password);
            user.setRole(roleRepository.findById(1).orElse(null));
            userRepository.save(user);
            return ServiceStatus.success;
        }else {
            return ServiceStatus.NotUnique;
        }
    }
}
