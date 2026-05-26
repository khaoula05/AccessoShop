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

        if (productRepo.count() == 0) {
            // MODIFICATION ICI : Utilisation de vos fichiers locaux réels via le chemin virtuel /uploads/
            productRepo.save(createProduct("Montre Élégante Or", 299.99, "Montre classique dorée", "montre", "/uploads/montre1.png"));
            productRepo.save(createProduct("Collier Perles Luxe", 89.99, "Collier délicat avec perles", "collier", "/uploads/collier1.png"));
            productRepo.save(createProduct("Bague Diamant", 499.99, "Bague en argent et diamant", "bague", "/uploads/bague1.png"));
            productRepo.save(createProduct("Bracelet Argent", 59.99, "Bracelet ajustable 925", "bracelet", "/uploads/bracelet1.png"));
            productRepo.save(createProduct("Boucles d'oreilles", 45.00, "Boucles style créoles", "boucle", "/uploads/gold1.png"));
        }
    }

    private Product createProduct(String name, double price, String desc, String category, String imageUrl) {
        Product p = new Product();
        p.setName(name);
        p.setPrice(price);
        p.setDescription(desc);
        p.setCategory(category.toLowerCase());
        p.setImageUrl(imageUrl);
        p.setStock(10);
        p.setRating(4.5);
        p.setFeatured(true);
        return p;
    }
}