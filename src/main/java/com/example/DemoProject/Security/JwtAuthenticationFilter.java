package com.example.DemoProject.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain)
            throws ServletException, IOException {

        if (request.getMethod().equals("OPTIONS")) {
            filterChain.doFilter(request, response);
            return;
        }

        System.out.println("JwtAuthenticationFilter triggered for: " + request.getRequestURI());

        final String authHeader = request.getHeader("Authorization");
        System.out.println("Authorization Header: " + authHeader);  // Log incoming header

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("No Bearer token found, skipping filter.");
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);
        System.out.println("Extracted Token: " + jwt);

        String userEmail;

        try {
            // ✅ Try to extract username from token
            userEmail = jwtUtil.extractUsername(jwt);
            System.out.println("Extracted Username from Token: " + userEmail);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                System.out.println("Fetching UserDetails for: " + userEmail);
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
                System.out.println("Loaded user from DB: " + userDetails.getUsername());

                if (jwtUtil.isTokenValid(jwt, userDetails)) {
                    System.out.println("Token is valid for user: " + userEmail);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    System.out.println("User authenticated successfully. Roles: " + userDetails.getAuthorities());
                } else {
                    System.out.println("Token is NOT valid for user: " + userEmail);
                    // Optionally return 401 here too
                }
            } else {
                System.out.println("Skipping authentication as SecurityContext already holds an authentication object.");
            }

        } catch (io.jsonwebtoken.ExpiredJwtException |
                 io.jsonwebtoken.MalformedJwtException |
                 io.jsonwebtoken.SignatureException |
                 IllegalArgumentException e) {

            // ✅ Handle bad tokens gracefully: return 401 Unauthorized
            System.err.println("JWT Error: " + e.getMessage());

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Invalid or expired JWT token.\"}");
            return;  // 🔁 Stop the filter chain here
        }

        filterChain.doFilter(request, response);
    }
}