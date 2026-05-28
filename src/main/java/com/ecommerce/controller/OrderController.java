package com.ecommerce.controller;

import com.ecommerce.entity.Cart;
import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderItem;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.service.OrderService;
import com.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;

    // ==========================================
    // ANCIENNES MÉTHODES (CONSERVÉES À L'IDENTIQUE)
    // ==========================================

    @GetMapping("/checkout")
    public String showCheckoutPage(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        User user = userService.findByEmail(principal.getName());
        List<Cart> cartItems = cartRepository.findByUser(user);

        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }

        double subtotal = 0;
        for (Cart cart : cartItems) {
            subtotal += cart.getProduct().getPrice() * cart.getQuantity();
        }

        double shipping = (subtotal >= 500) ? 0 : 30;
        double tva = subtotal * 0.20;
        double total = subtotal + shipping + tva;

        model.addAttribute("subtotal", subtotal);
        model.addAttribute("shipping", shipping);
        model.addAttribute("tva", tva);
        model.addAttribute("total", total);
        model.addAttribute("cartItems", cartItems);   // ← images produits pour le récap paiement
        
        Order order = new Order();
        order.setTotalAmount(total);
        model.addAttribute("order", order);

        return "order/checkout";
    }

    @PostMapping("/checkout")
    public String processCheckout(@ModelAttribute("order") Order order,
                                  Principal principal,
                                  @RequestParam(value = "firstName", required = false, defaultValue = "") String firstName,
                                  @RequestParam(value = "lastName", required = false, defaultValue = "") String lastName,
                                  @RequestParam(value = "phone", required = false, defaultValue = "") String phone,
                                  @RequestParam(value = "shippingAddress", required = false, defaultValue = "") String shippingAddress,
                                  @RequestParam(value = "paymentMethod", required = false, defaultValue = "COD") String paymentMethod,
                                  Model model) {

        if (principal == null) {
            return "redirect:/login";
        }

        User user = userService.findByEmail(principal.getName());
        List<Cart> cartItems = cartRepository.findByUser(user);

        if (cartItems.isEmpty()) {
            return "redirect:/cart/view";
        }

        double subtotal = 0;
        for (Cart cart : cartItems) {
            subtotal += cart.getProduct().getPrice() * cart.getQuantity();
        }
        double shipping = (subtotal >= 500) ? 0 : 30;
        double tva = subtotal * 0.20;
        double total = subtotal + shipping + tva;

        try {
            order.setUserId(user.getId());
            order.setTotalAmount(total);
            order.setStatus("EN_COURS");
            order.setPaymentMethod(paymentMethod);

            String adresseComplete = "Nom: " + firstName + " " + lastName + " | Tél: " + phone + " | Addr: " + shippingAddress;
            order.setShippingAddress(adresseComplete);

            List<OrderItem> items = new ArrayList<>();
            for (Cart cart : cartItems) {
                Product product = cart.getProduct();
                OrderItem item = new OrderItem();
                item.setProductName(product.getName());
                item.setPrice(product.getPrice());
                item.setQuantity(cart.getQuantity());
                item.setOrder(order);
                items.add(item);
            }
            order.setItems(items);

            orderRepository.save(order);
            cartRepository.deleteAll(cartItems);

            return "redirect:/order/confirmation";

        } catch (Exception e) {
            System.err.println("ERREUR CRITIQUE COMMANDE : " + e.getMessage());
            e.printStackTrace();
            
            model.addAttribute("subtotal", subtotal);
            model.addAttribute("shipping", shipping);
            model.addAttribute("tva", tva);
            model.addAttribute("total", total);
            model.addAttribute("error", "Erreur lors de la validation : " + e.getMessage());
            return "order/checkout";
        }
    }

    @GetMapping("/confirmation")
    public String orderConfirmation() {
        return "order/confirmation";
    }

    @GetMapping("/my-purchases")
    public String myPurchases(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        User user = userService.findByEmail(principal.getName());
        List<Order> orders = orderService.getOrdersByUser(user);
        model.addAttribute("orders", orders);
        return "order/my-purchases";
    }

    // ==========================================
    // NOUVELLES MÉTHODES ADMIN CORRIGÉES & SÉCURISÉES
    // ==========================================

    @GetMapping("/admin/list")
    public String listAdminOrders(Model model) {
        try {
            // Récupération sécurisée des commandes depuis le repository
            List<Order> orders = orderRepository.findAll();
            
            if (orders == null) {
                orders = new ArrayList<>();
            }
            
            model.addAttribute("orders", orders);
            
        } catch (Exception e) {
            // Écrit le problème exact de base de données dans ta console d'IDE
            System.err.println("❌ CRASH INTERCEPTÉ (FINDALL) : " + e.getMessage());
            e.printStackTrace();
            
            // On injecte une liste vide pour forcer l'affichage de la page HTML sans planter
            model.addAttribute("orders", new ArrayList<>());
            model.addAttribute("dbError", "Erreur base de données : " + e.getMessage());
        }
        
        // CORRECTION DU NOM ET DU DOSSIER : dossier "admin", fichier "orders-management"
        return "admin/orders-management"; 
    }

    @PostMapping("/admin/update-status/{id}")
    public String updateOrderStatus(@PathVariable("id") Long id, @RequestParam("status") String status) {
        try {
            Order order = orderRepository.findById(id).orElse(null);
            if (order != null) {
                order.setStatus(status);
                orderRepository.save(order);
            }
        } catch (Exception e) {
            System.err.println("❌ CRASH INTERCEPTÉ (UPDATE STATUS) : " + e.getMessage());
            e.printStackTrace();
        }
        
        // Redirection propre vers l'URL admin de gestion
        return "redirect:/order/admin/list";
    }
}