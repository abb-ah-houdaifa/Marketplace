package com.marketplace.marketplace.utils.encryption;

import com.marketplace.marketplace.exception.EncryptionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@Slf4j
public class Encryptor {
    @Value("${spring.application.security.end-to-end.key}")
    private String key;
    @Value("${spring.application.security.end-to-end.iVector}")
    private String initVector; //initialization vector used for encryption
    @Value("${spring.application.security.end-to-end.algorithm}")
    private String encryptionAlgo;

    public String encryptData(String input) {
        try {
            IvParameterSpec ivParameter = new IvParameterSpec(
                    initVector.getBytes(StandardCharsets.UTF_8)
            );

            SecretKeySpec secretKey = new SecretKeySpec(
                    key.getBytes(StandardCharsets.UTF_8),
                    "AES"
            );

            Cipher cipher = Cipher.getInstance(encryptionAlgo);
            cipher.init(
                    Cipher.ENCRYPT_MODE,
                    secretKey,
                    ivParameter
            );

            byte[] encryptedValue = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedValue);
        } catch (Exception ex) {
            log.error("Error while encrypting data : {}", ex.getMessage());
            throw new EncryptionException("Error while encrypting data");
        }

//
    }

    public String decryptData(String input) {
        try {
            IvParameterSpec ivParameter = new IvParameterSpec(
                    initVector.getBytes(StandardCharsets.UTF_8)
            );

            SecretKeySpec secretKey = new SecretKeySpec(
                    key.getBytes(),
                    "AES"
            );

            Cipher cipher = Cipher.getInstance(encryptionAlgo);
            cipher.init(
                    Cipher.DECRYPT_MODE,
                    secretKey,
                    ivParameter
            );

            byte [] encryptedData = Base64.getDecoder().decode(input);
            byte [] originalValue = cipher.doFinal(encryptedData);
            return new String(originalValue, StandardCharsets.UTF_8);
        } catch (Exception exception) {
            log.error("Error while decrypting data : {}", exception.getMessage());
            throw new EncryptionException("Error while decrypting data");
        }
    }
}
