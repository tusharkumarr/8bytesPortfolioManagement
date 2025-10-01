package com.portfolio.management.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.management.model.GenericResponse;
import com.portfolio.management.model.User;
import com.portfolio.management.repository.UserRepository;
import com.portfolio.management.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

public class JwtFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/auth/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                String phoneNumber = JwtUtil.validateTokenAndGetPhone(token);

                Optional<User> userOpt = userRepository.findByPhoneNumber(phoneNumber);
                if (userOpt.isEmpty()) {
                    sendError(response, "User not found for token");
                    return;
                }

                User user = userOpt.get();

                request.setAttribute("currentUser", user);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                phoneNumber, null, Collections.emptyList()
                        );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                sendError(response, "Invalid or expired token");
                return;
            }
        } else {
            sendError(response, "JWT token not provided");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void sendError(HttpServletResponse response, String message) throws IOException {
        GenericResponse<Object> errorResponse = GenericResponse.failure(message);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
