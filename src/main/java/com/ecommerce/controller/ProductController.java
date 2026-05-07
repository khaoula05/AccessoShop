package com.ecommerce.controller;

import com.ecommerce.entity.Product;
import com.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService service;
    
    // 1. Ordre exact demandé : Boucle, Montre, Collier, Bague, Bracelet
    private final List<String> categories = List.of("boucle", "montre", "collier", "bague", "bracelet");

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String category,
                       @RequestParam(required = false) String search,
                       Model model) {
        
        List<Product> products = new ArrayList<>();

        if (search != null && !search.isBlank()) {
            products = service.searchProducts(search);
            model.addAttribute("search", search);
            model.addAttribute("currentCategory", "recherche");
            model.addAttribute("view", "products");
        }

        // 2. LOGIQUE DE LA GALERIE (Page d'accueil de la boutique)
        else if (category == null || category.equalsIgnoreCase("boutique")) {
            // Liste mise à jour avec vos images gold1, gold2, gold3
            List<String> localImages = Arrays.asList(
                 "montre1.jpg", "collier1.jpg", 
                "bague1.jpg", "bracelet1.jpg", 
                "gold1.jpg", "gold3.jpg"
            );
            model.addAttribute("localImages", localImages);
            model.addAttribute("currentCategory", "boutique");
            model.addAttribute("view", "gallery"); // Pour activer l'affichage galerie dans list.html
        } 
        
        // 3. LOGIQUE DES CATÉGORIES SPÉCIFIQUES
        else {
            products = service.getProductsByCategory(category.toLowerCase());
            model.addAttribute("currentCategory", category.toLowerCase());
            model.addAttribute("view", "products");
        }
        
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        return "products/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Product product = service.getProductById(id);
        if (product == null) return "redirect:/products";
        
        model.addAttribute("product", product);
        model.addAttribute("related", service.getProductsByCategory(product.getCategory()));
        return "products/detail";
    }

    // --- SECTION ADMIN ---

    @GetMapping("/admin/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String newForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categories);
        return "admin/product-form";
    }

    @PostMapping("/admin/save")
    @PreAuthorize("hasRole('ADMIN')")
    public String save(@Valid @ModelAttribute Product product, BindingResult result, Model model) {
        if(product.getCategory() != null) {
            product.setCategory(product.getCategory().toLowerCase());
        }

        if (result.hasErrors()) {
            model.addAttribute("categories", categories);
            return "admin/product-form";
        }

        if (product.getImageUrl() == null || product.getImageUrl().isBlank()) {
            product.setImageUrl(defaultImageFor(product.getCategory()));
        }

        service.saveProduct(product);
        return "redirect:/products?category=" + product.getCategory();
    }

    @GetMapping("/admin/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id) {
        service.deleteProduct(id);
        return "redirect:/products";
    }

    private String defaultImageFor(String category) {
        return switch (category) {
            case "montre" -> "/images/montre1.jpg";
            case "collier" -> "/images/collier1.jpg";
            case "bague" -> "/images/bague1.jpg";
            case "bracelet" -> "/images/bracelet1.jpg";
            default -> "/images/gold1.jpg";
        };
    }
}
