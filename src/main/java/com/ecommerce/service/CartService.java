package com.ecommerce.service;

import com.ecommerce.entity.Cart;
import com.ecommerce.entity.Product;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    private final CartRepository repo;
    private final ProductRepository productRepo;

    public CartService(CartRepository repo, ProductRepository productRepo) {
        this.repo = repo;
        this.productRepo = productRepo;
    }

    public Cart addToCart(Cart cart) {
        List<Cart> existing = repo.findByUserId(cart.getUserId());
        for (Cart c : existing) {
            if (c.getProductId().equals(cart.getProductId())) {
                c.setQuantity(c.getQuantity() + cart.getQuantity());
                return repo.save(c);
            }
        }
        return repo.save(cart);
    }

    public List<Cart> getUserCart(Long userId) {
        return repo.findByUserId(userId);
    }

    public List<CartItemView> getCartItems(String email) {
        Long userId = (long) Math.abs(email.hashCode());
        List<CartItemView> views = new ArrayList<>();
        for (Cart item : repo.findByUserId(userId)) {
            Product product = productRepo.findById(item.getProductId()).orElse(null);
            if (product != null) {
                views.add(new CartItemView(item, product));
            }
        }
        return views;
    }

    public double getCartTotal(Long userId) {
        List<Cart> items = repo.findByUserId(userId);
        double total = 0;
        for (Cart item : items) {
            Product p = productRepo.findById(item.getProductId()).orElse(null);
            if (p != null) total += p.getPrice() * item.getQuantity();
        }
        return total;
    }

    public void removeFromCart(Long cartId) {
        repo.deleteById(cartId);
    }

    public void clearCart(Long userId) {
        repo.deleteByUserId(userId);
    }

    public int getCartCount(Long userId) {
        return repo.findByUserId(userId).stream().mapToInt(Cart::getQuantity).sum();
    }

    public static class CartItemView {
        public Cart cart;
        public Product product;
        public double subtotal;

        public CartItemView(Cart cart, Product product) {
            this.cart = cart;
            this.product = product;
            this.subtotal = product.getPrice() * cart.getQuantity();
        }

        public double getSubtotal() {
            return subtotal;
        }
    }
}
