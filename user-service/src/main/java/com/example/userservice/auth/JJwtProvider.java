package com.example.userservice.auth;

import com.example.userservice.dto.response.LoginResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JJwtProvider implements JwtProvider {

  private final SecretKey secretKey;
  private final Long accessTokenValidityInMilliseconds;

  public JJwtProvider(
      @Value("${jwt.secret}") String secret,
      @Value("${jwt.access-token-validity}") long accessTokenValidityInMilliseconds
  ) {
    this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    this.accessTokenValidityInMilliseconds = accessTokenValidityInMilliseconds;
  }

  @Override
  public LoginResponse generateToken(final Long userId) {
    Date now = new Date();
    Date validity = new Date(now.getTime() + accessTokenValidityInMilliseconds);

    String accessToken = Jwts.builder()
        .setSubject(userId.toString())
        .setIssuedAt(now)
        .setExpiration(validity)
        .signWith(secretKey, SignatureAlgorithm.HS256)
        .compact();

    return LoginResponse.of(accessToken, validity.getTime());
  }

  @Override
  public Long getUserIdFromToken(String token) {
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(secretKey)
        .build()
        .parseClaimsJws(token)
        .getBody();

    return Long.valueOf(claims.getSubject());
  }

  @Override
  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder()
          .setSigningKey(secretKey)
          .build()
          .parseClaimsJws(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  public boolean isTokenExpired(String token) {
    try {
      Claims claims = Jwts.parserBuilder()
          .setSigningKey(secretKey)
          .build()
          .parseClaimsJws(token)
          .getBody();

      return claims.getExpiration().before(new Date());
    } catch (Exception e) {
      return true;
    }
  }
}
