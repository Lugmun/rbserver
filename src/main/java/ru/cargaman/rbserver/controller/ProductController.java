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
        switch (code){
            case success -> {
                return ResponseEntity.ok("Success");
            }
            case UserNotFound -> {
                return ResponseEntity.status(404).body("There is no such user");
            }
            case NotUnique -> {
                return ResponseEntity.status(409).body("Product with such name already exists");
            }
        }
        return ResponseEntity.status(418).body("-_-");
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
        switch (code){
            case success -> {
                return ResponseEntity.ok("Success");
            }
            case UserNotFound -> {
                return ResponseEntity.status(404).body("There is no such user");
            }
            case EntityNotFound -> {
                return ResponseEntity.status(404).body("There is no such product");
            }
            case NotUnique -> {
                return ResponseEntity.status(409).body("Product with such name already exists");
            }
            case NotAllowed -> {
                return ResponseEntity.status(403).body("You can not edit this product");
            }
        }
        return ResponseEntity.status(418).body("-_-");
    }
}

