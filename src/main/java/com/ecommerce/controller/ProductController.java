package com.ecommerce.controller;

import com.ecommerce.entity.Product;
import com.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.util.Map;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService service;
    private final List<String> categories = List.of("boucle", "montre", "collier", "bague", "bracelet");

    private final Cloudinary cloudinary;

public ProductController(ProductService service, Cloudinary cloudinary) {
    this.service = service;
    this.cloudinary = cloudinary;
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
        else if (category == null || category.equalsIgnoreCase("boutique")) {
            List<String> localImages = Arrays.asList(
                "montre1.png", "collier1.png", 
                "bague1.png", "bracelet1.png", 
                "gold1.png", "gold3.png"
            );
            model.addAttribute("localImages", localImages);
            model.addAttribute("currentCategory", "boutique");
            model.addAttribute("view", "gallery"); 
        } 
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

    @GetMapping("/admin/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String newForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categories);
        return "admin/product-form";
    }

    @GetMapping("/admin/edit/{id}")
@PreAuthorize("hasRole('ADMIN')")
public String editForm(@PathVariable Long id, Model model) {
    Product product = service.getProductById(id);
    if (product == null) return "redirect:/products";
    model.addAttribute("product", product);
    model.addAttribute("categories", categories);
    return "admin/product-form";
}

@PostMapping("/admin/update/{id}")
@PreAuthorize("hasRole('ADMIN')")
public String update(@PathVariable Long id,
                     @Valid @ModelAttribute Product product,
                     BindingResult result,
                     @RequestParam("imageFile") MultipartFile imageFile,
                     Model model) {

    if (result.hasErrors()) {
        model.addAttribute("categories", categories);
        return "admin/product-form";
    }

    try {
        Product existing = service.getProductById(id);
        product.setId(id);

        if (imageFile != null && !imageFile.isEmpty()) {
            Map uploadResult = cloudinary.uploader().upload(
                imageFile.getBytes(),
                ObjectUtils.emptyMap()
            );
            product.setImageUrl((String) uploadResult.get("secure_url"));
        } else {
            // Garde l'ancienne image
            product.setImageUrl(existing.getImageUrl());
        }

        if (product.getCategory() != null) {
            product.setCategory(product.getCategory().toLowerCase());
        }

    } catch (Exception e) {
        e.printStackTrace();
        model.addAttribute("error", "Erreur lors du traitement de l'image.");
        model.addAttribute("categories", categories);
        return "admin/product-form";
    }

    service.saveProduct(product);
    return "redirect:/products?category=" + product.getCategory();
}

    @PostMapping("/admin/save")
    @PreAuthorize("hasRole('ADMIN')")
    public String save(@Valid @ModelAttribute Product product, 
                       BindingResult result, 
                       @RequestParam("imageFile") MultipartFile imageFile, 
                       Model model) {
        
        if(product.getCategory() != null) {
            product.setCategory(product.getCategory().toLowerCase());
        }

        if (result.hasErrors()) {
            model.addAttribute("categories", categories);
            return "admin/product-form";
        }

        try {
            // NOUVEAU CODE - Cloudinary
            if (imageFile != null && !imageFile.isEmpty()) {
                Map uploadResult = cloudinary.uploader().upload(
                    imageFile.getBytes(),
                    ObjectUtils.emptyMap()
                );
                product.setImageUrl((String) uploadResult.get("secure_url"));
}
            else if (product.getImageUrl() == null || product.getImageUrl().isBlank()) {
                product.setImageUrl(defaultImageFor(product.getCategory()));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Erreur lors du traitement de l'image.");
            model.addAttribute("categories", categories);
            return "admin/product-form";
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
        // MODIFICATION ICI : Les fallbacks pointent aussi vers le dossier virtuel /uploads/
        return switch (category) {
            case "montre" -> "/uploads/montre1.png";
            case "collier" -> "/uploads/collier1.png";
            case "bague" -> "/uploads/bague1.png";
            case "bracelet" -> "/uploads/bracelet1.png";
            default -> "/uploads/gold1.png"; 
        };
    }
}
