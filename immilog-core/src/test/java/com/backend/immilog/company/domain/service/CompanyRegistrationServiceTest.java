package com.backend.immilog.company.domain.service;

import com.backend.immilog.company.application.dto.CompanyRegisterCommand;
import com.backend.immilog.company.application.mapper.CompanyMapper;
import com.backend.immilog.company.domain.model.Company;
import com.backend.immilog.company.domain.model.CompanyManager;
import com.backend.immilog.company.domain.model.CompanyMetaData;
import com.backend.immilog.company.domain.model.Industry;
import com.backend.immilog.company.domain.repository.CompanyRepository;
import com.backend.immilog.company.exception.CompanyErrorCode;
import com.backend.immilog.company.exception.CompanyException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("CompanyRegistrationService 도메인 서비스 테스트")
class CompanyRegistrationServiceTest {

    private final CompanyRepository companyRepository = mock(CompanyRepository.class);
    private final CompanyMapper companyMapper = mock(CompanyMapper.class);
    private final CompanyRegistrationService companyRegistrationService = new CompanyRegistrationService(companyRepository, companyMapper);

    @Test
    @DisplayName("새로운 회사를 등록할 수 있다")
    void shouldRegisterNewCompany() {
        // given
        String userId = "1";
        CompanyRegisterCommand command = new CompanyRegisterCommand(
                Industry.IT,
                "서울",
                "테스트 회사",
                "010-1234-5678",
                "test@company.com",
                "https://company.com",
                "KR",
                "서울시 강남구",
                "logo.png"
        );

        CompanyManager manager = CompanyManager.of("KR", "서울", userId);
        CompanyMetaData metaData = CompanyMetaData.of(Industry.IT, "테스트 회사", "test@company.com", "010-1234-5678", "서울시 강남구", "https://company.com", "logo.png");
        Company expectedCompany = new Company(null, manager, metaData);

        when(companyRepository.findByManagerUserId(userId)).thenReturn(Optional.empty());
        when(companyRepository.existsByName(command.name())).thenReturn(false);
        when(companyMapper.toNewCompany(userId, command)).thenReturn(expectedCompany);

        // when
        Company result = companyRegistrationService.registerNewCompany(userId, command);

        // then
        assertThat(result).isEqualTo(expectedCompany);
        assertThat(result.name()).isEqualTo("테스트 회사");
        assertThat(result.countryId()).isEqualTo("KR");
        assertThat(result.region()).isEqualTo("서울");
    }

    @Test
    @DisplayName("이미 회사 매니저인 사용자가 새 회사를 등록하려 하면 예외가 발생한다")
    void shouldThrowExceptionWhenUserIsAlreadyManager() {
        // given
        String userId = "1";
        CompanyRegisterCommand command = new CompanyRegisterCommand(
                Industry.IT,
                "테스트 회사",
                "test@company.com",
                "010-1234-5678",
                "서울",
                "https://company.com",
                "KR",
                "서울시 강남구",
                "logo.png"
        );

        CompanyManager existingManager = CompanyManager.of("KR", "서울", userId);
        CompanyMetaData existingMetaData = CompanyMetaData.of(Industry.IT, "기존 회사", "existing@company.com", "010-9876-5432", null, null, null);
        Company existingCompany = new Company("1", existingManager, existingMetaData);

        when(companyRepository.findByManagerUserId(userId)).thenReturn(Optional.of(existingCompany));

        // when & then
        assertThatThrownBy(() -> companyRegistrationService.registerNewCompany(userId, command))
                .isInstanceOf(CompanyException.class)
                .hasMessage("User is already a company manager.");
    }

    @Test
    @DisplayName("빈 회사를 관리하는 사용자는 새 회사를 등록할 수 있다")
    void shouldAllowRegistrationWhenUserManagesEmptyCompany() {
        // given
        String userId = "1";
        CompanyRegisterCommand command = new CompanyRegisterCommand(
                Industry.IT,
                "테스트 회사",
                "test@company.com",
                "010-1234-5678",
                "서울",
                "https://company.com",
                "KR",
                "서울시 강남구",
                "logo.png"
        );

        Company emptyCompany = Company.createEmpty();
        CompanyManager manager = CompanyManager.of("KR", "서울", userId);
        CompanyMetaData metaData = CompanyMetaData.of(Industry.IT, "테스트 회사", "test@company.com", "010-1234-5678", "서울시 강남구", "https://company.com", "logo.png");
        Company expectedCompany = new Company(null, manager, metaData);

        when(companyRepository.findByManagerUserId(userId)).thenReturn(Optional.of(emptyCompany));
        when(companyRepository.existsByName(command.name())).thenReturn(false);
        when(companyMapper.toNewCompany(userId, command)).thenReturn(expectedCompany);

        // when
        Company result = companyRegistrationService.registerNewCompany(userId, command);

        // then
        assertThat(result).isEqualTo(expectedCompany);
    }

