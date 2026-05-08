package com.ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    // 1. ENCODAGE DU MOT DE PASSE
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. FILTRE DE SÉCURITÉ (LE CŒUR DE VOTRE DEMANDE)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Désactivé pour faciliter le développement
            .authorizeHttpRequests(auth -> auth
                // BESOIN : Accès libre aux styles et images
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()

                // Administration des produits reservee aux administrateurs
                .requestMatchers("/products/admin/**").hasRole("ADMIN")
                
                // BESOIN : Accès libre à la GALERIE et à l'ACCUEIL avant connexion
                .requestMatchers("/", "/index", "/products/**").permitAll()
                
                // BESOIN : Accès libre aux formulaires d'inscription
                .requestMatchers("/register", "/login").permitAll()
                
                // BESOIN : Tout le reste (panier, profil) demande une connexion
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")               // Votre page personnalisée
                .loginProcessingUrl("/login")      // URL d'action du formulaire
                .defaultSuccessUrl("/products", true) // Redirige vers la boutique après connexion
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/")             // Redirige vers l'accueil après déconnexion
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .permitAll()
            );

        return http.build();
    }
}
