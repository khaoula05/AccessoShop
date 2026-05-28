package com.ecommerce.repository;

import com.ecommerce.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository repository;

    @Test
    void shouldFindByCategory() {

        Product product = new Product();
        product.setName("Montre Gold");
        product.setCategory("montre");
        product.setPrice(100);

        repository.save(product);

        List<Product> products =
                repository.findByCategory("montre");

        assertFalse(products.isEmpty());
    }

    @Test
    void shouldSearchByName() {

        Product product = new Product();
        product.setName("Bracelet Gold");
        product.setPrice(50);
        product.setCategory("bracelet");
        repository.save(product);

        List<Product> products =
                repository.findByNameContainingIgnoreCase("gold");

        assertFalse(products.isEmpty());
    }
}