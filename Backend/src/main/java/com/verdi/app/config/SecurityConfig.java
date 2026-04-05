package com.verdi.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Отключаем CSRF (ты это уже сделал, оставляем)
                .csrf(csrf -> csrf.disable())

                // 2. Явно подключаем CORS конфигурацию, которую мы описали ниже в бине
                .cors(Customizer.withDefaults())

                // 3. Настройка доступов
                .authorizeHttpRequests(auth -> auth
                        // Проверь, чтобы эндпоинты в контроллере точно начинались с /api/auth
                        .requestMatchers("/api/auth/**", "/api/dict/**").permitAll()
                        .anyRequest().authenticated()
                )
                // 4. Важно: если ты используешь базовую форму логина Spring, она может перехватывать запросы.
                // Добавь это, чтобы отключить стандартную страницу логина Spring Security
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());

        return http.build();
    }

    // Этот бин нужен, чтобы браузер не блокировал запросы с порта 63343
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Разрешаем твой порт (например, из IntelliJ Live Edit или просто файл)
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true); // Позволяет передавать куки/токены если понадобится

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}