package com.ecommerce.controller;

import com.ecommerce.entity.Product;
import com.cloudinary.Cloudinary;
import com.ecommerce.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService service;

    // Cloudinary requis par le constructeur de ProductController
    @MockBean
    private Cloudinary cloudinary;

    @Test
    void shouldDisplayProductsPage() throws Exception {

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/list"));
    }

    @Test
    void shouldSearchProducts() throws Exception {

        when(service.searchProducts("montre"))
                .thenReturn(List.of());

        mockMvc.perform(get("/products")
                        .param("search", "montre"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("products"));
    }

    @Test
    void shouldDisplayProductDetails() throws Exception {

        Product product = new Product();
        product.setId(1L);
        product.setCategory("montre");

        when(service.getProductById(1L))
                .thenReturn(product);

        when(service.getProductsByCategory("montre"))
                .thenReturn(List.of());

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/detail"))
                .andExpect(model().attributeExists("product"));
    }

    @Test
    void shouldRedirectIfProductNotFound() throws Exception {

        when(service.getProductById(1L))
                .thenReturn(null);

        mockMvc.perform(get("/products/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"));
    }

    @Test
    void shouldDeleteProduct() throws Exception {

        mockMvc.perform(get("/products/admin/delete/1"))
                .andExpect(status().is3xxRedirection());

        verify(service).deleteProduct(1L);
    }
}