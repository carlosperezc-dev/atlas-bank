package com.atlas.bank.atlas_bank.infrastructure.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SecurityConfigTest {

    private final SecurityConfig securityConfig = new SecurityConfig();

    private Set<String> authoritiesFor(List<String> realmRoles){
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(300))
                .claim("realm_access", Map.of("roles", realmRoles))
                .build();

        JwtAuthenticationConverter converter = securityConfig.jwtAuthenticationConverter();

        return converter.convert(jwt).getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith("ROLE_"))
                .collect(Collectors.toSet());
    }

    @Test
    @DisplayName("El rol admin de Keycloak se mapea a ROLE_ADMIN")
    void shouldMapAdminRoleWithPrefix(){
        assertTrue(authoritiesFor(List.of("admin")).contains("ROLE_ADMIN"));
    }

    @Test
    @DisplayName("El rol user de Keycloak se mapea a ROLE_USER")
    void shouldMapUserRoleWithPrefix(){
        assertTrue(authoritiesFor(List.of("user")).contains("ROLE_USER"));
    }

    @Test
    @DisplayName("Los roles ya prefijados no se duplican")
    void shouldNotDuplicateRolePrefix(){
        assertEquals(Set.of("ROLE_ADMIN"), authoritiesFor(List.of("ROLE_ADMIN")));
    }

    @Test
    @DisplayName("Un token sin realm_access no otorga authorities")
    void shouldReturnEmptyWhenNoRealmAccess(){
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(300))
                .build();

        assertTrue(securityConfig.jwtAuthenticationConverter()
                .convert(jwt).getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .noneMatch(authority -> authority.startsWith("ROLE_")));
    }
}
