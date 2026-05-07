package com.ecommerce.controller;

import com.ecommerce.entity.Cart;
import com.ecommerce.entity.Product;
import com.ecommerce.service.CartService;
import com.ecommerce.service.ProductService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final ProductService productService;

    public CartController(CartService cartService, ProductService productService) {
        this.cartService = cartService;
        this.productService = productService;
    }

    @GetMapping
    public String viewCart(Authentication auth, Model model) {
        if (auth == null) return "redirect:/login";
        
        Long userId = getUserId(auth);
        List<Cart> cartItems = cartService.getUserCart(userId);
        
        List<CartItemView> views = new ArrayList<>();
        for (Cart c : cartItems) {
            Product p = productService.getProductById(c.getProductId());
            if (p != null) views.add(new CartItemView(c, p));
        }
        
        model.addAttribute("cartItems", views);
        model.addAttribute("total", cartService.getCartTotal(userId));
        return "cart/view";
    }

    @PostMapping("/add/{productId}")
    public String addToCart(@PathVariable Long productId, 
                            @RequestParam(defaultValue = "1") int quantity,
                            Authentication auth) {
        if (auth == null) return "redirect:/login";
        
        Cart cart = new Cart();
        cart.setUserId(getUserId(auth));
        cart.setProductId(productId);
        cart.setQuantity(quantity);
        cartService.addToCart(cart);
        return "redirect:/cart";
    }

    @GetMapping("/remove/{cartId}")
    public String removeFromCart(@PathVariable Long cartId) {
        cartService.removeFromCart(cartId);
        return "redirect:/cart";
    }

    private Long getUserId(Authentication auth) {
        // On utilise le hashCode de l'email comme ID unique temporaire
        return (long) Math.abs(auth.getName().hashCode());
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
    }
}