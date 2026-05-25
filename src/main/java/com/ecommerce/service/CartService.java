package com.ecommerce.service;

import com.ecommerce.entity.Cart;
import com.ecommerce.entity.User;
import com.ecommerce.repository.CartRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    private final CartRepository repo;

    public CartService(CartRepository repo) {
        this.repo = repo;
    }

    public Cart addToCart(Cart cart) {

        List<Cart> existing =
                repo.findByUser(cart.getUser());

        for (Cart c : existing) {

            if (
                c.getProduct().getId()
                .equals(cart.getProduct().getId())
            ) {

                c.setQuantity(
                        c.getQuantity()
                        + cart.getQuantity()
                );

                return repo.save(c);
            }
        }

        return repo.save(cart);
    }

    public List<Cart> getUserCart(User user) {

        return repo.findByUser(user);
    }

    public double getCartTotal(User user) {

        List<Cart> items =
                repo.findByUser(user);

        double total = 0;

        for (Cart item : items) {

            total +=
                    item.getProduct().getPrice()
                    * item.getQuantity();
        }

        return total;
    }

    public void removeFromCart(Long cartId) {

        repo.deleteById(cartId);
    }

    public void clearCart(User user) {

        repo.deleteByUser(user);
    }
}