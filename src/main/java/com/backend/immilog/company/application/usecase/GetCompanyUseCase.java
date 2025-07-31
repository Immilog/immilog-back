package com.backend.immilog.company.application.usecase;

import com.backend.immilog.company.application.dto.CompanyFetchResult;
import com.backend.immilog.company.application.service.CompanyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

public interface GetCompanyUseCase {
    CompanyFetchResult getCompany(String userId);

    @Service
    class CompanyFetcher implements GetCompanyUseCase {
        private final CompanyService companyService;

        public CompanyFetcher(CompanyService companyService) {
            this.companyService = companyService;
        }

        @Override
        @Transactional(readOnly = true)
        public CompanyFetchResult getCompany(String userId) {
            return companyService.getCompanyByManagerUserId(userId);
        }
    }
}