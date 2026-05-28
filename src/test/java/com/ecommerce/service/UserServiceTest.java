package com.ecommerce.service;

import com.ecommerce.entity.User;
import com.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repo;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private UserService service;

    @Test
    void shouldRegisterUser() {

        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("123456");

        when(encoder.encode("123456"))
                .thenReturn("encodedPassword");

        when(repo.save(any(User.class)))
                .thenReturn(user);

        User saved = service.register(user);

        assertNotNull(saved);

        verify(encoder).encode("123456");
        verify(repo).save(any(User.class));
    }

    @Test
    void shouldFindUserByEmail() {

        User user = new User();
        user.setEmail("test@test.com");

        when(repo.findByEmail("test@test.com"))
                .thenReturn(user);

        User result = service.findByEmail("test@test.com");

        assertNotNull(result);
        assertEquals("test@test.com", result.getEmail());
    }
}