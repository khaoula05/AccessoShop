package com.ecommerce.controller;

import com.ecommerce.entity.Cart;
import com.ecommerce.entity.Product;
import com.ecommerce.service.CartService;
import com.ecommerce.service.ProductService;
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

    @MockBean
    private Authentication authentication;

    @Test
    void shouldRedirectToLoginWhenAuthNull() throws Exception {

        mockMvc.perform(get("/cart"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void shouldDisplayCart() throws Exception {

        when(authentication.getName()).thenReturn("test@test.com");

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setProductId(1L);
        cart.setQuantity(2);

        Product product = new Product();
        product.setId(1L);
        product.setName("Montre");
        product.setPrice(100);

        when(cartService.getUserCart(anyLong()))
                .thenReturn(List.of(cart));

        when(productService.getProductById(1L))
                .thenReturn(product);

        when(cartService.getCartTotal(anyLong()))
                .thenReturn(200.0);

        mockMvc.perform(get("/cart")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("cartItems"))
                .andExpect(model().attributeExists("total"))
                .andExpect(view().name("cart/view"));
    }

    @Test
    void shouldAddToCart() throws Exception {

        when(authentication.getName()).thenReturn("test@test.com");

        mockMvc.perform(post("/cart/add/1")
                        .param("quantity", "2")
                        .principal(authentication))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));

        verify(cartService).addToCart(any(Cart.class));
    }

    @Test
    void shouldRemoveFromCart() throws Exception {

        mockMvc.perform(get("/cart/remove/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));

        verify(cartService).removeFromCart(1L);
    }
}