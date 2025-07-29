package com.backend.immilog.company.application.usecase;

import com.backend.immilog.company.application.dto.CompanyRegisterCommand;
import com.backend.immilog.company.application.service.CompanyService;
import com.backend.immilog.company.domain.model.Industry;
import com.backend.immilog.shared.enums.Country;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DisplayName("UpdateCompanyUseCase 유즈케이스 테스트")
class UpdateCompanyUseCaseTest {

    private final CompanyService companyService = mock(CompanyService.class);
    private final UpdateCompanyUseCase.CompanyUpdater companyUpdater = new UpdateCompanyUseCase.CompanyUpdater(companyService);

    @Test
    @DisplayName("회사 정보를 정상적으로 수정할 수 있다")
    void shouldUpdateCompany() {
        // given
        Long userSeq = 1L;
        CompanyRegisterCommand command = new CompanyRegisterCommand(
                Industry.IT,
                "수정된 회사명",
                "updated@company.com",
                "010-9876-5432",
                "서울시 서초구",
                "https://updated-company.com",
                Country.SOUTH_KOREA,
                "서울",
                "updated-logo.png"
        );

        // when
        companyUpdater.updateCompany(userSeq, command);

        // then
        verify(companyService).updateCompany(userSeq, command);
    }

    @Test
    @DisplayName("회사 업종을 IT에서 ETC로 변경할 수 있다")
    void shouldUpdateCompanyIndustryFromITToETC() {
        // given
        Long userSeq = 2L;
        CompanyRegisterCommand command = new CompanyRegisterCommand(
                Industry.ETC,
                "업종 변경 회사",
                "industry-change@company.com",
                "010-1111-2222",
                "부산시 중구",
                "https://industry-change.com",
                Country.SOUTH_KOREA,
                "부산",
                "industry-change-logo.png"
        );

        // when
        companyUpdater.updateCompany(userSeq, command);

        // then
        verify(companyService).updateCompany(userSeq, command);
    }

    @Test
    @DisplayName("회사 지역을 한국에서 일본으로 변경할 수 있다")
    void shouldUpdateCompanyCountryFromKoreaToJapan() {
        // given
        Long userSeq = 3L;
        CompanyRegisterCommand command = new CompanyRegisterCommand(
                Industry.IT,
                "지역 변경 회사",
                "country-change@company.com",
                "090-3333-4444",
                "도쿄시 시부야구",
                "https://country-change.com",
                Country.JAPAN,
                "도쿄",
                "country-change-logo.png"
        );

        // when
        companyUpdater.updateCompany(userSeq, command);

        // then
        verify(companyService).updateCompany(userSeq, command);
    }

    @Test
    @DisplayName("회사명만 변경할 수 있다")
    void shouldUpdateOnlyCompanyName() {
        // given
        Long userSeq = 4L;
        CompanyRegisterCommand command = new CompanyRegisterCommand(
                Industry.IT,
                "새로운 회사명",
                "same@company.com",
                "010-1234-5678",
                "서울시 강남구",
                "https://same-company.com",
                Country.SOUTH_KOREA,
                "서울",
                "same-logo.png"
        );

        // when
        companyUpdater.updateCompany(userSeq, command);

        // then
        verify(companyService).updateCompany(userSeq, command);
    }

    @Test
    @DisplayName("회사 연락처 정보를 변경할 수 있다")
    void shouldUpdateCompanyContactInfo() {
        // given
        Long userSeq = 5L;
        CompanyRegisterCommand command = new CompanyRegisterCommand(
                Industry.ETC,
                "연락처 변경 회사",
                "new-contact@company.com",
                "010-9999-8888",
                "대구시 수성구",
                "https://new-contact.com",
                Country.SOUTH_KOREA,
                "대구",
                "new-contact-logo.png"
        );

        // when
        companyUpdater.updateCompany(userSeq, command);

        // then
        verify(companyService).updateCompany(userSeq, command);
    }

    @Test
    @DisplayName("회사 홈페이지를 null로 변경할 수 있다")
    void shouldUpdateCompanyHomepageToNull() {
        // given
        Long userSeq = 6L;
        CompanyRegisterCommand command = new CompanyRegisterCommand(
                Industry.IT,
                "홈페이지 제거 회사",
                "no-homepage@company.com",
                "010-7777-6666",
                "광주시 서구",
                null,
                Country.SOUTH_KOREA,
                "광주",
                "no-homepage-logo.png"
        );

        // when
        companyUpdater.updateCompany(userSeq, command);

        // then
        verify(companyService).updateCompany(userSeq, command);
    }

    @Test
    @DisplayName("회사 로고를 null로 변경할 수 있다")
    void shouldUpdateCompanyLogoToNull() {
        // given
        Long userSeq = 7L;
        CompanyRegisterCommand command = new CompanyRegisterCommand(
                Industry.ETC,
                "로고 제거 회사",
                "no-logo@company.com",
                "051-5555-4444",
                "부산시 해운대구",
                "https://no-logo.com",
                Country.SOUTH_KOREA,
                "부산",
                null
        );

        // when
        companyUpdater.updateCompany(userSeq, command);

        // then
        verify(companyService).updateCompany(userSeq, command);
    }

    @Test
    @DisplayName("회사의 모든 정보를 한번에 변경할 수 있다")
    void shouldUpdateAllCompanyInformation() {
        // given
        Long userSeq = 8L;
        CompanyRegisterCommand command = new CompanyRegisterCommand(
                Industry.ETC,
                "완전 변경 회사",
                "complete-change@company.com",
                "070-1234-5678",
                "인천시 연수구",
                "https://complete-change.com",
                Country.SOUTH_KOREA,
                "인천",
                "complete-change-logo.png"
        );

        // when
        companyUpdater.updateCompany(userSeq, command);

        // then
        verify(companyService).updateCompany(userSeq, command);
    }

    @Test
    @DisplayName("여러 번 회사 정보를 수정할 수 있다")
    void shouldAllowMultipleUpdates() {
        // given
        Long userSeq = 9L;
        
        CompanyRegisterCommand firstCommand = new CompanyRegisterCommand(
                Industry.IT,
                "첫 번째 수정",
                "first-update@company.com",
                "010-1111-1111",
                "서울시 종로구",
                "https://first-update.com",
                Country.SOUTH_KOREA,
                "서울",
                "first-logo.png"
        );

        CompanyRegisterCommand secondCommand = new CompanyRegisterCommand(
                Industry.ETC,
                "두 번째 수정",
                "second-update@company.com",
                "010-2222-2222",
                "부산시 동래구",
                "https://second-update.com",
                Country.SOUTH_KOREA,
                "부산",
                "second-logo.png"
        );

        // when
        companyUpdater.updateCompany(userSeq, firstCommand);
        companyUpdater.updateCompany(userSeq, secondCommand);

        // then
        verify(companyService).updateCompany(userSeq, firstCommand);
        verify(companyService).updateCompany(userSeq, secondCommand);
    }

    @Test
    @DisplayName("회사 수정 시 CompanyService가 올바르게 호출된다")
    void shouldCallCompanyServiceCorrectly() {
        // given
        Long userSeq = 10L;
        CompanyRegisterCommand command = new CompanyRegisterCommand(
                Industry.IT,
                "서비스 호출 테스트",
                "service-call@company.com",
                "010-0000-9999",
                "울산시 중구",
                "https://service-call.com",
                Country.SOUTH_KOREA,
                "울산",
                "service-call-logo.png"
        );

        // when
        companyUpdater.updateCompany(userSeq, command);

        // then
        verify(companyService).updateCompany(userSeq, command);
    }
}