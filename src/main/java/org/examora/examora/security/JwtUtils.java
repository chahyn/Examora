package org.examora.examora.security;

import org.springframework.stereotype.Component;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtils {

    private final String SECRET = "ziko1234examorasecretkey1234567890";
    private final long EXPIRATION = 86400000L; // 1 day in ms

    // Generate token: username.expiry.signature
    public String generateToken(String username) {
        String payload = username + "." + (System.currentTimeMillis() + EXPIRATION);
        String signature = sign(payload);
        return Base64.getEncoder().encodeToString(payload.getBytes()) + "." + signature;
    }

    public String getUsernameFromToken(String token) {
        String payload = decodePayload(token);
        return payload.split("\\.")[0];
    }

    public boolean validateToken(String token) {
        try {
            String payload = decodePayload(token);
            String[] parts = payload.split("\\.");
            String username = parts[0];
            long expiry = Long.parseLong(parts[1]);

            // Check expiry
            if (new Date().getTime() > expiry) return false;

            // Check signature
            String expectedSig = sign(payload);
            String actualSig = token.split("\\.")[1];
            return expectedSig.equals(actualSig);
        } catch (Exception e) {
            return false;
        }
    }

    private String decodePayload(String token) {
        String encodedPayload = token.split("\\.")[0];
        return new String(Base64.getDecoder().decode(encodedPayload));
    }

    private String sign(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(SECRET.getBytes(), "HmacSHA256"));
            return Base64.getEncoder().encodeToString(mac.doFinal(data.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException("Error signing token", e);
        }
    }
}