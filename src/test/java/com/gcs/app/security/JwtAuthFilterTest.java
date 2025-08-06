package com.gcs.app.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private UserDetails userDetails;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private JwtAuthFilter filter;

    @BeforeEach
    void setup() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void doFilterInternal_noAuthorizationHeader_callsFilterChain() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtUtil, userDetailsService, securityContext);
    }

    @Test
    void doFilterInternal_authHeaderNotBearer_callsFilterChain() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic some-token");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtUtil, userDetailsService, securityContext);
    }

    @Test
    void doFilterInternal_validToken_authenticatesUser() throws Exception {
        String token = "valid.jwt.token";
        String username = "test.user";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractUsername(token)).thenReturn(username);
        when(securityContext.getAuthentication()).thenReturn(null);

        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(userDetails.isEnabled()).thenReturn(true);
        when(jwtUtil.validateToken(token, username)).thenReturn(true);
        when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());

        filter.doFilterInternal(request, response, filterChain);

        ArgumentCaptor<UsernamePasswordAuthenticationToken> authCaptor = ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(securityContext).setAuthentication(authCaptor.capture());
        UsernamePasswordAuthenticationToken auth = authCaptor.getValue();

        assertEquals(userDetails, auth.getPrincipal());
        assertNull(auth.getCredentials());
        assertEquals(userDetails.getAuthorities(), auth.getAuthorities());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_tokenInvalid_noAuthenticationSet() throws Exception {
        String token = "invalid.jwt.token";
        String username = "test.user";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractUsername(token)).thenReturn(username);
        when(securityContext.getAuthentication()).thenReturn(null);

        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(userDetails.isEnabled()).thenReturn(true);
        when(jwtUtil.validateToken(token, username)).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        verify(securityContext, never()).setAuthentication(any());
        verify(filterChain).doFilter(request, response);
    }
}