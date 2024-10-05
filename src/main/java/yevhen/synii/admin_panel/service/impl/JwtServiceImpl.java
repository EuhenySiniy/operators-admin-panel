package yevhen.synii.admin_panel.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import yevhen.synii.admin_panel.entity.TokenEntity;
import yevhen.synii.admin_panel.entity.enums.UserRole;
import yevhen.synii.admin_panel.repository.TokensRepo;

import java.security.Key;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl {
    private final TokensRepo tokensRepo;
    private static final String SECRET_KEY = "G0DJtiwbC8rQ4yhiNhRKEsndr+OW8fAua1jxjkm8YmhjqpIwtxKEXOadH6FuqLCr11/I2gt+7SthOlcrXPAeB+GCnfCLpY9SYRIIdWyM33bm2VMg+ubpszNiUcou5WCy6tKxFBeIJsrkkPquWuWBRJ8Kmmopr+0j6EMjJJl2K4unNblAu058tke56aV+szEipB0pVV3tm1MKNi3Xld5K9FZpVU3fZeOOvmOM+d9dn5SDbzKdfvPffz9QPaAcF2d4CWujLYWrFph2yM4T20gn6cAax6GD9uOQdZNxizuXtFj5+6fvBSnrgkdoUYqPxXOl4ZHV6Pl3P6YwQ8rJb9J1mZZ9/RhHldlywAXjwfyqj5I=";
    private static final Long ONE_HOUR = 3600000L;
    private static final Long ONE_DAY = 86400000L;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Long extractId(String token) {
        return extractClaim(token, claims -> claims.get("id", Long.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolved) {
        final Claims claims= extractAllClaims(token);
        return claimsResolved.apply(claims);
    }

    public String generateAccessToken(UserDetails user, Long id, UserRole role) {
        return generateAccessToken(new HashMap<>(), user, id, role);
    }

    public String generateAccessToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            Long id,
            UserRole role
    ) {
        extraClaims.put("id", id);
        extraClaims.put("role", role);
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + ONE_HOUR))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(UserDetails user) {
        return generateRefreshToken(new HashMap<>(), user);
    }

    public String generateRefreshToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + ONE_DAY * 7))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails user) {
        final String username = extractUsername(token);
        return (username.equals(user.getUsername())) && !isTokenExpired(token) && !isTokenSpoiled(token);
    }

    public Timestamp tokenExpiration(String token) {
        return new Timestamp(extractExpiration(token).getTime());
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

    private boolean isTokenSpoiled(String token) {
        TokenEntity accessTokenEntity = tokensRepo.getTokenEntityByAccessToken(token);
        TokenEntity refreshTokenEntity = tokensRepo.getTokenEntityByRefreshToken(token);
        if(accessTokenEntity != null) {
            return accessTokenEntity.isExpired();
        } else if(refreshTokenEntity != null) {
            return refreshTokenEntity.isExpired();
        }
        return true;
    }
}
