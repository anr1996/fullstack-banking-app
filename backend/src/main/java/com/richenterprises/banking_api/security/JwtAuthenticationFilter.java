package com.richenterprises.banking_api.security;

import com.richenterprises.banking_api.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * The JWT authentication filter. Intercepts every HTTP request, validates the Bearer token, and 
 * sets the authenticated user in the SecurityContext.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;

    /**
     * The constructor injection of JwtUtil. 
     * 
     * @param jwtUtil (The utility that validates tokens and extracts claims.)
     */
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * This is the core filter logic which runs once per request. 
     * @param request (The HTTP request.)
     * @param response (The HTTP response.)
     * @param filterChain (The chain of remaining filters to execute.)
     * @throws ServletException (Throws if the downstream chain fails.)
     * @throws IOException (Throws if reading or writing the request fails.)
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
            
        // Without a Bearer token the request continues unauthenticated, and the authorization
        // rules in SecurityConfig decide whether that is allowed.
        if(authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(BEARER_PREFIX.length());

        // An invalid or expired token is treated as a no token at all. The request proceeds
        // unauthenticated rather than being rejected here, so the same 401 handling applies as
        // for a missing token.
        if (!jwtUtil.validateToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String email = jwtUtil.extractEmail(token);
        String role = jwtUtil.extractRole(token);

        // A token can validate but still be missing a claim we depend on. Guard against that 
        // rather than building an authentication with null fields.
        if (email == null || role == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // The claim stores the bare role name (ex: ADMIN). Spring's authority convention requires
        // the ROLE_ prefix, so it is added here. This must stay consistent with how the role is 
        // written in Authservice: if the claim ever includes the prefix, remove it here to avoid
        // ROLE_ROLE.
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);

        UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(email, null, List.of(authority));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    
}
