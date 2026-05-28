package com.ecommerce.service;

import com.ecommerce.entity.Product;
import com.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductService service;

    @Test
    void shouldReturnAllProducts() {

        when(repository.findAll())
                .thenReturn(List.of(new Product()));

        List<Product> products = service.getAllProducts();

        assertFalse(products.isEmpty());
    }

    @Test
    void shouldReturnFeaturedProducts() {

        when(repository.findByFeaturedTrue())
                .thenReturn(List.of(new Product()));

        List<Product> products = service.getFeaturedProducts();

        assertEquals(1, products.size());
    }

    @Test
    void shouldReturnProductsByCategory() {

        when(repository.findByCategory("montre"))
                .thenReturn(List.of(new Product()));

        List<Product> products =
                service.getProductsByCategory("montre");

        assertFalse(products.isEmpty());
    }

    @Test
    void shouldSearchProducts() {

        when(repository.findByNameContainingIgnoreCase("gold"))
                .thenReturn(List.of(new Product()));

        List<Product> products =
                service.searchProducts("gold");

        assertFalse(products.isEmpty());
    }

    @Test
    void shouldReturnProductById() {

        Product product = new Product();
        product.setId(1L);

        when(repository.findById(1L))
                .thenReturn(Optional.of(product));

        Product result = service.getProductById(1L);

        assertNotNull(result);
    }

    @Test
    void shouldSaveProduct() {

        Product product = new Product();

        when(repository.save(product))
                .thenReturn(product);

        Product saved = service.saveProduct(product);

        assertNotNull(saved);

        verify(repository).save(product);
    }

    @Test
    void shouldDeleteProduct() {

        service.deleteProduct(1L);

        verify(repository).deleteById(1L);
    }
}