    @Test
    @DisplayName("이미 존재하는 회사명으로 등록하려 하면 예외가 발생한다")
    void shouldThrowExceptionWhenCompanyNameAlreadyExists() {
        // given
        String userId = "1";
        CompanyRegisterCommand command = new CompanyRegisterCommand(
                Industry.IT,
                "기존 회사",
                "test@company.com",
                "010-1234-5678",
                "서울",
                "https://company.com",
                "KR",
                "서울시 강남구",
                "logo.png"
        );

        when(companyRepository.findByManagerUserId(userId)).thenReturn(Optional.empty());
        when(companyRepository.existsByName("기존 회사")).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> companyRegistrationService.registerNewCompany(userId, command))
                .isInstanceOf(CompanyException.class)
                .hasMessage(CompanyErrorCode.COMPANY_NAME_ALREADY_EXISTS.getMessage());
    }

    @Test
    @DisplayName("사용자가 매니저로 등록되지 않은 경우 새 회사를 등록할 수 있다")
    void shouldAllowRegistrationWhenUserIsNotManager() {
        // given
        String userId = "1";
        CompanyRegisterCommand command = new CompanyRegisterCommand(
                Industry.IT,
                "테스트 회사",
                "서울",
                "test@company.com",
                "010-1234-5678",
                "https://company.com",
                "KR",
                "서울시 강남구",
                "logo.png"
        );

        CompanyManager manager = CompanyManager.of("KR", "서울", userId);
        CompanyMetaData metaData = CompanyMetaData.of(Industry.IT, "테스트 회사", "test@company.com", "010-1234-5678", "서울시 강남구", "https://company.com", "logo.png");
        Company expectedCompany = new Company(null, manager, metaData);

        when(companyRepository.findByManagerUserId(userId)).thenReturn(Optional.empty());
        when(companyRepository.existsByName(command.name())).thenReturn(false);
        when(companyMapper.toNewCompany(userId, command)).thenReturn(expectedCompany);

        // when
        Company result = companyRegistrationService.registerNewCompany(userId, command);

        // then
        assertThat(result).isEqualTo(expectedCompany);
        assertThat(result.name()).isEqualTo("테스트 회사");
        assertThat(result.industry()).isEqualTo(Industry.IT);
        assertThat(result.email()).isEqualTo("test@company.com");
        assertThat(result.phone()).isEqualTo("010-1234-5678");
        assertThat(result.address()).isEqualTo("서울시 강남구");
        assertThat(result.homepage()).isEqualTo("https://company.com");
        assertThat(result.logo()).isEqualTo("logo.png");
    }

    @Test
    @DisplayName("동일한 회사명이 아닐 때 등록할 수 있다")
    void shouldAllowRegistrationWhenCompanyNameIsUnique() {
        // given
        String userId = "1";
        CompanyRegisterCommand command = new CompanyRegisterCommand(
                Industry.IT,
                "유니크한 회사명",
                "test@company.com",
                "010-1234-5678",
                "서울",
                "https://company.com",
                "KR",
                "서울시 강남구",
                "logo.png"
        );

        CompanyManager manager = CompanyManager.of("KR", "서울", userId);
        CompanyMetaData metaData = CompanyMetaData.of(Industry.IT, "유니크한 회사명", "test@company.com", "010-1234-5678", "서울시 강남구", "https://company.com", "logo.png");
        Company expectedCompany = new Company(null, manager, metaData);

        when(companyRepository.findByManagerUserId(userId)).thenReturn(Optional.empty());
        when(companyRepository.existsByName("유니크한 회사명")).thenReturn(false);
        when(companyMapper.toNewCompany(userId, command)).thenReturn(expectedCompany);

        // when
        Company result = companyRegistrationService.registerNewCompany(userId, command);

        // then
        assertThat(result).isEqualTo(expectedCompany);
        assertThat(result.name()).isEqualTo("유니크한 회사명");
    }
}