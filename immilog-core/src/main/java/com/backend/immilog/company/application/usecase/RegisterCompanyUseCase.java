package com.backend.immilog.company.application.usecase;

import com.backend.immilog.company.application.dto.CompanyRegisterCommand;
import com.backend.immilog.company.application.service.CompanyService;
import com.backend.immilog.company.domain.model.CompanyId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

public interface RegisterCompanyUseCase {
    CompanyId registerCompany(
            String userId,
            CompanyRegisterCommand command
    );

    @Service
    class CompanyRegistrar implements RegisterCompanyUseCase {
        private final CompanyService companyService;

        public CompanyRegistrar(CompanyService companyService) {
            this.companyService = companyService;
        }

        @Override
        @Transactional
        public CompanyId registerCompany(
                String userId,
                CompanyRegisterCommand command
        ) {
            return companyService.registerCompany(userId, command);
        }
    }
}