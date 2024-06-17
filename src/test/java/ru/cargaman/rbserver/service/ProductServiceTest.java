package ru.cargaman.rbserver.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.cargaman.rbserver.model.Product;
import ru.cargaman.rbserver.model.User;
import ru.cargaman.rbserver.repository.ProductRepository;
import ru.cargaman.rbserver.repository.UserRepository;
import ru.cargaman.rbserver.status.ServiceStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private ProductService productService;
    private List<User> users = new ArrayList<>();
    private List<Product> products = new ArrayList<>();

    @BeforeEach
    public void setUp(){
        users.add(newUser(1, "Lorix", "1234"));
        users.add(newUser(2, "Pyetro", "1234"));

        products.add(newProduct(1, "Вода питьевая", "л", users.get(0), true));
        products.add(newProduct(2, "Яйцо куриное С0", "шт", users.get(1), false));
    }

    @Test
    public void GetAllPublicTest(){
        Mockito.when(productRepository.findAll()).thenReturn(products);
        List<Product> expected = products.stream().filter(Product::isPublic).toList();

        List<Product> actual = productService.getAllPublic();

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void AddTestSuccess(){
        Integer userId = 2;
        String name = "Соль поваренная";
        String measure = "г";
        Mockito.when(userRepository.findById(userId)).thenReturn(
                users.stream().filter(u -> Objects.equals(u.getId(), userId)).findFirst()
        );
        Mockito.when(productRepository.findAll()).thenReturn(products);
        ServiceStatus expected = ServiceStatus.success;

        ServiceStatus actual = productService.add(userId, name, measure);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void AddTestUserNotFound(){
        Integer userId = 3;
        String name = "Соль поваренная";
        String measure = "г";
        Mockito.when(userRepository.findById(userId)).thenReturn(
                users.stream().filter(u -> Objects.equals(u.getId(), userId)).findFirst()
        );
//        Mockito.when(productRepository.findAll()).thenReturn(products);
        ServiceStatus expected = ServiceStatus.UserNotFound;

        ServiceStatus actual = productService.add(userId, name, measure);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void AddTestNotUnique(){
        Integer userId = 2;
        String name = "Вода питьевая";
        String measure = "г";
        Mockito.when(userRepository.findById(userId)).thenReturn(
                users.stream().filter(u -> Objects.equals(u.getId(), userId)).findFirst()
        );
        Mockito.when(productRepository.findAll()).thenReturn(products);
        ServiceStatus expected = ServiceStatus.NotUnique;

        ServiceStatus actual = productService.add(userId, name, measure);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void UpdateTestSuccess(){
        Integer userId = 2;
        Integer productId = 2;
        String name = "Яйцо куриное C1";
        String measure = null;
        Mockito.when(userRepository.findById(userId)).thenReturn(
                users.stream().filter(u -> Objects.equals(u.getId(), userId)).findFirst()
        );
        Mockito.when(productRepository.findAll()).thenReturn(products);
        Mockito.when(productRepository.findById(productId)).thenReturn(
                products.stream().filter(p -> Objects.equals(p.getId(), productId)).findFirst()
        );

        ServiceStatus expected = ServiceStatus.success;
        ServiceStatus actual = productService.update(userId, productId, name, measure);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void UpdateTestUserNotFound(){
        Integer userId = 3;
        Integer productId = 2;
        String name = "Яйцо куриное C1";
        String measure = null;
        Mockito.when(userRepository.findById(userId)).thenReturn(
                users.stream().filter(u -> Objects.equals(u.getId(), userId)).findFirst()
        );
//        Mockito.when(productRepository.findAll()).thenReturn(products);
//        Mockito.when(productRepository.findById(productId)).thenReturn(
//                products.stream().filter(p -> Objects.equals(p.getId(), productId)).findFirst()
//        );

        ServiceStatus expected = ServiceStatus.UserNotFound;
        ServiceStatus actual = productService.update(userId, productId, name, measure);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void UpdateTestNotUnique(){
        Integer userId = 2;
        Integer productId = 2;
        String name = "Вода питьевая";
        String measure = null;
        Mockito.when(userRepository.findById(userId)).thenReturn(
                users.stream().filter(u -> Objects.equals(u.getId(), userId)).findFirst()
        );
        Mockito.when(productRepository.findAll()).thenReturn(products);
//        Mockito.when(productRepository.findById(productId)).thenReturn(
//                products.stream().filter(p -> Objects.equals(p.getId(), productId)).findFirst()
//        );

        ServiceStatus expected = ServiceStatus.NotUnique;
        ServiceStatus actual = productService.update(userId, productId, name, measure);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void UpdateTestEntityNotFound(){
        Integer userId = 2;
        Integer productId = 123;
        String name = "Яйцо куриное C1";
        String measure = null;
        Mockito.when(userRepository.findById(userId)).thenReturn(
                users.stream().filter(u -> Objects.equals(u.getId(), userId)).findFirst()
        );
        Mockito.when(productRepository.findAll()).thenReturn(products);
        Mockito.when(productRepository.findById(productId)).thenReturn(
                products.stream().filter(p -> Objects.equals(p.getId(), productId)).findFirst()
        );

        ServiceStatus expected = ServiceStatus.EntityNotFound;
        ServiceStatus actual = productService.update(userId, productId, name, measure);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void UpdateTestNotAllowed(){
        Integer userId = 1;
        Integer productId = 2;
        String name = "Яйцо куриное C1";
        String measure = null;
        Mockito.when(userRepository.findById(userId)).thenReturn(
                users.stream().filter(u -> Objects.equals(u.getId(), userId)).findFirst()
        );
        Mockito.when(productRepository.findAll()).thenReturn(products);
        Mockito.when(productRepository.findById(productId)).thenReturn(
                products.stream().filter(p -> Objects.equals(p.getId(), productId)).findFirst()
        );

        ServiceStatus expected = ServiceStatus.NotAllowed;
        ServiceStatus actual = productService.update(userId, productId, name, measure);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void DeleteTestUserNotFound(){
        Integer userId = 3;
        Integer productId = 2;
        boolean value = true;
        Mockito.when(userRepository.findById(userId)).thenReturn(
                users.stream().filter(u -> Objects.equals(u.getId(), userId)).findFirst()
        );

        ServiceStatus expected = ServiceStatus.UserNotFound;
        ServiceStatus actual = productService.delete(productId, value, userId);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void DeleteTestEntityNotFound(){
        Integer userId = 2;
        Integer productId = 3;
        boolean value = true;
        Mockito.when(userRepository.findById(userId)).thenReturn(
                users.stream().filter(u -> Objects.equals(u.getId(), userId)).findFirst()
        );
        Mockito.when(productRepository.findById(productId)).thenReturn(
                products.stream().filter(p -> Objects.equals(p.getId(), productId)).findFirst()
        );

        ServiceStatus expected = ServiceStatus.EntityNotFound;
        ServiceStatus actual = productService.delete(productId, value, userId);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void DeleteTestNotAllowed(){
        Integer userId = 1;
        Integer productId = 2;
        boolean value = true;
        Mockito.when(userRepository.findById(userId)).thenReturn(
                users.stream().filter(u -> Objects.equals(u.getId(), userId)).findFirst()
        );
        Mockito.when(productRepository.findById(productId)).thenReturn(
                products.stream().filter(p -> Objects.equals(p.getId(), productId)).findFirst()
        );

        ServiceStatus expected = ServiceStatus.NotAllowed;
        ServiceStatus actual = productService.delete(productId, value, userId);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void DeleteTestSuccess(){
        Integer userId = 2;
        Integer productId = 2;
        boolean value = true;
        Mockito.when(userRepository.findById(userId)).thenReturn(
                users.stream().filter(u -> Objects.equals(u.getId(), userId)).findFirst()
        );
        Mockito.when(productRepository.findById(productId)).thenReturn(
                products.stream().filter(p -> Objects.equals(p.getId(), productId)).findFirst()
        );

        ServiceStatus expected = ServiceStatus.success;
        ServiceStatus actual = productService.delete(productId, value, userId);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);;
    }

    private Product newProduct(Integer id, String name, String measure, User author, boolean isPublic){
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setMeasure(measure);
        product.setAuthor(author);
        product.setPublic(isPublic);
        product.setDeleted(false);
        return product;
    }

    private User newUser(Integer id, String login, String password){
        User user = new User();
        user.setId(id);
        user.setLogin(login);
        user.setPassword(password);
        return user;
    }
}