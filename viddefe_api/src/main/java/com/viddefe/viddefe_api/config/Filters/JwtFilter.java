package com.viddefe.viddefe_api.config.Filters;

import com.viddefe.viddefe_api.config.Components.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.http.HttpMethod;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private JwtUtil jwtUtil;
    @Value("${api.prefix}")
    private String apiPrefix;

    private static final List<String> PUBLIC_PATHS = List.of(
            "/error",
            "/usuarios",
            "/people"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        System.out.println(apiPrefix);

        String path = request.getRequestURI();
        System.out.println("iniciamos peticion para: "+path);

        // Permitir solicitudes OPTIONS para CORS
        if (request.getMethod().equals(HttpMethod.OPTIONS.name())) {
            return true;
        }

        if(!path.startsWith(apiPrefix)) {
            System.out.println("No inicia con api prefix" + apiPrefix + "!=" + path);
            return false;
        }
        // Verificar rutas públicas
        for (String publicPath : PUBLIC_PATHS) {
            System.out.println("hey im here" + apiPrefix + publicPath);

            if (path.startsWith(apiPrefix + publicPath)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, jakarta.servlet.ServletException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (jwtUtil.isTokenValid(token)) {
                Claims claims = jwtUtil.getClaims(token);
                String email = claims.getSubject();
                String role = claims.get("role", String.class);

                var authToken = new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + role))
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}