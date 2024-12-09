package com.backend.immilog.user.infrastructure.jpa.entity;

import com.backend.immilog.user.domain.model.Company;
import com.backend.immilog.user.domain.enums.Industry;
import com.backend.immilog.user.domain.enums.UserCountry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("CompanyEntity 테스트")
class CompanyEntityTest {
    @Test
    @DisplayName("CompanyEntity Company 컨버팅")
    void companyEntityFromCompany_validCompany() {
        Company company = Company.builder()
                .industry(Industry.IT)
                .companyName("Test Company")
                .companyEmail("test@company.com")
                .companyPhone("1234567890")
                .companyAddress("123 Test St")
                .companyHomepage("www.test.com")
                .companyCountry(UserCountry.SOUTH_KOREA)
                .companyRegion("Test Region")
                .companyLogo("logo.png")
                .companyManagerUserSeq(1L)
                .build();

        CompanyEntity companyEntity = CompanyEntity.from(company);

        assertThat(companyEntity.getIndustry()).isEqualTo(company.industry());
        assertThat(companyEntity.getCompanyName()).isEqualTo(company.companyName());
        assertThat(companyEntity.getCompanyEmail()).isEqualTo(company.companyEmail());
        assertThat(companyEntity.getCompanyPhone()).isEqualTo(company.companyPhone());
        assertThat(companyEntity.getCompanyAddress()).isEqualTo(company.companyAddress());
        assertThat(companyEntity.getCompanyHomepage()).isEqualTo(company.companyHomepage());
        assertThat(companyEntity.getCompanyCountry()).isEqualTo(company.companyCountry());
        assertThat(companyEntity.getCompanyRegion()).isEqualTo(company.companyRegion());
        assertThat(companyEntity.getCompanyLogo()).isEqualTo(company.companyLogo());
        assertThat(companyEntity.getCompanyManagerUserSeq()).isEqualTo(company.companyManagerUserSeq());
    }

    @DisplayName("CompanyEntity Company 컨버팅 - null Company object")
    void companyEntityToDomain_validCompanyEntity() {
        CompanyEntity companyEntity = CompanyEntity.builder()
                .seq(1L)
                .industry(Industry.IT)
                .companyName("Test Company")
                .companyEmail("test@company.com")
                .companyPhone("1234567890")
                .companyAddress("123 Test St")
                .companyHomepage("www.test.com")
                .companyCountry(UserCountry.SOUTH_KOREA)
                .companyRegion("Test Region")
                .companyLogo("logo.png")
                .companyManagerUserSeq(1L)
                .build();

        Company company = companyEntity.toDomain();

        assertThat(company.seq()).isEqualTo(companyEntity.getSeq());
        assertThat(company.industry()).isEqualTo(companyEntity.getIndustry());
        assertThat(company.companyName()).isEqualTo(companyEntity.getCompanyName());
        assertThat(company.companyEmail()).isEqualTo(companyEntity.getCompanyEmail());
        assertThat(company.companyPhone()).isEqualTo(companyEntity.getCompanyPhone());
        assertThat(company.companyAddress()).isEqualTo(companyEntity.getCompanyAddress());
        assertThat(company.companyHomepage()).isEqualTo(companyEntity.getCompanyHomepage());
        assertThat(company.companyCountry()).isEqualTo(companyEntity.getCompanyCountry());
        assertThat(company.companyRegion()).isEqualTo(companyEntity.getCompanyRegion());
        assertThat(company.companyLogo()).isEqualTo(companyEntity.getCompanyLogo());
        assertThat(company.companyManagerUserSeq()).isEqualTo(companyEntity.getCompanyManagerUserSeq());
    }

    @DisplayName("CompanyEntity from - null Company object")
    void companyEntityFromCompany_nullCompany() {
        Company company = null;

        assertThatThrownBy(() -> CompanyEntity.from(company))
                .isInstanceOf(NullPointerException.class);
    }

    @DisplayName("CompanyEntity toDomain - null fields")
    void companyEntityToDomain_nullFields() {
        CompanyEntity companyEntity = CompanyEntity.builder().build();

        Company company = companyEntity.toDomain();

        assertThat(company.seq()).isNull();
        assertThat(company.industry()).isNull();
        assertThat(company.companyName()).isNull();
        assertThat(company.companyEmail()).isNull();
        assertThat(company.companyPhone()).isNull();
        assertThat(company.companyAddress()).isNull();
        assertThat(company.companyHomepage()).isNull();
        assertThat(company.companyCountry()).isNull();
        assertThat(company.companyRegion()).isNull();
        assertThat(company.companyLogo()).isNull();
        assertThat(company.companyManagerUserSeq()).isNull();
    }

    @Test
    @DisplayName("엔티티 세터")
    void companyEntitySetters() {
        CompanyEntity companyEntity = CompanyEntity.builder().seq(1L).build();
        companyEntity.setIndustry(Industry.IT);
        companyEntity.setCompanyName("Test Company");
        companyEntity.setCompanyEmail("email@email.com");
        companyEntity.setCompanyPhone("1234567890");
        companyEntity.setCompanyAddress("123 Test St");
        companyEntity.setCompanyHomepage("www.test.com");
        companyEntity.setCompanyCountry(UserCountry.SOUTH_KOREA);
        companyEntity.setCompanyRegion("Test Region");
        companyEntity.setCompanyLogo("logo.png");
        companyEntity.setCompanyManagerUserSeq(1L);
    }
}