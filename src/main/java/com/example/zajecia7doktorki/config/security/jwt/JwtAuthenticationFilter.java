package com.example.zajecia7doktorki.config.security.jwt;

import com.example.zajecia7doktorki.domain.Customer;
import com.example.zajecia7doktorki.model.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

import static com.example.zajecia7doktorki.constants.ConstantsUtil.*;

@Service
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authenticationHeader = request.getHeader("Authorization");
        final String jsonWebToken;
        final String userLogin;
        if (Objects.isNull(authenticationHeader) || !authenticationHeader.startsWith(BEARER)) {
            filterChain.doFilter(request, response);
            return;
        }
        jsonWebToken = authenticationHeader.substring(7);
        try {
            userLogin = jwtService.extractUserLogin(jsonWebToken);
            if (Objects.nonNull(userLogin) && Objects.isNull(SecurityContextHolder.getContext().getAuthentication())) {
                if (jwtService.isTokenValid(jsonWebToken, getUserDetails(jsonWebToken))) {
                    setAuthenticationContext(jsonWebToken, request);
                }
            }
        } catch (ExpiredJwtException e) {
            request.setAttribute(EXPIRED, e.getMessage());
        }
        filterChain.doFilter(request, response);
    }

    private void setAuthenticationContext(String token, HttpServletRequest request) {
        UserDetails userDetails = getUserDetails(token);
        UsernamePasswordAuthenticationToken
                authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private UserDetails getUserDetails(String token) {
        Customer userDetails = new Customer();
        Claims claims = jwtService.extractAllClaims(token);
        String[] jwtSubject = jwtService.extractUserLogin(token).split(",");
        String role = (String) claims.get(ROLE);
        role = role.replace("Role.", "");
        userDetails.setId(Long.valueOf((jwtSubject[0])));
        userDetails.setLogin(jwtSubject[1]);
        userDetails.setRole(Role.valueOf(role));
        return userDetails;
    }
}
