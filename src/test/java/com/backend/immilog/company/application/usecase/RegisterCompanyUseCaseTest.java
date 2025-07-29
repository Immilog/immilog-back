package com.backend.immilog.company.application.usecase;

import com.backend.immilog.company.application.dto.CompanyRegisterCommand;
import com.backend.immilog.company.application.service.CompanyService;
import com.backend.immilog.company.domain.model.CompanyId;
import com.backend.immilog.company.domain.model.Industry;
import com.backend.immilog.shared.enums.Country;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("RegisterCompanyUseCase 유즈케이스 테스트")
class RegisterCompanyUseCaseTest {

    private final CompanyService companyService = mock(CompanyService.class);
    private final RegisterCompanyUseCase.CompanyRegistrar companyRegistrar = new RegisterCompanyUseCase.CompanyRegistrar(companyService);

    @Test
    @DisplayName("새로운 회사를 정상적으로 등록할 수 있다")
    void shouldRegisterNewCompany() {
        // given
        Long userSeq = 1L;
        CompanyRegisterCommand command = new CompanyRegisterCommand(
                Industry.IT,
                "테스트 회사",
                "test@company.com",
                "010-1234-5678",
                "서울시 강남구",
                "https://company.com",
                Country.SOUTH_KOREA,
                "서울",
                "logo.png"
        );

        CompanyId expectedCompanyId = CompanyId.of(1L);

        when(companyService.registerCompany(userSeq, command)).thenReturn(expectedCompanyId);

        // when
        CompanyId result = companyRegistrar.registerCompany(userSeq, command);

        // then
        assertThat(result).isEqualTo(expectedCompanyId);
        assertThat(result.value()).isEqualTo(1L);
        verify(companyService).registerCompany(userSeq, command);
    }

    @Test
    @DisplayName("IT 업종의 회사를 등록할 수 있다")
    void shouldRegisterCompanyWithITIndustry() {
        // given
        Long userSeq = 2L;
        CompanyRegisterCommand command = new CompanyRegisterCommand(
                Industry.IT,
                "IT 회사",
                "it@company.com",
                "010-1111-2222",
                "서울시 종로구",
                "https://it-company.com",
                Country.SOUTH_KOREA,
                "서울",
                "it-logo.png"
        );

        CompanyId expectedCompanyId = CompanyId.of(2L);

        when(companyService.registerCompany(userSeq, command)).thenReturn(expectedCompanyId);

        // when
        CompanyId result = companyRegistrar.registerCompany(userSeq, command);

        // then
        assertThat(result).isEqualTo(expectedCompanyId);
        assertThat(result.value()).isEqualTo(2L);
        verify(companyService).registerCompany(userSeq, command);
    }

    @Test
    @DisplayName("ETC 업종의 회사를 등록할 수 있다")
    void shouldRegisterCompanyWithETCIndustry() {
        // given
        Long userSeq = 3L;
        CompanyRegisterCommand command = new CompanyRegisterCommand(
                Industry.ETC,
                "기타 업종 회사",
                "etc@company.com",
                "010-3333-4444",
                "부산시 해운대구",
                "https://etc-company.com",
                Country.SOUTH_KOREA,
                "부산",
                "etc-logo.png"
        );

        CompanyId expectedCompanyId = CompanyId.of(3L);

        when(companyService.registerCompany(userSeq, command)).thenReturn(expectedCompanyId);

        // when
        CompanyId result = companyRegistrar.registerCompany(userSeq, command);

        // then
        assertThat(result).isEqualTo(expectedCompanyId);
        assertThat(result.value()).isEqualTo(3L);
        verify(companyService).registerCompany(userSeq, command);
    }

    @Test
    @DisplayName("일본 지역의 회사를 등록할 수 있다")
    void shouldRegisterCompanyInJapan() {
        // given
        Long userSeq = 4L;
        CompanyRegisterCommand command = new CompanyRegisterCommand(
                Industry.IT,
                "일본 회사",
                "japan@company.com",
                "090-1234-5678",
                "도쿄시 시부야구",
                "https://japan-company.com",
                Country.JAPAN,
                "도쿄",
                "japan-logo.png"
        );

        CompanyId expectedCompanyId = CompanyId.of(4L);

        when(companyService.registerCompany(userSeq, command)).thenReturn(expectedCompanyId);

        // when
        CompanyId result = companyRegistrar.registerCompany(userSeq, command);

        // then
        assertThat(result).isEqualTo(expectedCompanyId);
        assertThat(result.value()).isEqualTo(4L);
        verify(companyService).registerCompany(userSeq, command);
    }

