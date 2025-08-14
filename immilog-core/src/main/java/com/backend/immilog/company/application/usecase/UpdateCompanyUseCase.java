package com.backend.immilog.company.application.usecase;

import com.backend.immilog.company.application.dto.CompanyRegisterCommand;
import com.backend.immilog.company.application.service.CompanyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

public interface UpdateCompanyUseCase {
    void updateCompany(
            String userId,
            CompanyRegisterCommand command
    );

    @Service
    class CompanyUpdater implements UpdateCompanyUseCase {
        private final CompanyService companyService;

        public CompanyUpdater(CompanyService companyService) {
            this.companyService = companyService;
        }

        @Override
        @Transactional
        public void updateCompany(
                String userId,
                CompanyRegisterCommand command
        ) {
            companyService.updateCompany(userId, command);
        }
    }
}