package com.ecommerce.controller;

import com.ecommerce.service.CartService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)	
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @MockBean
    private Authentication authentication;


    @Test
    void shouldRedirectToLoginIfNotAuthenticated() throws Exception {

        mockMvc.perform(get("/order/checkout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void shouldDisplayCheckoutPage() throws Exception {

        when(authentication.getName()).thenReturn("test@test.com");

        when(cartService.getCartItems("test@test.com"))
                .thenReturn(List.of());

        mockMvc.perform(get("/order/checkout")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(view().name("order/checkout"))
                .andExpect(model().attributeExists("cartItems"))
                .andExpect(model().attributeExists("total"));
    }

    @Test
    void shouldPlaceOrderSuccessfully() throws Exception {

        mockMvc.perform(post("/order/place")
                        .param("prenom", "Nour")
                        .param("nom", "Test")
                        .param("email", "test@test.com")
                        .param("telephone", "0600000000")
                        .param("adresse", "Marrakech")
                        .param("ville", "Marrakech")
                        .param("modeLivraison", "standard")
                        .param("modePaiement", "cash"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }
}