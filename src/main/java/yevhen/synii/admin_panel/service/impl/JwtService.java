package yevhen.synii.admin_panel.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    private static final String SECRET_KEY = "G0DJtiwbC8rQ4yhiNhRKEsndr+OW8fAua1jxjkm8YmhjqpIwtxKEXOadH6FuqLCr11/I2gt+7SthOlcrXPAeB+GCnfCLpY9SYRIIdWyM33bm2VMg+ubpszNiUcou5WCy6tKxFBeIJsrkkPquWuWBRJ8Kmmopr+0j6EMjJJl2K4unNblAu058tke56aV+szEipB0pVV3tm1MKNi3Xld5K9FZpVU3fZeOOvmOM+d9dn5SDbzKdfvPffz9QPaAcF2d4CWujLYWrFph2yM4T20gn6cAax6GD9uOQdZNxizuXtFj5+6fvBSnrgkdoUYqPxXOl4ZHV6Pl3P6YwQ8rJb9J1mZZ9/RhHldlywAXjwfyqj5I=";
    private static final Long ONE_HOUR = 3600000L;
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolved) {
        final Claims claims= extractAllClaims(token);
        return claimsResolved.apply(claims);
    }

    public String generateToken(UserDetails user) {
        return generateToken(new HashMap<>(), user);
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + ONE_HOUR * 24))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails user) {
        final String username = extractUsername(token);
        return (username.equals(user.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
       byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
       return Keys.hmacShaKeyFor(keyBytes);
    }
}
