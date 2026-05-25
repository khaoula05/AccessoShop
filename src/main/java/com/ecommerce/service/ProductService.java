package com.ecommerce.service;

import com.ecommerce.entity.Product;
import com.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repository;
    

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public List<Product> getAllProducts() {
        return repository.findAll();
    }

    public List<Product> getFeaturedProducts() {
        return repository.findByFeaturedTrue();
    }

    public List<Product> getProductsByCategory(String category) {
        return repository.findByCategory(category);
    }

    public List<Product> searchProducts(String query) {
        return repository.findByNameContainingIgnoreCase(query);
    }

    public Product getProductById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Product saveProduct(Product product) {
        return repository.save(product);
    }

    public void deleteProduct(Long id) {
        repository.deleteById(id);
    }
}
