package com.backend.immilog.user.application.services.command;

import com.backend.immilog.shared.infrastructure.DataRepository;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenCommandService {
    private final DataRepository dataRepository;

    public RefreshTokenCommandService(DataRepository dataRepository) {
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
}
