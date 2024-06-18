package ru.cargaman.rbserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.cargaman.rbserver.model.Product;
import ru.cargaman.rbserver.request.ProductAddRequest;
import ru.cargaman.rbserver.request.ProductEditRequest;
import ru.cargaman.rbserver.response.ProductResponse;
import ru.cargaman.rbserver.service.ProductService;
import ru.cargaman.rbserver.status.ServiceStatus;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("/all")
    public ResponseEntity<?> getAll(
            //temporal
            @RequestParam Integer userId,
            @RequestParam Boolean publicOnly
    ){
        List<Product> products;
        if(publicOnly){
            products = productService.getAllPublic();
        }else {
            products = productService.getAllProducts();
        }
        return ResponseEntity.ok(products.stream().map(p -> new ProductResponse(
                p.getId(),
                p.getName(),
                p.getMeasure(),
                p.isPublic(),
                p.getAuthor().getLogin()
        )));
    }

    @GetMapping("/available")
    public ResponseEntity<?> getAvailable(
            //temporal
            @RequestParam Integer userId,
            @RequestParam boolean publicOnly
    ){
        List<Product> products;
        if(publicOnly){
            products = productService.getAllPublic();
        }else {
            products = productService.getAllAvailable(userId);
        }
        return ResponseEntity.ok(products.stream().map(p -> new ProductResponse(
                p.getId(),
                p.getName(),
                p.getMeasure(),
                p.isPublic(),
                p.getAuthor().getLogin()
        )));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(
            @PathVariable("id") Integer productId,
            @RequestParam Integer userId
    ){
        Product product = productService.getById(productId);
        if(product == null){
            return ResponseEntity.status(404).body("There is no such product");
        }
        if(Objects.equals(product.getAuthor().getId(), userId) || product.isPublic()){
            return ResponseEntity.ok(new ProductResponse(
                    product.getId(),
                    product.getName(),
                    product.getMeasure(),
                    product.isPublic(),
                    product.getAuthor().getLogin()
            ));
        }
        else {
            return ResponseEntity.status(403).body("It's looks like you don't have access to this product");
        }
    }

    @PostMapping
    public ResponseEntity<?> postProduct(
            @RequestBody ProductAddRequest productRequest,
            //temporal
            @RequestParam Integer userId
            ){
        if(productRequest.name() == null){
            return ResponseEntity.badRequest().body("There is no product name in request body");
        }
        if(productRequest.measure() == null){
            return ResponseEntity.badRequest().body("There is no product measure in request body");
        }
        ServiceStatus code = productService.add(userId, productRequest.name(), productRequest.measure());
        return ChooseAnswer(code);
    }

    @PutMapping
    public ResponseEntity<?> putProduct(
            //temporal
            @RequestParam Integer userId,
            @RequestBody ProductEditRequest productRequest
            ){
        if(productRequest.productId() == null){
            return ResponseEntity.badRequest().body("There is no product id in request body");
        }
        ServiceStatus code = productService.update(userId, productRequest.productId(), productRequest.name(), productRequest.measure());
        return ChooseAnswer(code);
    }

    @PutMapping("/public/{id}")
    public ResponseEntity<?> publicProduct(
            @PathVariable("id") Integer productId
    ){
        ServiceStatus code = productService.PublicUpdate(productId);
        return ChooseAnswer(code);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(
            @PathVariable("id") Integer id,
            //temporal
            @RequestParam Integer userId
    ){
        ServiceStatus code = productService.delete(id, true, userId);
        return ChooseAnswer(code);
    }

    @DeleteMapping("/restore/{id}")
    public ResponseEntity<?> restoreProduct(
            @PathVariable("id") Integer id,
            //temporal
            @RequestParam Integer userId
    ){
        ServiceStatus code = productService.delete(id, false, userId);
        return ChooseAnswer(code);
    }

    private ResponseEntity<?> ChooseAnswer(ServiceStatus code){
        switch (code){
            case success -> {
                return ResponseEntity.ok("Success");
            }
            case NotUnique -> {
                return ResponseEntity.status(409).body("Product with such name already exists");
            }
            case UserNotFound -> {
                return ResponseEntity.status(404).body("There is no such user");
            }
            case RecipeNotFound -> {
                return ResponseEntity.status(404).body("There is no such recipe");
            }
            case EntityNotFound -> {
                return ResponseEntity.status(404).body("There is no such product");
            }
            case NotAllowed -> {
                return ResponseEntity.status(403).body("It's look like you don't have access to this product");
            }
        }
        return ResponseEntity.status(418).body("-_-");
    }
}

