package com.backend.immilog.company.domain.service;

import com.backend.immilog.company.application.dto.CompanyRegisterCommand;
import com.backend.immilog.company.application.mapper.CompanyMapper;
import com.backend.immilog.company.domain.model.Company;
import com.backend.immilog.company.domain.repository.CompanyRepository;
import com.backend.immilog.company.exception.CompanyErrorCode;
import com.backend.immilog.company.exception.CompanyException;
import org.springframework.stereotype.Service;

@Service
public class CompanyRegistrationService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    public CompanyRegistrationService(
            CompanyRepository companyRepository,
            CompanyMapper companyMapper
    ) {
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
    }

    public Company registerNewCompany(
            Long userSeq,
            CompanyRegisterCommand command
    ) {
        validateUserNotAlreadyManager(userSeq);
        validateCompanyNameUniqueness(command.name());

        return companyMapper.toNewCompany(userSeq, command);
    }

    private void validateUserNotAlreadyManager(Long userSeq) {
        Company existingCompany = companyRepository.findByManagerUserSeq(userSeq).orElse(null);
        if (existingCompany != null && !existingCompany.isEmpty()) {
            throw new CompanyException(CompanyErrorCode.USER_ALREADY_MANAGER);
        }
    }

    private void validateCompanyNameUniqueness(String companyName) {
        if (companyRepository.existsByName(companyName)) {
            throw new CompanyException(CompanyErrorCode.COMPANY_NAME_ALREADY_EXISTS);
        }
    }
}