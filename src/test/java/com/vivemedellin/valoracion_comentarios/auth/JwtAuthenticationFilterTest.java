package com.vivemedellin.valoracion_comentarios.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    private JwtAuthenticationFilter filter;
    private final String secret = "12345678901234567890123456789012"; // 32 bytes for HMAC-SHA256

    @BeforeEach
    void setup() {
        filter = new JwtAuthenticationFilter();
        // Inject the secret via reflection since @Value won't work in unit tests
        try {
            var field = JwtAuthenticationFilter.class.getDeclaredField("jwtSecret");
            field.setAccessible(true);
            field.set(filter, secret);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        SecurityContextHolder.clearContext();
    }

    @Test
    void testDoFilterInternal_validToken_setsAuthentication() throws Exception {
        // Create JWT token
        Map<String, Object> metaData = new HashMap<>();
        metaData.put("role", "admin");

        Key key = Keys.hmacShaKeyFor(secret.getBytes());
        String token = Jwts.builder()
                .setSubject("test-user-id")
                .claim("raw_user_meta_data", metaData)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 10000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // Mock request/response
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/graphql");
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();

        FilterChain chain = mock(FilterChain.class);

        // Act
        filter.doFilterInternal(request, response, chain);

        // Assert
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("test-user-id", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        assertTrue(SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_admin")));

        verify(chain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_invalidToken_doesNotThrow() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/graphql");
        request.addHeader("Authorization", "Bearer invalid.token.value");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        assertDoesNotThrow(() -> filter.doFilterInternal(request, response, chain));
        verify(chain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testShouldNotFilter_nonGraphQL_returnsTrue() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/some-other-path");

        assertTrue(filter.shouldNotFilter(request));
    }

    @Test
    void testShouldNotFilter_graphql_returnsFalse() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/graphql");

        assertFalse(filter.shouldNotFilter(request));
    }
}
