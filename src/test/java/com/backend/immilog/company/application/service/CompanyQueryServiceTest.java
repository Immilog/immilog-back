package com.backend.immilog.company.application.service;

import com.backend.immilog.company.domain.model.Company;
import com.backend.immilog.company.domain.model.CompanyManager;
import com.backend.immilog.company.domain.model.CompanyMetaData;
import com.backend.immilog.company.domain.model.Industry;
import com.backend.immilog.company.domain.repository.CompanyRepository;
import com.backend.immilog.shared.enums.Country;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("CompanyQueryService 애플리케이션 서비스 테스트")
class CompanyQueryServiceTest {

    private final CompanyRepository companyRepository = mock(CompanyRepository.class);
    private final CompanyQueryService companyQueryService = new CompanyQueryService(companyRepository);

    @Test
    @DisplayName("매니저 사용자 ID로 회사를 조회할 수 있다")
    void shouldGetCompanyByManagerUserSeq() {
        // given
        Long userSeq = 1L;
        CompanyManager manager = CompanyManager.of(Country.SOUTH_KOREA, "서울", userSeq);
        CompanyMetaData metaData = CompanyMetaData.of(Industry.IT, "테스트 회사", "test@company.com", "010-1234-5678", "서울시 강남구", "https://company.com", "logo.png");
        Company company = new Company(1L, manager, metaData);

        when(companyRepository.getByCompanyManagerUserSeq(userSeq)).thenReturn(Optional.of(company));

        // when
        Company result = companyQueryService.getByCompanyManagerUserSeq(userSeq);

        // then
        assertThat(result).isEqualTo(company);
        assertThat(result.name()).isEqualTo("테스트 회사");
        assertThat(result.managerUserSeq()).isEqualTo(userSeq);
        verify(companyRepository).getByCompanyManagerUserSeq(userSeq);
    }

    @Test
    @DisplayName("매니저 사용자 ID로 회사를 찾을 수 없으면 빈 회사를 반환한다")
    void shouldReturnEmptyCompanyWhenNotFoundByManagerUserSeq() {
        // given
        Long userSeq = 1L;

        when(companyRepository.getByCompanyManagerUserSeq(userSeq)).thenReturn(Optional.empty());

        // when
        Company result = companyQueryService.getByCompanyManagerUserSeq(userSeq);

        // then
        assertThat(result.isEmpty()).isTrue();
        assertThat(result.seq()).isNull();
        verify(companyRepository).getByCompanyManagerUserSeq(userSeq);
    }

    @Test
    @DisplayName("회사 ID로 회사를 조회할 수 있다")
    void shouldGetCompanyById() {
        // given
        Long companyId = 1L;
        CompanyManager manager = CompanyManager.of(Country.SOUTH_KOREA, "서울", 1L);
        CompanyMetaData metaData = CompanyMetaData.of(Industry.IT, "테스트 회사", "test@company.com", "010-1234-5678", "서울시 강남구", "https://company.com", "logo.png");
        Company company = new Company(companyId, manager, metaData);

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));

        // when
        Company result = companyQueryService.getById(companyId);

        // then
        assertThat(result).isEqualTo(company);
        assertThat(result.seq()).isEqualTo(companyId);
        assertThat(result.name()).isEqualTo("테스트 회사");
        verify(companyRepository).findById(companyId);
    }

    @Test
    @DisplayName("회사 ID로 회사를 찾을 수 없으면 빈 회사를 반환한다")
    void shouldReturnEmptyCompanyWhenNotFoundById() {
        // given
        Long companyId = 999L;

        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

        // when
        Company result = companyQueryService.getById(companyId);

        // then
        assertThat(result.isEmpty()).isTrue();
        assertThat(result.seq()).isNull();
        verify(companyRepository).findById(companyId);
    }

    @Test
    @DisplayName("회사명으로 존재 여부를 확인할 수 있다")
    void shouldCheckExistenceByName() {
        // given
        String companyName = "테스트 회사";

        when(companyRepository.existsByName(companyName)).thenReturn(true);

        // when
        boolean exists = companyQueryService.existsByName(companyName);

        // then
        assertThat(exists).isTrue();
        verify(companyRepository).existsByName(companyName);
    }

    @Test
    @DisplayName("존재하지 않는 회사명으로 확인하면 false를 반환한다")
    void shouldReturnFalseWhenCompanyNameDoesNotExist() {
        // given
        String companyName = "존재하지 않는 회사";

        when(companyRepository.existsByName(companyName)).thenReturn(false);

        // when
        boolean exists = companyQueryService.existsByName(companyName);

        // then
        assertThat(exists).isFalse();
        verify(companyRepository).existsByName(companyName);
    }

    @Test
    @DisplayName("다른 매니저의 회사를 조회할 수 있다")
    void shouldGetCompanyByDifferentManagerUserSeq() {
        // given
        Long userSeq = 2L;
        CompanyManager manager = CompanyManager.of(Country.JAPAN, "도쿄", userSeq);
        CompanyMetaData metaData = CompanyMetaData.of(Industry.ETC, "일본 회사", "japan@company.com", "090-1234-5678", "도쿄시", "https://japan.com", "japan-logo.png");
        Company company = new Company(2L, manager, metaData);

        when(companyRepository.getByCompanyManagerUserSeq(userSeq)).thenReturn(Optional.of(company));

        // when
        Company result = companyQueryService.getByCompanyManagerUserSeq(userSeq);

        // then
        assertThat(result).isEqualTo(company);
        assertThat(result.seq()).isEqualTo(2L);
        assertThat(result.name()).isEqualTo("일본 회사");
        assertThat(result.country()).isEqualTo(Country.JAPAN);
        assertThat(result.region()).isEqualTo("도쿄");
        assertThat(result.managerUserSeq()).isEqualTo(userSeq);
    }

    @Test
    @DisplayName("다른 회사 ID로 회사를 조회할 수 있다")
    void shouldGetCompanyByDifferentId() {
        // given
        Long companyId = 3L;
        CompanyManager manager = CompanyManager.of(Country.SOUTH_KOREA, "부산", 3L);
        CompanyMetaData metaData = CompanyMetaData.of(Industry.IT, "부산 회사", "busan@company.com", "051-1234-5678", "부산시 해운대구", "https://busan.com", "busan-logo.png");
        Company company = new Company(companyId, manager, metaData);

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));

        // when
        Company result = companyQueryService.getById(companyId);

        // then
        assertThat(result).isEqualTo(company);
        assertThat(result.seq()).isEqualTo(companyId);
        assertThat(result.name()).isEqualTo("부산 회사");
        assertThat(result.address()).isEqualTo("부산시 해운대구");
        assertThat(result.phone()).isEqualTo("051-1234-5678");
    }

    @Test
    @DisplayName("여러 회사명의 존재 여부를 확인할 수 있다")
    void shouldCheckExistenceForMultipleCompanyNames() {
        // given
        String existingName = "존재하는 회사";
        String nonExistingName = "존재하지 않는 회사";

        when(companyRepository.existsByName(existingName)).thenReturn(true);
        when(companyRepository.existsByName(nonExistingName)).thenReturn(false);

        // when
        boolean existingResult = companyQueryService.existsByName(existingName);
        boolean nonExistingResult = companyQueryService.existsByName(nonExistingName);

        // then
        assertThat(existingResult).isTrue();
        assertThat(nonExistingResult).isFalse();
        verify(companyRepository).existsByName(existingName);
        verify(companyRepository).existsByName(nonExistingName);
    }
}