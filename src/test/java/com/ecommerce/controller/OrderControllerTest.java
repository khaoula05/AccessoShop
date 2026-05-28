package com.ecommerce.controller;

import com.ecommerce.entity.Cart;
import com.ecommerce.entity.Order;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.service.OrderService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Dépendances réelles de OrderController (@Autowired dans le contrôleur)
    @MockBean
    private OrderService orderService;

    @MockBean
    private UserService userService;

    @MockBean
    private CartRepository cartRepository;

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private Authentication authentication;

    // =========================
    // shouldRedirectToLoginIfNotAuthenticated
    // =========================

    @Test
    void shouldRedirectToLoginIfNotAuthenticated() throws Exception {

        // Aucun principal → le contrôleur renvoie redirect:/login
        mockMvc.perform(get("/order/checkout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    // =========================
    // shouldDisplayCheckoutPage
    // =========================

    @Test
    void shouldDisplayCheckoutPage() throws Exception {

        User user = new User();
        user.setId(1L);

        Product product = new Product();
        product.setPrice(100.0);
        product.setName("Montre");

        Cart cart = new Cart();
        cart.setProduct(product);
        cart.setQuantity(2);

        when(authentication.getName()).thenReturn("test@test.com");
        when(userService.findByEmail("test@test.com")).thenReturn(user);

        // Le contrôleur utilise cartRepository.findByUser(user), pas CartService
        when(cartRepository.findByUser(user)).thenReturn(List.of(cart));

        mockMvc.perform(get("/order/checkout")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(view().name("order/checkout"))
                .andExpect(model().attributeExists("total"));
    }

    // =========================
    // shouldPlaceOrderSuccessfully
    // =========================

    @Test
    void shouldPlaceOrderSuccessfully() throws Exception {

        User user = new User();
        user.setId(1L);

        Product product = new Product();
        product.setPrice(100.0);
        product.setName("Montre");

        Cart cart = new Cart();
        cart.setProduct(product);
        cart.setQuantity(2);

        when(authentication.getName()).thenReturn("test@test.com");
        when(userService.findByEmail("test@test.com")).thenReturn(user);
        when(cartRepository.findByUser(user)).thenReturn(List.of(cart));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        // L'endpoint réel est POST /order/checkout (pas /order/place)
        // Les paramètres réels : firstName, lastName, phone, shippingAddress, paymentMethod
        mockMvc.perform(post("/order/checkout")
                        .param("firstName", "Nour")
                        .param("lastName", "Test")
                        .param("phone", "0600000000")
                        .param("shippingAddress", "Marrakech")
                        .param("paymentMethod", "cash")
                        .principal(authentication))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/order/confirmation")); // redirect réel
    }
}