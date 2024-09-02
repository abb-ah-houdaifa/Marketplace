package com.marketplace.marketplace.utils.encryption;

import jakarta.persistence.AttributeConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AttributeEncryptor implements AttributeConverter<String, String> {
    private final Encryptor encryptor;

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return encryptor.encryptData(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return encryptor.decryptData(dbData);
    }
}
