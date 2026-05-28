package com.ecommerce.controller;

import com.ecommerce.entity.User;
import com.ecommerce.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
    

    // =========================
    // TEST PAGE LOGIN
    // =========================

    @Test
    void shouldReturnLoginPage() throws Exception {

        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));
    }

    // =========================
    // TEST LOGIN AVEC ERREUR
    // =========================

    @Test
    void shouldShowErrorMessageWhenLoginFails() throws Exception {

        mockMvc.perform(get("/login")
                        .param("error", "true"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute(
                        "error",
                        "Email ou mot de passe incorrect"
                ))
                .andExpect(view().name("auth/login"));
    }

    // =========================
    // TEST PAGE REGISTER
    // =========================

    @Test
    void shouldReturnRegisterPage() throws Exception {

        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("user"))
                .andExpect(view().name("auth/register"));
    }

    // =========================
    // TEST REGISTER SUCCESS
    // =========================

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {

        mockMvc.perform(post("/register")
                        .param("name", "Nour")
                        .param("email", "nour@test.com")
                        .param("password", "123456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered=true"));

        verify(userService).register(org.mockito.ArgumentMatchers.any(User.class));
    }

    // =========================
    // TEST REGISTER FAILED
    // =========================

    @Test
    void shouldReturnRegisterPageWhenEmailAlreadyExists() throws Exception {

        doThrow(new RuntimeException("Email exists"))
                .when(userService)
                .register(org.mockito.ArgumentMatchers.any(User.class));

        mockMvc.perform(post("/register")
                        .param("name", "Nour")
                        .param("email", "nour@test.com")
                        .param("password", "123456"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute(
                        "error",
                        "Cet email est déjà utilisé"
                ))
                .andExpect(view().name("auth/register"));
    }
}