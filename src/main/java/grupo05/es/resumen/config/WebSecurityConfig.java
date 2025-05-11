package grupo05.es.resumen.config;

import grupo05.es.resumen.security.SecurityUserDetailsService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final SecurityUserDetailsService userDetailsService;

    public WebSecurityConfig(SecurityUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/registro", "/login", "/css/**", "/js/**",
                        "/images/**", "/h2-console/**","/pruebas/files/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/escritor/**").hasRole("ESCRITOR")
                .requestMatchers("/lector/**").hasRole("LECTOR")
                .requestMatchers("/visitante/**").hasRole("VISITANTE")
                    .requestMatchers("/valoraciones/**").hasRole("LECTOR")
                    .requestMatchers("/pruebas/files/subir/bd/post").hasRole("ESCRITOR")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/catalogo", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .permitAll()
            )
            .csrf(csrf -> csrf.disable()) // Necesario para permitir H2 y formularios sin tokens CSRF
            .headers(headers -> headers.frameOptions().disable()) // Permitir uso de iframe en H2 console
            .userDetailsService(userDetailsService)
            .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
