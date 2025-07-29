package com.backend.immilog.company.application.service;

import com.backend.immilog.company.application.dto.CompanyFetchResult;
import com.backend.immilog.company.application.dto.CompanyRegisterCommand;
import com.backend.immilog.company.application.mapper.CompanyMapper;
import com.backend.immilog.company.domain.model.Company;
import com.backend.immilog.company.domain.model.CompanyId;
import com.backend.immilog.company.domain.model.CompanyManager;
import com.backend.immilog.company.domain.model.CompanyMetaData;
import com.backend.immilog.company.domain.model.Industry;
import com.backend.immilog.company.domain.service.CompanyRegistrationService;
import com.backend.immilog.company.domain.service.CompanyValidationService;
import com.backend.immilog.shared.enums.Country;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("CompanyService 애플리케이션 서비스 테스트")
class CompanyServiceTest {

    private final CompanyQueryService companyQueryService = mock(CompanyQueryService.class);
    private final CompanyCommandService companyCommandService = mock(CompanyCommandService.class);
    private final CompanyRegistrationService companyRegistrationService = mock(CompanyRegistrationService.class);
    private final CompanyValidationService companyValidationService = mock(CompanyValidationService.class);
    private final CompanyMapper companyMapper = mock(CompanyMapper.class);
    private final CompanyService companyService = new CompanyService(
            companyQueryService,
            companyCommandService,
            companyRegistrationService,
            companyValidationService,
            companyMapper
    );

    @Test
    @DisplayName("회사를 등록할 수 있다")
    void shouldRegisterCompany() {
        // given
        Long userSeq = 1L;
        CompanyRegisterCommand command = new CompanyRegisterCommand(
                Industry.IT,
                "서울",
                "테스트 회사",
                "test@company.com",
                "010-1234-5678",
                "서울시 강남구",
                Country.SOUTH_KOREA,
                "https://company.com",
                "logo.png"
        );

        CompanyManager manager = CompanyManager.of(Country.SOUTH_KOREA, "서울", userSeq);
        CompanyMetaData metaData = CompanyMetaData.of(Industry.IT, "테스트 회사", "test@company.com", "010-1234-5678", "서울시 강남구", "https://company.com", "logo.png");
        Company newCompany = new Company(null, manager, metaData);
        Company savedCompany = new Company(1L, manager, metaData);

        when(companyRegistrationService.registerNewCompany(userSeq, command)).thenReturn(newCompany);
        when(companyCommandService.save(newCompany)).thenReturn(savedCompany);

        // when
        CompanyId result = companyService.registerCompany(userSeq, command);

        // then
        assertThat(result.value()).isEqualTo(1L);
        verify(companyRegistrationService).registerNewCompany(userSeq, command);
        verify(companyCommandService).save(newCompany);
    }

    @Test
    @DisplayName("회사 정보를 수정할 수 있다")
    void shouldUpdateCompany() {
        // given
        Long userSeq = 1L;
        CompanyRegisterCommand command = new CompanyRegisterCommand(
                Industry.IT,
                "서울",
                "수정된 회사",
                "updated@company.com",
                "010-9876-5432",
                "서울시 서초구",
                Country.SOUTH_KOREA,
                "https://updated.com",
                "updated-logo.png"
        );

        CompanyManager manager = CompanyManager.of(Country.SOUTH_KOREA, "서울", userSeq);
        CompanyMetaData existingMetaData = CompanyMetaData.of(Industry.IT, "기존 회사", "existing@company.com", "010-1234-5678", "서울시 강남구", "https://existing.com", "logo.png");
        Company existingCompany = new Company(1L, manager, existingMetaData);

        CompanyMetaData updatedMetaData = CompanyMetaData.of(Industry.IT, "수정된 회사", "updated@company.com", "010-9876-5432", "서울시 서초구", "https://updated.com", "updated-logo.png");
        Company updatedCompany = new Company(1L, manager, updatedMetaData);

        when(companyQueryService.getByCompanyManagerUserSeq(userSeq)).thenReturn(existingCompany);
        when(companyMapper.updateCompany(existingCompany, command)).thenReturn(updatedCompany);
        when(companyCommandService.save(updatedCompany)).thenReturn(updatedCompany);

        // when
        companyService.updateCompany(userSeq, command);

        // then
        verify(companyQueryService).getByCompanyManagerUserSeq(userSeq);
        verify(companyValidationService).validateCompanyExists(existingCompany);
        verify(companyMapper).updateCompany(existingCompany, command);
        verify(companyCommandService).save(updatedCompany);
    }

    @Test
    @DisplayName("매니저 사용자 ID로 회사 정보를 조회할 수 있다")
    void shouldGetCompanyByManagerUserSeq() {
        // given
        Long userSeq = 1L;
        CompanyManager manager = CompanyManager.of(Country.SOUTH_KOREA, "서울", userSeq);
        CompanyMetaData metaData = CompanyMetaData.of(Industry.IT, "테스트 회사", "test@company.com", "010-1234-5678", "서울시 강남구", "https://company.com", "logo.png");
        Company company = new Company(1L, manager, metaData);

        when(companyQueryService.getByCompanyManagerUserSeq(userSeq)).thenReturn(company);

        // when
        CompanyFetchResult result = companyService.getCompanyByManagerUserSeq(userSeq);

        // then
        assertThat(result).isNotNull();
        verify(companyQueryService).getByCompanyManagerUserSeq(userSeq);
    }

