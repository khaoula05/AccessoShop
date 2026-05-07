package com.ecommerce.controller;

import com.ecommerce.entity.Cart;
import com.ecommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private CartService cartService;

    @GetMapping("/checkout")
    public String checkout(Authentication authentication, Model model) {
        if (authentication == null) return "redirect:/login";

        String email = authentication.getName();
        List<CartService.CartItemView> cartItems = cartService.getCartItems(email);
        double total = cartItems.stream().mapToDouble(CartService.CartItemView::getSubtotal).sum();

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", total);
        return "order/checkout";
    }

    @PostMapping("/place")
    public String placeOrder(Authentication authentication,
                             @RequestParam String prenom,
                             @RequestParam String nom,
                             @RequestParam String email,
                             @RequestParam String telephone,
                             @RequestParam String adresse,
                             @RequestParam String ville,
                             @RequestParam(required = false) String codePostal,
                             @RequestParam(required = false) String region,
                             @RequestParam String modeLivraison,
                             @RequestParam String modePaiement,
                             @RequestParam(required = false) String noteCommande,
                             RedirectAttributes redirectAttributes) {

        // TODO: implémenter la logique métier (création commande en BDD)
        redirectAttributes.addFlashAttribute("successMessage",
            "Commande confirmée ! Merci " + prenom + ", vous recevrez une confirmation par email.");
        return "redirect:/";
    }
}
