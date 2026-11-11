package com.atlas.bank.atlas_bank.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final String ROLE_PREFIX = "ROLE_";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .authorizeHttpRequests(auth -> auth
                        //Accounts
                        //Crear y listar cuentas: solo ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/v1/accounts").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,"/api/v1/accounts").hasRole("ADMIN")
                        //Cerrar cuenta: solo ADMIN
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/accounts/{id}/close").hasRole("ADMIN")
                        //Consultar una cuenta o su dashboard: USER y ADMIN
                        .requestMatchers(HttpMethod.GET,"/api/v1/accounts/{id}").hasAnyRole("USER","ADMIN")
                        .requestMatchers(HttpMethod.GET,"/api/v1/accounts/{id}/dashboard").hasAnyRole("USER","ADMIN")

                        //Transactions
                        //Transferir y consultar movimientos: USER y ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/v1/transactions/transfer")
                        .hasAnyRole("USER","ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/transactions/{id}/transactions")
                        .hasAnyRole("USER","ADMIN")

                        //AI
                        .requestMatchers("/api/v1/ai/**").permitAll()

                        //H2
                        .requestMatchers("/h2-console/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer( oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                )
                //estas 2 son para tratar lo de H2
                .headers( headers ->
                        headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    //Hay que crear un conversor personalizado para que cuando keycloak genera un token, SecurityFilterChain lo entienda
    //Keycloak devuelve los roles del realm tal cual se crearon (ej: "admin", "user"), sin el prefijo ROLE_
    //que Spring Security exige para hasRole()/hasAnyRole(). Se normaliza a ROLE_ADMIN / ROLE_USER.
   //Visibilidad de paquete para poder testear el mapeo de roles sin levantar Keycloak
   JwtAuthenticationConverter jwtAuthenticationConverter() {
       JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

       converter.setJwtGrantedAuthoritiesConverter(jwt -> {
           var realmAccess = jwt.getClaimAsMap("realm_access");
           if (realmAccess == null || realmAccess.get("roles") == null) {
               return List.of();
           }
           var roles = (List<String>) realmAccess.get("roles");

           return roles.stream()
                   .filter(Objects::nonNull)
                   .map(role -> role.trim().toUpperCase(Locale.ROOT))
                   .map(role -> role.startsWith(ROLE_PREFIX) ? role : ROLE_PREFIX + role)
                   .map(SimpleGrantedAuthority::new)
                   .collect(Collectors.toList());
       });
       return converter;
   }
}
