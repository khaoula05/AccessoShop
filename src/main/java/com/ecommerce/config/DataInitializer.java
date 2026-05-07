package com.ecommerce.config;

import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    public DataInitializer(ProductRepository productRepo, UserRepository userRepo, PasswordEncoder encoder) {
        this.productRepo = productRepo;
        this.userRepo = userRepo;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        // 1. Initialisation des Utilisateurs (Admin et Test)
        if (userRepo.count() == 0) {
            User admin = new User();
            admin.setEmail("admin@accesso.com");
            admin.setPassword(encoder.encode("admin123"));
            admin.setName("Administrateur");
            admin.setRole("ADMIN");
            userRepo.save(admin);

            User user = new User();
            user.setEmail("test@accesso.com");
            user.setPassword(encoder.encode("test123"));
            user.setName("Client Test");
            user.setRole("USER");
            userRepo.save(user);
        }

        // 2. Initialisation des Produits avec vos NOUVELLES catégories
        if (productRepo.count() == 0) {
            // Exemples de produits pour remplir les listes
            productRepo.save(createProduct("Montre Élégante Or", 299.99, "Montre classique dorée", "montre", "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400"));
            productRepo.save(createProduct("Collier Perles Luxe", 89.99, "Collier délicat avec perles", "collier", "https://images.unsplash.com/photo-1515562141207-7a88fb7ce338?w=400"));
            productRepo.save(createProduct("Bague Diamant", 499.99, "Bague en argent et diamant", "bague", "https://images.unsplash.com/photo-1535632066927-ab7c9ab60908?w=400"));
            productRepo.save(createProduct("Bracelet Argent", 59.99, "Bracelet ajustable 925", "bracelet", "https://images.unsplash.com/photo-1573408301185-9519f94816b5?w=400"));
            productRepo.save(createProduct("Boucles d'oreilles", 45.00, "Boucles style créoles", "boucle", "https://images.unsplash.com/photo-1535632066927-ab7c9ab60908?w=400"));
        }
    }

    private Product createProduct(String name, double price, String desc, String category, String imageUrl) {
        Product p = new Product();
        p.setName(name);
        p.setPrice(price);
        p.setDescription(desc);
        p.setCategory(category.toLowerCase()); // On force en minuscule pour éviter les erreurs
        p.setImageUrl(imageUrl);
        p.setStock(10);
        p.setRating(4.5);
        p.setFeatured(true);
        return p;
    }
}