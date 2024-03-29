package fr.educentre.demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity

public class SecurityConfigurer {
    @Bean
    public SecurityFilter securityFilter() {
        return new SecurityFilter();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }



    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
// Standard pour les REST API
        http = http.cors().and().csrf().disable();
        http = http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and();
// On place notre filter dans le middleware
        http = http.addFilterBefore(securityFilter(),
                UsernamePasswordAuthenticationFilter.class);
// Si vous venez du web et souhaitez le faire dans le sens inverse
// Détermination des endpoints privées
        http = http.authorizeHttpRequests((r) ->
                r.requestMatchers("/api/v1/**").authenticated()
                        .anyRequest().permitAll());
        return http.build();
    }
}
//Remarque : configuration de votre application avec le filter et le endpoint
