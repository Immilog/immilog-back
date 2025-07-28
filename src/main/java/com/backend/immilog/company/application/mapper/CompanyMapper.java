package com.backend.immilog.company.application.mapper;

import com.backend.immilog.company.application.dto.CompanyRegisterCommand;
import com.backend.immilog.company.domain.model.Company;
import org.springframework.stereotype.Component;

@Component
public class CompanyMapper {
    public Company toNewCompany(
            Long userSeq,
            CompanyRegisterCommand cmd
    ) {
        return Company.createEmpty()
                .manager(cmd.country(), cmd.region(), userSeq)
                .companyData(
                        cmd.industry(),
                        cmd.name(),
                        cmd.email(),
                        cmd.phone(),
                        cmd.address(),
                        cmd.homepage(),
                        cmd.logo()
                );
    }

    public Company updateCompany(
            Company existing,
            CompanyRegisterCommand cmd
    ) {
        return existing.updateAddress(cmd.address())
                .updateCountry(cmd.country())
                .updateEmail(cmd.email())
                .updateHomepage(cmd.homepage())
                .updateLogo(cmd.logo())
                .updatePhone(cmd.phone())
                .updateName(cmd.name())
                .updateRegion(cmd.region())
                .updateIndustry(cmd.industry());
    }
}