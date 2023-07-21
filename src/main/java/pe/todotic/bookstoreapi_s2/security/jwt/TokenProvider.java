package pe.todotic.bookstoreapi_s2.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TokenProvider {

    private final static String SECRET = "70fe6fdc416af010dfda93b995a95a73ebb02394cP5v3EFq8BDf2pO5lalac+uwI5Q70FE6FDC416AF010DFDA93B995A95A73EBB02394";
    private final JwtParser jwtParser;
    private final Key key;

    public TokenProvider () {
        byte[] secretBytes = Decoders.BASE64.decode(SECRET);
        key = Keys.hmacShaKeyFor(secretBytes);
        jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
    }


    public String createToken (Authentication authentication) {
        Long duration = 3600 * 24 * 30L * 1000;
        Long now = new Date().getTime();
        Date expirationDate = new Date(now + duration);
        String role = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts
                .builder()
                .setSubject(authentication.getName())
                .claim("auth", role)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(expirationDate)
                .compact();
    }


    public Authentication getAuthentication (String token) {
        Claims claims = jwtParser.parseClaimsJws(token).getBody();

        List<SimpleGrantedAuthority> authorities = Arrays
                .stream(claims.get("auth").toString().split(","))
                .filter(auth -> !auth.trim().isEmpty())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }


    public boolean validate (String token) {
        try {
            jwtParser.parseClaimsJws(token);
            return true;
        } catch (JwtException ex) {
            log.error("Token validation error {}", ex.getMessage());
        }
        return false;
    }


}
