package com.education.flashEng.security;

import com.education.flashEng.exception.JwtExceptionHandler;
import com.education.flashEng.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailService customUserDetailService;

    @Autowired
    private SecurityPermitAllHttp securityPermitAllHttp;

    @Autowired
    private JwtExceptionHandler jwtExceptionHandler;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        String requestMethod = request.getMethod();
        String token = getJwtFromRequest(request);

        boolean isPermitAll = securityPermitAllHttp.getPermitAllEndpoints().entrySet().stream()
                .anyMatch(entry -> {
                    String endpoint = entry.getKey();
                    Set<String> methods = entry.getValue();
                    return antPathMatcher.match(endpoint, requestURI) && methods.contains(requestMethod);
                });

        if (isPermitAll) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            if (token == null) {
                jwtExceptionHandler.handleJwtException(response, HttpStatus.UNAUTHORIZED, "Token is missing");
                return;
            }

            jwtUtil.verifyToken(token);
            String username = jwtUtil.getUsernameFromToken(token);
            UserDetails userDetails = customUserDetailService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            jwtExceptionHandler.handleJwtException(response, HttpStatus.UNAUTHORIZED, "Token Expired");
        } catch (MalformedJwtException e) {
            jwtExceptionHandler.handleJwtException(response, HttpStatus.BAD_REQUEST, "Token is not valid");
        } catch (Exception e) {
            jwtExceptionHandler.handleJwtException(response, HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error: " + e.getMessage());
        }
    }



    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}