    @Test
    @DisplayName("홈페이지가 없는 회사를 등록할 수 있다")
    void shouldRegisterCompanyWithoutHomepage() {
        // given
        Long userSeq = 5L;
        CompanyRegisterCommand command = new CompanyRegisterCommand(
                Industry.IT,
                "홈페이지 없는 회사",
                "no-homepage@company.com",
                "010-5555-6666",
                "대구시 중구",
                null,
                Country.SOUTH_KOREA,
                "대구",
                "no-homepage-logo.png"
        );

        CompanyId expectedCompanyId = CompanyId.of(5L);

        when(companyService.registerCompany(userSeq, command)).thenReturn(expectedCompanyId);

        // when
        CompanyId result = companyRegistrar.registerCompany(userSeq, command);

        // then
        assertThat(result).isEqualTo(expectedCompanyId);
        assertThat(result.value()).isEqualTo(5L);
        verify(companyService).registerCompany(userSeq, command);
    }

    @Test
    @DisplayName("로고가 없는 회사를 등록할 수 있다")
    void shouldRegisterCompanyWithoutLogo() {
        // given
        Long userSeq = 6L;
        CompanyRegisterCommand command = new CompanyRegisterCommand(
                Industry.ETC,
                "로고 없는 회사",
                "no-logo@company.com",
                "051-7777-8888",
                "부산시 중구",
                "https://no-logo-company.com",
                Country.SOUTH_KOREA,
                "부산",
                null
        );

        CompanyId expectedCompanyId = CompanyId.of(6L);

        when(companyService.registerCompany(userSeq, command)).thenReturn(expectedCompanyId);

        // when
        CompanyId result = companyRegistrar.registerCompany(userSeq, command);

        // then
        assertThat(result).isEqualTo(expectedCompanyId);
        assertThat(result.value()).isEqualTo(6L);
        verify(companyService).registerCompany(userSeq, command);
    }

    @Test
    @DisplayName("서로 다른 사용자가 각각 회사를 등록할 수 있다")
    void shouldAllowDifferentUsersToRegisterCompanies() {
        // given
        Long userSeq1 = 7L;
        Long userSeq2 = 8L;

        CompanyRegisterCommand command1 = new CompanyRegisterCommand(
                Industry.IT,
                "첫 번째 회사",
                "first@company.com",
                "010-1111-1111",
                "서울시 강남구",
                "https://first-company.com",
                Country.SOUTH_KOREA,
                "서울",
                "first-logo.png"
        );

        CompanyRegisterCommand command2 = new CompanyRegisterCommand(
                Industry.ETC,
                "두 번째 회사",
                "second@company.com",
                "010-2222-2222",
                "부산시 해운대구",
                "https://second-company.com",
                Country.SOUTH_KOREA,
                "부산",
                "second-logo.png"
        );

        CompanyId expectedCompanyId1 = CompanyId.of(7L);
        CompanyId expectedCompanyId2 = CompanyId.of(8L);

        when(companyService.registerCompany(userSeq1, command1)).thenReturn(expectedCompanyId1);
        when(companyService.registerCompany(userSeq2, command2)).thenReturn(expectedCompanyId2);

        // when
        CompanyId result1 = companyRegistrar.registerCompany(userSeq1, command1);
        CompanyId result2 = companyRegistrar.registerCompany(userSeq2, command2);

        // then
        assertThat(result1).isEqualTo(expectedCompanyId1);
        assertThat(result2).isEqualTo(expectedCompanyId2);
        assertThat(result1.value()).isEqualTo(7L);
        assertThat(result2.value()).isEqualTo(8L);
        verify(companyService).registerCompany(userSeq1, command1);
        verify(companyService).registerCompany(userSeq2, command2);
    }

    @Test
    @DisplayName("회사 등록 시 CompanyService가 올바르게 호출된다")
    void shouldCallCompanyServiceCorrectly() {
        // given
        Long userSeq = 9L;
        CompanyRegisterCommand command = new CompanyRegisterCommand(
                Industry.IT,
                "서비스 호출 테스트",
                "service-test@company.com",
                "010-9999-0000",
                "광주시 서구",
                "https://service-test.com",
                Country.SOUTH_KOREA,
                "광주",
                "service-test-logo.png"
        );

        CompanyId expectedCompanyId = CompanyId.of(9L);

        when(companyService.registerCompany(userSeq, command)).thenReturn(expectedCompanyId);

        // when
        CompanyId result = companyRegistrar.registerCompany(userSeq, command);

        // then
        assertThat(result).isEqualTo(expectedCompanyId);
        verify(companyService).registerCompany(userSeq, command);
    }
}