package com.backend.immilog.shared.config.web;

import com.backend.immilog.shared.enums.Country;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class CountryDeserializer extends JsonDeserializer<Country> {
    
    @Override
    public Country deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();
        
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        
        // 먼저 enum 이름으로 시도
        try {
            return Country.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            // enum 이름이 아니면 한국어 이름으로 시도
            Country country = Country.getCountryByKoreanName(value);
            if (country != null) {
                return country;
            }
            
            // 둘 다 실패하면 예외 발생
            throw new IllegalArgumentException("Unknown country: " + value + 
                ". Valid values are: enum names (SOUTH_KOREA, JAPAN, etc.) or Korean names (대한민국, 일본, etc.)");
        }
    }
}