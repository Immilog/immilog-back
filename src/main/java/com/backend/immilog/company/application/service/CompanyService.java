package com.backend.immilog.company.application.service;

import com.backend.immilog.company.application.dto.CompanyFetchResult;
import com.backend.immilog.company.application.dto.CompanyRegisterCommand;
import com.backend.immilog.company.application.mapper.CompanyMapper;
import com.backend.immilog.company.domain.model.Company;
import com.backend.immilog.company.domain.model.CompanyId;
import com.backend.immilog.company.domain.service.CompanyRegistrationService;
import com.backend.immilog.company.domain.service.CompanyValidationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class CompanyService {

    private final CompanyQueryService companyQueryService;
    private final CompanyCommandService companyCommandService;
    private final CompanyRegistrationService companyRegistrationService;
    private final CompanyValidationService companyValidationService;
    private final CompanyMapper companyMapper;

    public CompanyService(
            CompanyQueryService companyQueryService,
            CompanyCommandService companyCommandService,
            CompanyRegistrationService companyRegistrationService,
            CompanyValidationService companyValidationService,
            CompanyMapper companyMapper
    ) {
        this.companyQueryService = companyQueryService;
        this.companyCommandService = companyCommandService;
        this.companyRegistrationService = companyRegistrationService;
        this.companyValidationService = companyValidationService;
        this.companyMapper = companyMapper;
    }

    public CompanyId registerCompany(
            Long userSeq,
            CompanyRegisterCommand command
    ) {
        Company newCompany = companyRegistrationService.registerNewCompany(userSeq, command);
        Company savedCompany = companyCommandService.save(newCompany);
        return CompanyId.of(savedCompany.seq());
    }

    public void updateCompany(
            Long userSeq,
            CompanyRegisterCommand command
    ) {
        Company existingCompany = companyQueryService.getByCompanyManagerUserSeq(userSeq);
        companyValidationService.validateCompanyExists(existingCompany);

        Company updatedCompany = companyMapper.updateCompany(existingCompany, command);
        companyCommandService.save(updatedCompany);
    }

    @Transactional(readOnly = true)
    public CompanyFetchResult getCompanyByManagerUserSeq(Long userSeq) {
        Company company = companyQueryService.getByCompanyManagerUserSeq(userSeq);
        return Optional.of(CompanyFetchResult.from(company)).orElse(CompanyFetchResult.createEmpty());
    }

    @Transactional(readOnly = true)
    public Company getCompanyById(CompanyId companyId) {
        return companyQueryService.getById(companyId.value());
    }

    public void deleteCompany(Long userSeq) {
        Company company = companyQueryService.getByCompanyManagerUserSeq(userSeq);
        companyValidationService.validateCompanyExists(company);
        companyCommandService.delete(company);
    }
}