package ru.cargaman.rbserver.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.cargaman.rbserver.model.Product;
import ru.cargaman.rbserver.model.User;
import ru.cargaman.rbserver.repository.ProductRepository;
import ru.cargaman.rbserver.repository.UserRepository;

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
        return productRepository.findAll();
    }

    public List<Product> getAllOfUserById (Integer userId) {
        return productRepository.findAll()
                .stream()
                .filter(product ->
                        Objects.equals(product.getAuthor().getId(), userId))
                .toList();
    }

    public List<Product> getAllOfUserByLogin (String userLogin) {
        return productRepository.findAll()
                .stream()
                .filter(product ->
                        Objects.equals(product.getAuthor().getLogin(), userLogin))
                .toList();
    }

    public Product getById(Integer productId){
        return productRepository.findAll()
                .stream()
                .filter(product ->
                        Objects.equals(product.getId(), productId)).findFirst().get();
    }

    public List<Product> getAllPublic() {
        return productRepository.findAll()
                .stream()
                .filter(Product::isPublic)
                .toList();
    }

    public void add(Product product) {
        productRepository.save(product);
    }

    public void update(String newName, Integer productId){
        productRepository.findById(productId).get().setName(newName);
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


    public void delete(Integer productId) {
        productRepository.deleteById(productId);
    }






}
