package com.education.flashEng.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secretKey}")
    private String secretKeyString;

    private final int jwtExpirationMs = 86400000; // 1 day

    public String generateToken(Authentication authentication) {
        User userPrincipal = (User) authentication.getPrincipal();
        SecretKey secretKey= Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKeyString)) ;
        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(secretKey)
                .compact();
    }

    public boolean verifyToken(String token){
        SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKeyString)) ;
        Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
        return true;
    }

    public String getUsernameFromToken(String token){
        SecretKey secretKey= Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKeyString)) ;
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }


}
