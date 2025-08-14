package com.backend.immilog.company.domain.model;

import com.backend.immilog.company.exception.CompanyErrorCode;
import com.backend.immilog.company.exception.CompanyException;

public class Company {
    private final String id;
    private final CompanyManager manager;
    private final CompanyMetaData companyMetaData;

    public Company(
            String id,
            CompanyManager manager,
            CompanyMetaData companyMetaData
    ) {
        this.validate(manager, companyMetaData);
        this.id = id;
        this.manager = manager;
        this.companyMetaData = companyMetaData;
    }

    public static Company createEmpty() {
        return new Company(null, CompanyManager.createEmpty(), CompanyMetaData.createEmpty());
    }

    public boolean isEmpty() {
        return this.id == null;
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

    public Company id(String id) {
        return new Company(id, this.manager, this.companyMetaData);
    }

    public Company manager(
            String countryId,
            String region,
            String userId
    ) {
        var manager = CompanyManager.of(countryId, region, userId);
        return new Company(this.id, manager, this.companyMetaData);
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
        return new Company(this.id, this.manager, newCompanyMetaData);
    }

    public Company updateName(String newName) {
        if (newName == null || this.companyMetaData.name().equals(newName)) {
            return this;
        }
        if (newName.trim().isEmpty()) {
            throw new CompanyException(CompanyErrorCode.INVALID_COMPANY_NAME);
        }
        var updatedMetaData = this.companyMetaData.withName(newName);
        return new Company(this.id, this.manager, updatedMetaData);
    }

    public Company updatePhone(String newPhone) {
        if (newPhone == null || this.companyMetaData.phone().equals(newPhone)) {
            return this;
        }
        var updatedMetaData = this.companyMetaData.withPhone(newPhone);
        return new Company(this.id, this.manager, updatedMetaData);
    }

    public Company updateLogo(String newLogo) {
        if (newLogo == null || this.companyMetaData.logo().equals(newLogo)) {
            return this;
        }
        var updatedMetaData = this.companyMetaData.withLogo(newLogo);
        return new Company(this.id, this.manager, updatedMetaData);
    }

    public Company updateHomepage(String newHomepage) {
        if (newHomepage == null || this.companyMetaData.homepage().equals(newHomepage)) {
            return this;
        }
        var updatedMetaData = this.companyMetaData.withHomepage(newHomepage);
        return new Company(this.id, this.manager, updatedMetaData);
    }

    public Company updateEmail(String newEmail) {
        if (newEmail == null || this.companyMetaData.email().equals(newEmail)) {
            return this;
        }
        var updatedMetaData = this.companyMetaData.withEmail(newEmail);
        return new Company(this.id, this.manager, updatedMetaData);
    }

    public Company updateCountry(String newCountryId) {
        if (newCountryId == null || this.manager.countryId().equals(newCountryId)) {
            return this;
        }
        var updatedManager = this.manager.withCountry(newCountryId);
        return new Company(this.id, updatedManager, this.companyMetaData);
    }

    public Company updateAddress(String newAddress) {
        if (newAddress == null || this.companyMetaData.address().equals(newAddress)) {
            return this;
        }
        var updatedMetaData = this.companyMetaData.withAddress(newAddress);
        return new Company(this.id, this.manager, updatedMetaData);
    }

    public Company updateRegion(String newRegion) {
        if (newRegion == null || this.manager.region().equals(newRegion)) {
            return this;
        }
        var updatedManager = this.manager.withRegion(newRegion);
        return new Company(this.id, updatedManager, this.companyMetaData);
    }

    public Company updateIndustry(Industry industry) {
        if (industry == null || this.companyMetaData.industry().equals(industry)) {
            return this;
        }
        var updatedMetaData = this.companyMetaData.withIndustry(industry);
        return new Company(this.id, this.manager, updatedMetaData);
    }

    public Industry industry() {return this.companyMetaData.industry();}

    public String name() {return this.companyMetaData.name();}

    public String email() {return this.companyMetaData.email();}

    public String phone() {return this.companyMetaData.phone();}

    public String address() {return this.companyMetaData.address();}

    public String homepage() {return this.companyMetaData.homepage();}

    public String countryId() {return this.manager.countryId();}

    public String region() {return this.manager.region();}

    public String logo() {return this.companyMetaData.logo();}

    public String managerUserId() {return this.manager.userId();}

    public String id() {return id;}
}