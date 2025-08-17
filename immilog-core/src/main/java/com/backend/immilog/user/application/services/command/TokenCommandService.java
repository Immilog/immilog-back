package com.backend.immilog.user.application.services.command;

import com.backend.immilog.shared.infrastructure.DataRepository;
import org.springframework.stereotype.Service;

@Service
public class TokenCommandService {
    private final DataRepository dataRepository;

    public TokenCommandService(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    public void saveKeyAndValue(
            String key,
            String value,
            int expireTime
    ) {
        dataRepository.save(key, value, expireTime);
    }

    public void deleteValueByKey(String key) {
        dataRepository.deleteByKey(key);
    }

    public String getValue(String key) {
        return dataRepository.findByKey(key);
    }

    public void deleteKey(String key) {
        dataRepository.deleteByKey(key);
    }
}
