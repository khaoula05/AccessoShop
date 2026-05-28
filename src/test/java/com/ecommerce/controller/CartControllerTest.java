package com.ecommerce.controller;

import com.ecommerce.entity.Cart;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.service.CartService;
import com.ecommerce.service.ProductService;
import com.ecommerce.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
@AutoConfigureMockMvc(addFilters = false)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @MockBean
    private ProductService productService;

    // Ajout du mock manquant : CartController en a besoin pour résoudre l'utilisateur
    @MockBean
    private UserService userService;

    @MockBean
    private Authentication authentication;

    // =========================
    // shouldRedirectToLoginWhenAuthNull
    // =========================

    @Test
    void shouldRedirectToLoginWhenAuthNull() throws Exception {

        // Aucun principal fourni → le contrôleur renvoie redirect:/login
        mockMvc.perform(get("/cart"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    // =========================
    // shouldDisplayCart
    // =========================

    @Test
    void shouldDisplayCart() throws Exception {

        User user = new User();
        user.setId(1L);

        Product product = new Product();
        product.setId(1L);
        product.setName("Montre");
        product.setPrice(100);

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setProduct(product);
        cart.setQuantity(2);

        when(authentication.getName()).thenReturn("test@test.com");
        when(userService.findByEmail("test@test.com")).thenReturn(user);

        // Le contrôleur appelle getUserCart(User) et getCartTotal(User), pas (Long)
        when(cartService.getUserCart(any(User.class))).thenReturn(List.of(cart));
        when(cartService.getCartTotal(any(User.class))).thenReturn(200.0);

        mockMvc.perform(get("/cart")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("cartItems"))
                .andExpect(model().attributeExists("total"))
                .andExpect(view().name("cart/view"));
    }

    // =========================
    // shouldAddToCart
    // =========================

    @Test
    void shouldAddToCart() throws Exception {

        User user = new User();
        user.setId(1L);

        Product product = new Product();
        product.setId(1L);

        when(authentication.getName()).thenReturn("test@test.com");
        when(userService.findByEmail("test@test.com")).thenReturn(user);
        when(productService.getProductById(1L)).thenReturn(product);

        mockMvc.perform(post("/cart/add/1")
                        .param("quantity", "2")
                        .principal(authentication))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));

        verify(cartService).addToCart(any(Cart.class));
    }

    // =========================
    // shouldRemoveFromCart
    // =========================

    @Test
    void shouldRemoveFromCart() throws Exception {

        // Le contrôleur vérifie le principal avant de supprimer → on le fournit
        when(authentication.getName()).thenReturn("test@test.com");

        mockMvc.perform(get("/cart/remove/1")
                        .principal(authentication))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));

        verify(cartService).removeFromCart(1L);
    }
}