package com.example.zajecia7doktorki.config.security.jwt;

import com.example.zajecia7doktorki.domain.Customer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

import static com.example.zajecia7doktorki.constants.ConstantsUtil.ROLE;

@Service
public class JwtService {

    @Value(value = "${jwtSigningKey}")
    private String jwtSigningKey;
    private static final long JWT_EXPIRE_DURATION = 24 * 60 * 60 * 1000;


    public String generateToken(Customer customer) {
        return Jwts.builder()
                .setSubject(String.format("%s,%s", customer.getId(), customer.getLogin()))
                .claim(ROLE, customer.getRole())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRE_DURATION))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String jsonWebToken, UserDetails userDetails) {
        String[] jwtSubject = extractUserLogin(jsonWebToken).split(",");
        final String role = (String) extractClaim(jsonWebToken, claims -> claims.get(ROLE));
        return jwtSubject[1].equals(userDetails.getUsername())
                && !isTokenExpired(jsonWebToken)
                && role.equals(userDetails.getAuthorities().iterator().next().getAuthority());
    }

    private boolean isTokenExpired(String jsonWebToken) {
        return extractExpiration(jsonWebToken).before(new Date());
    }

    private Date extractExpiration(String jsonWebToken) {
        return extractClaim(jsonWebToken, Claims::getExpiration);
    }

    public String extractUserLogin(String jsonWebToken) {
        return extractClaim(jsonWebToken, Claims::getSubject);
    }

    private <T> T extractClaim(String jsonWebToken, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(jsonWebToken);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String jsonWebToken) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(jsonWebToken)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
