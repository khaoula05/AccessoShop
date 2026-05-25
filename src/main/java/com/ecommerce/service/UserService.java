package com.ecommerce.service;

import com.ecommerce.entity.User;
import com.ecommerce.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;

    public UserService(UserRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    public User register(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        return repo.save(user);
    }

    public User findByEmail(String email) {
        return repo.findByEmail(email);
    }

    public User getUserById(Long id) {
        // Corrigé avec 'repo' pour effacer la ligne rouge de compilation
        return repo.findById(id).orElse(null);
    }
}