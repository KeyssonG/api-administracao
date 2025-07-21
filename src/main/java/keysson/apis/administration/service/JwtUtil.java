package keysson.apis.administration.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Component
@Getter
public class JwtUtil {

    @Value("C6slIxtVM5y1mBrCphrqygYNVoN7t5V/03NVfJddayQ=")
    private String secretKey;

    private final long EXPIRATION_TIME = MILLISECONDS.toMillis(86400000);
    private final Key key;

    public JwtUtil(@Value("C6slIxtVM5y1mBrCphrqygYNVoN7t5V/03NVfJddayQ=") String secretKey) {
        this.secretKey = secretKey;
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public Date getExpirationDate() {
        return new Date(System.currentTimeMillis() + EXPIRATION_TIME);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Integer extractUserId(String token) {
        return extractAllClaims(token).get("id", Integer.class);
    }

    public Integer extractCompanyId(String token) {
        return extractAllClaims(token).get("companyId", Integer.class);
    }

    public UUID extractConsumerId(String token) {
        String consumerIdStr = extractAllClaims(token).get("consumerId", String.class);
        return UUID.fromString(consumerIdStr);
    }

    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.err.println("Token expirado em: " + e.getClaims().getExpiration());
        } catch (MalformedJwtException e) {
            System.err.println("Token malformado: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erro inesperado: " + e.getClass() + " - " + e.getMessage());
        }
        return false;
    }
}

