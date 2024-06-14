package ru.cargaman.rbserver.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.cargaman.rbserver.model.Product;
import ru.cargaman.rbserver.model.User;
import ru.cargaman.rbserver.repository.ProductRepository;
import ru.cargaman.rbserver.repository.UserRepository;
import ru.cargaman.rbserver.status.ServiceStatus;

import java.util.List;
import java.util.Objects;

@Service
public class ProductService {

    private ProductRepository productRepository;
    private UserRepository userRepository;

    @Autowired
    public void setProductRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll().stream()
                .filter(p -> !p.isDeleted())
                .toList();
    }

    public List<Product> getAllOfUserById (Integer userId) {
        return productRepository.findAll()
                .stream()
                .filter(product ->
                        Objects.equals(product.getAuthor().getId(), userId))
                .filter(p -> !p.isDeleted())
                .toList();
    }

    public List<Product> getAllOfUserByLogin (String userLogin) {
        return productRepository.findAll()
                .stream()
                .filter(product ->
                        Objects.equals(product.getAuthor().getLogin(), userLogin))
                .filter(p -> !p.isDeleted())
                .toList();
    }

    public Product getById(Integer productId){
        return productRepository.findById(productId).orElse(null);
    }

    public List<Product> getAllPublic() {
        return productRepository.findAll()
                .stream()
                .filter(Product::isPublic)
                .filter(p -> !p.isDeleted())
                .toList();
    }

    public List<Product> getAllAvailable(Integer id){
        return productRepository.findAll()
                .stream()
                .filter(p -> p.isPublic() || Objects.equals(p.getAuthor().getId(), id))
                .filter(p -> !p.isDeleted())
                .toList();
    }


    public ServiceStatus add(Integer userId, String name, String measure) {
        User user = userRepository.findById(userId).orElse(null);
        if(user != null){
            if(productRepository.findAll()
                    .stream()
                    .filter(p -> p.isPublic() || Objects.equals(p.getAuthor().getId(), userId))
                    .anyMatch(product -> Objects.equals(product.getName(), name))){
                return ServiceStatus.NotUnique;
            }
            Product product = new Product();
            product.setName(name);
            product.setMeasure(measure);
            product.setPublic(false);
            product.setAuthor(user);
            productRepository.save(product);
            return ServiceStatus.success;
        }
        else {
            return ServiceStatus.UserNotFound;
        }
    }

    public ServiceStatus update(Integer userId, Integer productId, String name, String measure){
        User user = userRepository.findById(userId).orElse(null);
        if(user == null){
            return ServiceStatus.UserNotFound;
        }
        if(productRepository.findAll()
                .stream()
                .filter(p -> p.isPublic() || Objects.equals(p.getAuthor().getId(), userId))
                .filter(product -> !Objects.equals(product.getId(), productId))
                .anyMatch(product -> Objects.equals(product.getName(), name))) {
            return ServiceStatus.NotUnique;
        }
        Product product = productRepository.findById(productId).orElse(null);
        if(product == null){
            return ServiceStatus.EntityNotFound;
        }
        if(product.isDeleted()){
            return ServiceStatus.EntityNotFound;
        }
        if(!Objects.equals(product.getAuthor().getId(), userId)){
            return ServiceStatus.NotAllowed;
        }
        if(name != null){
            product.setName(name);
        }
        if(measure != null){
            product.setMeasure(measure);
        }
        productRepository.save(product);
        return ServiceStatus.success;
    }
    public void update(String newName, String  newMeasure, Integer productId){
        productRepository.findById(productId).get().setName(newName);
        productRepository.findById(productId).get().setMeasure(newMeasure);
    }

    public void update(String newName, String  newMeasure, Boolean isPublic, Integer productId){
        productRepository.findById(productId).get().setName(newName);
        productRepository.findById(productId).get().setMeasure(newMeasure);
        productRepository.findById(productId).get().setPublic(isPublic);
    }

    //todo: admins public product update and other abilities

    public ServiceStatus delete(Integer productId, boolean value, Integer userId) {
        User user = userRepository.findById(userId).orElse(null);
        if(user == null){
            return ServiceStatus.UserNotFound;
        }
        Product product = productRepository.findById(productId).orElse(null);
        if(product == null){
            return ServiceStatus.EntityNotFound;
        }
        if(!Objects.equals(product.getAuthor().getId(), userId)){
            return ServiceStatus.NotAllowed;
        }
        product.setDeleted(value);
        productRepository.save(product);
        return ServiceStatus.success;
    }
}
