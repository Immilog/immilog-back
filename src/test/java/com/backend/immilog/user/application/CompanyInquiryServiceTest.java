package com.backend.immilog.user.application;

import com.backend.immilog.user.application.result.CompanyResult;
import com.backend.immilog.user.application.services.CompanyInquiryService;
import com.backend.immilog.user.domain.model.Company;
import com.backend.immilog.user.domain.model.enums.UserCountry;
import com.backend.immilog.user.domain.repositories.CompanyRepository;
import com.backend.immilog.user.infrastructure.jpa.entity.CompanyEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.when;

@DisplayName("CompanyInquiryService 테스트")
class CompanyInquiryServiceTest {
    @Mock
    private CompanyRepository companyRepository;
    private CompanyInquiryService companyInquiryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        companyInquiryService = new CompanyInquiryService(companyRepository);
    }

    @Test
    @DisplayName("회사정보 조회 - 성공")
    void getCompany() {
        // given
        Long userSeq = 1L;
        Company company = CompanyEntity.builder()
                .seq(1L)
                .companyName("회사명")
                .companyEmail("이메일")
                .companyPhone("전화번호")
                .companyAddress("주소")
                .companyHomepage("홈페이지")
                .companyCountry(UserCountry.SOUTH_KOREA)
                .companyRegion("지역")
                .companyLogo("로고")
                .build()
                .toDomain();
        when(companyRepository.getByCompanyManagerUserSeq(userSeq)).thenReturn(Optional.of(company));
        // when
        CompanyResult result = companyInquiryService.getCompany(userSeq);
        // then
        Assertions.assertThat(result.companyLogo()).isEqualTo("로고");
    }


}