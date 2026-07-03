package com.automation.framework.utilities;

import com.automation.framework.exceptions.FrameworkException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Encoding and token helpers for handling secrets and API authentication.
 * Application-agnostic; no domain-specific (payment/card) logic.
 */
public final class EncryptionUtils {

    private EncryptionUtils() {
    }

    public static String base64Encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    public static String base64Decode(String value) {
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }

    /** HMAC-SHA256 hex digest, commonly used for signed API requests. */
    public static String hmacSha256(String message, String secret) {
        return hmac("HmacSHA256", message, secret);
    }

    public static String hmacSha512(String message, String secret) {
        return hmac("HmacSHA512", message, secret);
    }

    private static String hmac(String algorithm, String message, String secret) {
        try {
            Mac mac = Mac.getInstance(algorithm);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), algorithm));
            byte[] bytes = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            throw new FrameworkException("Failed to compute " + algorithm, e);
        }
    }
}
