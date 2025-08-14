package com.backend.immilog.user.application.services.query;

import com.backend.immilog.shared.infrastructure.DataRepository;
import org.springframework.stereotype.Service;

@Service
public class TokenQueryService {
    private final DataRepository dataRepository;

    public TokenQueryService(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    public String getValueByKey(String key) {
        return dataRepository.findByKey(key);
    }
}
