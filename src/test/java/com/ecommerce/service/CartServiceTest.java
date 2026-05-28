package com.ecommerce.service;

import com.ecommerce.entity.Cart;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.repository.CartRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository repo;

    @InjectMocks
    private CartService service;

    // =========================
    // Helpers
    // =========================

    private User makeUser() {
        User u = new User();
        u.setId(1L);
        return u;
    }

    private Product makeProduct(Long id, double price) {
        Product p = new Product();
        p.setId(id);
        p.setPrice(price);
        return p;
    }

    // =========================
    // Tests
    // =========================

    @Test
    void shouldAddToCart() {

        User user = makeUser();
        Product product = makeProduct(1L, 100);

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setProduct(product);
        cart.setQuantity(2);

        when(repo.findByUser(user)).thenReturn(List.of());
        when(repo.save(cart)).thenReturn(cart);

        Cart saved = service.addToCart(cart);

        assertNotNull(saved);
        verify(repo).save(cart);
    }

    @Test
    void shouldMergeExistingCartItem() {

        User user = makeUser();
        Product product = makeProduct(1L, 100);

        Cart existing = new Cart();
        existing.setUser(user);
        existing.setProduct(product);
        existing.setQuantity(1);

        Cart newCart = new Cart();
        newCart.setUser(user);
        newCart.setProduct(product);
        newCart.setQuantity(2);

        when(repo.findByUser(user)).thenReturn(List.of(existing));
        when(repo.save(any(Cart.class))).thenReturn(existing);

        Cart result = service.addToCart(newCart);

        assertEquals(3, result.getQuantity());
    }

    @Test
    void shouldReturnUserCart() {

        User user = makeUser();

        when(repo.findByUser(user)).thenReturn(List.of(new Cart()));

        List<Cart> carts = service.getUserCart(user);

        assertFalse(carts.isEmpty());
    }

    @Test
    void shouldCalculateCartTotal() {

        User user = makeUser();
        Product product = makeProduct(1L, 100);

        Cart cart = new Cart();
        cart.setProduct(product);
        cart.setQuantity(2);

        when(repo.findByUser(user)).thenReturn(List.of(cart));

        double total = service.getCartTotal(user);

        assertEquals(200, total);
    }

    @Test
    void shouldRemoveCart() {

        service.removeFromCart(1L);

        verify(repo).deleteById(1L);
    }

    @Test
    void shouldClearCart() {

        User user = makeUser();

        service.clearCart(user);

        verify(repo).deleteByUser(user);
    }
}