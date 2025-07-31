package com.backend.immilog.company.application.service;

import com.backend.immilog.company.domain.model.Company;
import com.backend.immilog.company.domain.repository.CompanyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompanyQueryService {
    private final CompanyRepository companyRepository;

    public CompanyQueryService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Transactional(readOnly = true)
    public Company getByCompanyManagerUserId(String userId) {
        return companyRepository.getByCompanyManagerUserId(userId).orElse(Company.createEmpty());
    }

    @Transactional(readOnly = true)
    public Company getById(String companyId) {
        return companyRepository.findById(companyId).orElse(Company.createEmpty());
    }

    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return companyRepository.existsByName(name);
    }
}
