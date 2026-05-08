package com.ecommerce.service;

import com.ecommerce.entity.Cart;
import com.ecommerce.entity.Product;
import com.ecommerce.repository.CartRepository;
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
class CartServiceTest {

    @Mock
    private CartRepository repo;

    @Mock
    private ProductRepository productRepo;

    @InjectMocks
    private CartService service;

    @Test
    void shouldAddToCart() {

        Cart cart = new Cart();
        cart.setUserId(1L);
        cart.setProductId(1L);
        cart.setQuantity(2);

        when(repo.findByUserId(1L))
                .thenReturn(List.of());

        when(repo.save(cart))
                .thenReturn(cart);

        Cart saved = service.addToCart(cart);

        assertNotNull(saved);

        verify(repo).save(cart);
    }

    @Test
    void shouldMergeExistingCartItem() {

        Cart existing = new Cart();
        existing.setUserId(1L);
        existing.setProductId(1L);
        existing.setQuantity(1);

        Cart newCart = new Cart();
        newCart.setUserId(1L);
        newCart.setProductId(1L);
        newCart.setQuantity(2);

        when(repo.findByUserId(1L))
                .thenReturn(List.of(existing));

        when(repo.save(any(Cart.class)))
                .thenReturn(existing);

        Cart result = service.addToCart(newCart);

        assertEquals(3, result.getQuantity());
    }

    @Test
    void shouldReturnUserCart() {

        when(repo.findByUserId(1L))
                .thenReturn(List.of(new Cart()));

        List<Cart> carts = service.getUserCart(1L);

        assertFalse(carts.isEmpty());
    }

    @Test
    void shouldCalculateCartTotal() {

        Cart cart = new Cart();
        cart.setProductId(1L);
        cart.setQuantity(2);

        Product product = new Product();
        product.setPrice(100);

        when(repo.findByUserId(1L))
                .thenReturn(List.of(cart));

        when(productRepo.findById(1L))
                .thenReturn(Optional.of(product));

        double total = service.getCartTotal(1L);

        assertEquals(200, total);
    }

    @Test
    void shouldRemoveCart() {

        service.removeFromCart(1L);

        verify(repo).deleteById(1L);
    }

    @Test
    void shouldClearCart() {

        service.clearCart(1L);

        verify(repo).deleteByUserId(1L);
    }

    @Test
    void shouldReturnCartCount() {

        Cart cart1 = new Cart();
        cart1.setQuantity(2);

        Cart cart2 = new Cart();
        cart2.setQuantity(3);

        when(repo.findByUserId(1L))
                .thenReturn(List.of(cart1, cart2));

        int count = service.getCartCount(1L);

        assertEquals(5, count);
    }
}