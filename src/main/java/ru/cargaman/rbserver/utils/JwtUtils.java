package ru.cargaman.rbserver.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Component
public class JwtUtils {
    public static final String SECRET = "6c74862eeb33a1cc0c287c9469c7efb7ad764f5e0e8cf0cab5ef2cf1522eb045fc57b61a4454c8dc668abd7a30f484599d4faebe795f5370d0ea328243c01632f5aa27a98ecfd853cdb9a3b64fec36521ca186e34775a07a3858bfb99c7c87c80df7cbc368db1bd00d40d646aa43837412d5bc2ab52b3056a43c68fd7c469fffab117a5ef25b5036177bf5911c72a67b7286c07ade17c6664da3d3d21374726e7742f4628720ca484f413db8f50289126f938f2f575b4d64ee9417a5e3811c111085315382587f86dc2be3d459201e12db658bae55656447b9dc3629dc81b534aa4b4bca7a3f381e808df504d951953903c58c1a4573925e63a7e93ad74a605e";

    private Key getSignKey(){
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String createToken(Map<String, Object> claims, String username){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }

    public String generateToken(String username){
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    public Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    public Boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return Objects.equals(username, userDetails.getUsername()) && isTokenExpired(token);
    }

    public Boolean isTokenValid(String token){
        try{
            Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
