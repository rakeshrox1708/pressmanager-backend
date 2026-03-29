package com.newspaper.System.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    private final long ACCESS_EXP = 15 * 60 * 1000;   // 15 min
    private final long REFRESH_EXP = 7 * 24 * 60 * 60 * 1000; // 7 days

    public String generateAccessToken(int id, String role) {
        return Jwts.builder()
                .setSubject(String.valueOf(id))
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_EXP))
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(int id) {
        return Jwts.builder()
                .setSubject(String.valueOf(id))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXP))
                .signWith(key)
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody();
    }

    public int extractId(String token) {
        return Integer.parseInt(extractAllClaims(token).getSubject());
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    public Date extractExpiry(String token) {
        return extractAllClaims(token).getExpiration();
    }
}

//with Phone number as userid
//@Component
//public class JwtUtil {
//
//    private final Key key = Keys.hmacShaKeyFor(
//            "this_is_a_very_secure_secret_key_32bytes!".getBytes()
//    );
//
//    public String generateToken(String phone, String role) {
//
//        return Jwts.builder()
//                .setSubject(phone)
//                .claim("role", role)
//                .setIssuedAt(new Date())
//                .setExpiration(
//                        new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)
//                )
//                .signWith(key)
//                .compact();
//    }
//
//    public Claims extractAllClaims(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(key)
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//    }
//
//    public String extractPhone(String token) {
//        return extractAllClaims(token).getSubject();
//    }
//
//    public String extractRole(String token) {
//        return extractAllClaims(token).get("role", String.class);
//    }
//}
