package com.testpetize.todolist.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    @Value("${jwt.acess.expirationMs}")
    private long jwtAcessExpirationInMs;

    private static final String TOKEN_PREFIX = "Bearer ";

    public String generateAcessToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Instant now = Instant.now();
        Instant expiry = now.plusMillis(jwtAcessExpirationInMs);

        List<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("finely-finance")
                .issuedAt(now)
                .expiresAt(expiry)
                .subject(userDetails.getUsername())
                .claim("authorities", authorities)
                .claim("type", "access_token")
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public boolean isValidAcessToken(String token, UserDetails userDetails) {
        try {
            var decodedJwt = jwtDecoder.decode(token);

            String username = decodedJwt.getSubject();
            String type = decodedJwt.getClaim("type");

            return username.equals(userDetails.getUsername()) && "access_token".equals(type);
        } catch (JwtException e) {
            return false;
        }
    }

    public String extractUsernameFromToken(String token) {
        return jwtDecoder.decode(token.replace(TOKEN_PREFIX, ""))
                .getSubject();
    }
}
