package com.ecommerce.controller;

import com.ecommerce.service.ProductService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final ProductService productService;

    public HomeController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/")
    public String home(Model model, Authentication auth) {
        model.addAttribute("featuredProducts", productService.getFeaturedProducts());
        model.addAttribute("categories", java.util.List.of("Montres", "Sacs", "Bijoux", "Lunettes"));
        return "index";
    }
}