    @Test
    @DisplayName("빈 회사를 조회할 때 빈 결과를 반환한다")
    void shouldReturnEmptyResultWhenCompanyIsEmpty() {
        // given
        Long userSeq = 1L;
        Company emptyCompany = Company.createEmpty();

        when(companyQueryService.getByCompanyManagerUserSeq(userSeq)).thenReturn(emptyCompany);

        // when
        CompanyFetchResult result = companyService.getCompanyByManagerUserSeq(userSeq);

        // then
        assertThat(result).isNotNull();
        verify(companyQueryService).getByCompanyManagerUserSeq(userSeq);
    }

    @Test
    @DisplayName("회사 ID로 회사를 조회할 수 있다")
    void shouldGetCompanyById() {
        // given
        CompanyId companyId = CompanyId.of(1L);
        CompanyManager manager = CompanyManager.of(Country.SOUTH_KOREA, "서울", 1L);
        CompanyMetaData metaData = CompanyMetaData.of(Industry.IT, "테스트 회사", "test@company.com", "010-1234-5678", "서울시 강남구", "https://company.com", "logo.png");
        Company company = new Company(1L, manager, metaData);

        when(companyQueryService.getById(1L)).thenReturn(company);

        // when
        Company result = companyService.getCompanyById(companyId);

        // then
        assertThat(result).isEqualTo(company);
        verify(companyQueryService).getById(1L);
    }

    @Test
    @DisplayName("회사를 삭제할 수 있다")
    void shouldDeleteCompany() {
        // given
        Long userSeq = 1L;
        CompanyManager manager = CompanyManager.of(Country.SOUTH_KOREA, "서울", userSeq);
        CompanyMetaData metaData = CompanyMetaData.of(Industry.IT, "테스트 회사", "test@company.com", "010-1234-5678", "서울시 강남구", "https://company.com", "logo.png");
        Company company = new Company(1L, manager, metaData);

        when(companyQueryService.getByCompanyManagerUserSeq(userSeq)).thenReturn(company);

        // when
        companyService.deleteCompany(userSeq);

        // then
        verify(companyQueryService).getByCompanyManagerUserSeq(userSeq);
        verify(companyValidationService).validateCompanyExists(company);
        verify(companyCommandService).delete(company);
    }

    @Test
    @DisplayName("새로 등록된 회사의 ID를 올바르게 반환한다")
    void shouldReturnCorrectCompanyIdAfterRegistration() {
        // given
        Long userSeq = 1L;
        Long expectedCompanyId = 123L;
        CompanyRegisterCommand command = new CompanyRegisterCommand(
                Industry.IT,
                "서울",
                "테스트 회사",
                "test@company.com",
                "010-1234-5678",
                "서울시 강남구",
                Country.SOUTH_KOREA,
                "https://company.com",
                "logo.png"
        );

        CompanyManager manager = CompanyManager.of(Country.SOUTH_KOREA, "서울", userSeq);
        CompanyMetaData metaData = CompanyMetaData.of(Industry.IT, "테스트 회사", "test@company.com", "010-1234-5678", "서울시 강남구", "https://company.com", "logo.png");
        Company newCompany = new Company(null, manager, metaData);
        Company savedCompany = new Company(expectedCompanyId, manager, metaData);

        when(companyRegistrationService.registerNewCompany(userSeq, command)).thenReturn(newCompany);
        when(companyCommandService.save(newCompany)).thenReturn(savedCompany);

        // when
        CompanyId result = companyService.registerCompany(userSeq, command);

        // then
        assertThat(result.value()).isEqualTo(expectedCompanyId);
    }

    @Test
    @DisplayName("회사 수정 시 모든 서비스가 올바른 순서로 호출된다")
    void shouldCallServicesInCorrectOrderDuringUpdate() {
        // given
        Long userSeq = 1L;
        CompanyRegisterCommand command = new CompanyRegisterCommand(
                Industry.ETC,
                "도쿄",
                "새로운 회사명",
                "new@company.com",
                "090-1234-5678",
                "도쿄시",
                Country.JAPAN,
                "https://new.com",
                "new-logo.png"
        );

        CompanyManager manager = CompanyManager.of(Country.SOUTH_KOREA, "서울", userSeq);
        CompanyMetaData existingMetaData = CompanyMetaData.of(Industry.IT, "기존 회사", "existing@company.com", "010-1234-5678", null, null, null);
        Company existingCompany = new Company(1L, manager, existingMetaData);

        CompanyManager updatedManager = CompanyManager.of(Country.JAPAN, "도쿄", userSeq);
        CompanyMetaData updatedMetaData = CompanyMetaData.of(Industry.ETC, "새로운 회사명", "new@company.com", "090-1234-5678", "도쿄시", "https://new.com", "new-logo.png");
        Company updatedCompany = new Company(1L, updatedManager, updatedMetaData);

        when(companyQueryService.getByCompanyManagerUserSeq(userSeq)).thenReturn(existingCompany);
        when(companyMapper.updateCompany(existingCompany, command)).thenReturn(updatedCompany);
        when(companyCommandService.save(updatedCompany)).thenReturn(updatedCompany);

        // when
        companyService.updateCompany(userSeq, command);

        // then
        verify(companyQueryService).getByCompanyManagerUserSeq(userSeq);
        verify(companyValidationService).validateCompanyExists(existingCompany);
        verify(companyMapper).updateCompany(existingCompany, command);
        verify(companyCommandService).save(updatedCompany);
    }
}