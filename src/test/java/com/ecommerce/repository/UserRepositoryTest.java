package com.ecommerce.repository;

import com.ecommerce.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

    @Test
    void shouldFindUserByEmail() {

        User user = new User();
        user.setName("Test User");
        user.setEmail("test@test.com");
        user.setPassword("123456");

        repository.save(user);

        User found =
                repository.findByEmail("test@test.com");

        assertNotNull(found);
    }
}