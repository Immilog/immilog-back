package com.backend.immilog.company.domain.model;

import com.backend.immilog.company.exception.CompanyErrorCode;
import com.backend.immilog.company.exception.CompanyException;
import com.backend.immilog.user.domain.model.enums.Country;

public class Company {
    private final Long seq;
    private final CompanyManager manager;
    private final CompanyMetaData companyMetaData;

    public Company(
            Long seq,
            CompanyManager manager,
            CompanyMetaData companyMetaData
    ) {
        this.validate(manager, companyMetaData);
        this.seq = seq;
        this.manager = manager;
        this.companyMetaData = companyMetaData;
    }

    public static Company createEmpty() {
        return new Company(null, CompanyManager.createEmpty(), CompanyMetaData.createEmpty());
    }

    public boolean isEmpty() {
        return this.seq == null;
    }

    private void validate(
            CompanyManager manager,
            CompanyMetaData companyMetaData
    ) {
        if (manager == null) {
            throw new CompanyException(CompanyErrorCode.MANAGER_INFO_REQUIRED);
        }
        if (companyMetaData == null) {
            throw new CompanyException(CompanyErrorCode.COMPANY_META_DATA_REQUIRED);
        }
    }

    public Company seq(Long seq) {
        return new Company(seq, this.manager, this.companyMetaData);
    }

    public Company manager(
            Country country,
            String region,
            Long userSeq
    ) {
        var manager = CompanyManager.of(country, region, userSeq);
        return new Company(this.seq, manager, this.companyMetaData);
    }

    public Company companyData(
            Industry industry,
            String name,
            String email,
            String phone,
            String address,
            String homepage,
            String logo
    ) {
        var newCompanyMetaData = CompanyMetaData.of(industry, name, email, phone, address, homepage, logo);
        return new Company(this.seq, this.manager, newCompanyMetaData);
    }

    public Company updateName(String newName) {
        if (newName == null || this.companyMetaData.name().equals(newName)) {
            return this;
        }
        if (newName.trim().isEmpty()) {
            throw new CompanyException(CompanyErrorCode.INVALID_COMPANY_NAME);
        }
        var updatedMetaData = this.companyMetaData.withName(newName);
        return new Company(this.seq, this.manager, updatedMetaData);
    }

    public Company updatePhone(String newPhone) {
        if (newPhone == null || this.companyMetaData.phone().equals(newPhone)) {
            return this;
        }
        var updatedMetaData = this.companyMetaData.withPhone(newPhone);
        return new Company(this.seq, this.manager, updatedMetaData);
    }

    public Company updateLogo(String newLogo) {
        if (newLogo == null || this.companyMetaData.logo().equals(newLogo)) {
            return this;
        }
        var updatedMetaData = this.companyMetaData.withLogo(newLogo);
        return new Company(this.seq, this.manager, updatedMetaData);
    }

    public Company updateHomepage(String newHomepage) {
        if (newHomepage == null || this.companyMetaData.homepage().equals(newHomepage)) {
            return this;
        }
        var updatedMetaData = this.companyMetaData.withHomepage(newHomepage);
        return new Company(this.seq, this.manager, updatedMetaData);
    }

    public Company updateEmail(String newEmail) {
        if (newEmail == null || this.companyMetaData.email().equals(newEmail)) {
            return this;
        }
        var updatedMetaData = this.companyMetaData.withEmail(newEmail);
        return new Company(this.seq, this.manager, updatedMetaData);
    }

    public Company updateCountry(Country newCountry) {
        if (newCountry == null || this.manager.country().equals(newCountry)) {
            return this;
        }
        var updatedManager = this.manager.withCountry(newCountry);
        return new Company(this.seq, updatedManager, this.companyMetaData);
    }

    public Company updateAddress(String newAddress) {
        if (newAddress == null || this.companyMetaData.address().equals(newAddress)) {
            return this;
        }
        var updatedMetaData = this.companyMetaData.withAddress(newAddress);
        return new Company(this.seq, this.manager, updatedMetaData);
    }

    public Company updateRegion(String newRegion) {
        if (newRegion == null || this.manager.region().equals(newRegion)) {
            return this;
        }
        var updatedManager = this.manager.withRegion(newRegion);
        return new Company(this.seq, updatedManager, this.companyMetaData);
    }

    public Company updateIndustry(Industry industry) {
        if (industry == null || this.companyMetaData.industry().equals(industry)) {
            return this;
        }
        var updatedMetaData = this.companyMetaData.withIndustry(industry);
        return new Company(this.seq, this.manager, updatedMetaData);
    }

    public Industry industry() {return this.companyMetaData.industry();}

    public String name() {return this.companyMetaData.name();}

    public String email() {return this.companyMetaData.email();}

    public String phone() {return this.companyMetaData.phone();}

    public String address() {return this.companyMetaData.address();}

    public String homepage() {return this.companyMetaData.homepage();}

    public Country country() {return this.manager.country();}

    public String region() {return this.manager.region();}

    public String logo() {return this.companyMetaData.logo();}

    public Long managerUserSeq() {return this.manager.userSeq();}

    public Long seq() {return seq;}
}