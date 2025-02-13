package it.epicode.security.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long jwtExpirationInMs;

    // Estrae il nome utente dal token JWT
    public String getUsernameFromToken(String token) {
        try {
            return getClaimFromToken(token, Claims::getSubject);
        } catch (Exception e) {
            System.err.println("⚠️ Errore nel parsing del token JWT: " + e.getMessage());
            return null;
        }
    }

    // Estrae la data di scadenza dal token JWT
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    // Estrae un claim specifico dal token JWT
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    // Estrae tutti i claims dal token JWT
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    // Verifica se il token JWT è scaduto
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    // Genera un token JWT per l'utente, includendo i ruoli e hotelId se applicabile
    public String generateToken(UserDetails userDetails) {
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Long userId = null;
        Long hotelId = null;

        if (userDetails instanceof CustomUserDetails) {
            CustomUserDetails customUser = (CustomUserDetails) userDetails;
            userId = customUser.getId();

            // ✅ Se l'utente è un hotel owner, aggiungiamo `hotelId`
            if (roles.contains("ROLE_HOTEL") && customUser.getHotelId() != null) {
                hotelId = customUser.getHotelId();
            }
        }

        String token = Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("id", userId)
                .claim("hotelId", hotelId) // ✅ Aggiunto `hotelId` nel token
                .claim("roles", roles)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                .signWith(Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret)), SignatureAlgorithm.HS512)


                .compact();
        System.out.println(token);
        return token;

    }

    // Estrae i ruoli dal token JWT
    public List<String> getRolesFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.get("roles", List.class);
    }

    // Valida il token JWT
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
