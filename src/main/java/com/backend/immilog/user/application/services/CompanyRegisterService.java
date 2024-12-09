package com.backend.immilog.user.application.services;

import com.backend.immilog.user.application.command.CompanyRegisterCommand;
import com.backend.immilog.user.domain.model.Company;
import com.backend.immilog.user.domain.enums.UserCountry;
import com.backend.immilog.user.domain.repositories.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
@Service
public class CompanyRegisterService {
    private final CompanyRepository companyRepository;

    @Transactional
    public void registerOrEditCompany(
            Long userSeq,
            CompanyRegisterCommand command
    ) {
        AtomicBoolean isExistingCompany = new AtomicBoolean(false);
        checkIfExistingAndProceedWithUpdate(userSeq, command, isExistingCompany);
        if (!isExistingCompany.get()) {
            createCompany(userSeq, command);
        }
    }

    private void checkIfExistingAndProceedWithUpdate(
            Long userSeq,
            CompanyRegisterCommand request,
            AtomicBoolean isExistingCompany
    ) {
        companyRepository.getByCompanyManagerUserSeq(userSeq)
                .ifPresent(company -> {
                    updateCompany(company, request);
                    isExistingCompany.set(true);
                });
    }

    private void createCompany(
            Long userSeq,
            CompanyRegisterCommand request
    ) {
        companyRepository.saveEntity(Company.of(userSeq, request));
    }

    private void updateCompany(
            Company company,
            CompanyRegisterCommand request
    ) {
        company = updateCompanyAddressIfItsNotNull(company, request.companyAddress());
        company = updateCompanyCountryIfItsNotNull(company, request.companyCountry());
        company = updateCompanyEmailIfItsNotNull(company, request.companyEmail());
        company = updateCompanyHomepageIfItsNotNull(company, request.companyHomepage());
        company = updateCompanyLogoIfItsNotNull(company, request.companyLogo());
        company = updateCompanyPhoneIfItsNotNull(company, request.companyPhone());
        company = updateCompanyCountryNameIfItsNotNull(company, request.companyName());
        company = updateCompanyRegionIfItsNotNull(company, request.companyRegion());
        company = updateIndustryIfItsNotNull(company, request);
        companyRepository.saveEntity(company);
    }

    private static Company updateIndustryIfItsNotNull(
            Company company,
            CompanyRegisterCommand request
    ) {
        return request.industry() != null ? company.copyWithNewIndustry(request.industry()) : company;
    }

    private static Company updateCompanyCountryNameIfItsNotNull(
            Company company,
            String companyName
    ) {
        return companyName != null ? company.copyWithNewCompanyName(companyName) : company;
    }

    private static Company updateCompanyPhoneIfItsNotNull(
            Company company,
            String companyPhone
    ) {
        return companyPhone != null ? company.copyWithNewCompanyPhone(companyPhone) : company;
    }

    private static Company updateCompanyLogoIfItsNotNull(
            Company company,
            String companyLogo
    ) {
        return companyLogo != null ? company.copyWithNewCompanyLogo(companyLogo) : company;
    }

    private static Company updateCompanyHomepageIfItsNotNull(
            Company company,
            String companyHomepage
    ) {
        return companyHomepage != null ? company.copyWithNewCompanyHomepage(companyHomepage) : company;
    }

    private static Company updateCompanyEmailIfItsNotNull(
            Company company,
            String companyEmail
    ) {
        return companyEmail != null ? company.copyWithNewCompanyEmail(companyEmail) : company;
    }

    private static Company updateCompanyCountryIfItsNotNull(
            Company company,
            UserCountry country
    ) {
        return country != null ? company.copyWithNewCompanyCountry(country) : company;
    }

    private static Company updateCompanyAddressIfItsNotNull(
            Company company,
            String address
    ) {
        return address != null ? company.copyWithNewCompanyAddress(address) : company;
    }

    private static Company updateCompanyRegionIfItsNotNull(
            Company company,
            String region
    ) {
        return region != null ? company.copyWithNewCompanyRegion(region) : company;
    }
}