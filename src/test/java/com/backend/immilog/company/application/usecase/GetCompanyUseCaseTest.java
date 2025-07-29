package com.backend.immilog.company.application.usecase;

import com.backend.immilog.company.application.dto.CompanyFetchResult;
import com.backend.immilog.company.application.service.CompanyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("GetCompanyUseCase 유즈케이스 테스트")
class GetCompanyUseCaseTest {

    private final CompanyService companyService = mock(CompanyService.class);
    private final GetCompanyUseCase.CompanyFetcher companyFetcher = new GetCompanyUseCase.CompanyFetcher(companyService);

    @Test
    @DisplayName("매니저 사용자 ID로 회사 정보를 조회할 수 있다")
    void shouldGetCompanyByManagerUserSeq() {
        // given
        Long userSeq = 1L;
        CompanyFetchResult expectedResult = CompanyFetchResult.createEmpty();

        when(companyService.getCompanyByManagerUserSeq(userSeq)).thenReturn(expectedResult);

        // when
        CompanyFetchResult result = companyFetcher.getCompany(userSeq);

        // then
        assertThat(result).isEqualTo(expectedResult);
        verify(companyService).getCompanyByManagerUserSeq(userSeq);
    }

    @Test
    @DisplayName("존재하는 회사 정보를 조회할 수 있다")
    void shouldGetExistingCompany() {
        // given
        Long userSeq = 2L;
        CompanyFetchResult mockResult = CompanyFetchResult.createEmpty(); // Mock result for existing company

        when(companyService.getCompanyByManagerUserSeq(userSeq)).thenReturn(mockResult);

        // when
        CompanyFetchResult result = companyFetcher.getCompany(userSeq);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(mockResult);
        verify(companyService).getCompanyByManagerUserSeq(userSeq);
    }

    @Test
    @DisplayName("존재하지 않는 회사를 조회하면 빈 결과를 반환한다")
    void shouldReturnEmptyResultWhenCompanyNotExists() {
        // given
        Long userSeq = 3L;
        CompanyFetchResult emptyResult = CompanyFetchResult.createEmpty();

        when(companyService.getCompanyByManagerUserSeq(userSeq)).thenReturn(emptyResult);

        // when
        CompanyFetchResult result = companyFetcher.getCompany(userSeq);

        // then
        assertThat(result).isEqualTo(emptyResult);
        verify(companyService).getCompanyByManagerUserSeq(userSeq);
    }

    @Test
    @DisplayName("다른 사용자 ID로 회사를 조회할 수 있다")
    void shouldGetCompanyWithDifferentUserSeq() {
        // given
        Long userSeq = 4L;
        CompanyFetchResult mockResult = CompanyFetchResult.createEmpty();

        when(companyService.getCompanyByManagerUserSeq(userSeq)).thenReturn(mockResult);

        // when
        CompanyFetchResult result = companyFetcher.getCompany(userSeq);

        // then
        assertThat(result).isEqualTo(mockResult);
        verify(companyService).getCompanyByManagerUserSeq(userSeq);
    }

    @Test
    @DisplayName("매니저가 변경된 회사를 조회할 수 있다")
    void shouldGetCompanyWithChangedManager() {
        // given
        Long newManagerUserSeq = 5L;
        CompanyFetchResult mockResult = CompanyFetchResult.createEmpty();

        when(companyService.getCompanyByManagerUserSeq(newManagerUserSeq)).thenReturn(mockResult);

        // when
        CompanyFetchResult result = companyFetcher.getCompany(newManagerUserSeq);

        // then
        assertThat(result).isEqualTo(mockResult);
        verify(companyService).getCompanyByManagerUserSeq(newManagerUserSeq);
    }

    @Test
    @DisplayName("여러 번 회사 정보를 조회할 수 있다")
    void shouldAllowMultipleQueries() {
        // given
        Long userSeq = 6L;
        CompanyFetchResult mockResult = CompanyFetchResult.createEmpty();

        when(companyService.getCompanyByManagerUserSeq(userSeq)).thenReturn(mockResult);

        // when
        CompanyFetchResult firstResult = companyFetcher.getCompany(userSeq);
        CompanyFetchResult secondResult = companyFetcher.getCompany(userSeq);

        // then
        assertThat(firstResult).isEqualTo(mockResult);
        assertThat(secondResult).isEqualTo(mockResult);
        assertThat(firstResult).isEqualTo(secondResult);
        verify(companyService, org.mockito.Mockito.times(2)).getCompanyByManagerUserSeq(userSeq);
    }

    @Test
    @DisplayName("서로 다른 사용자가 각각 회사 정보를 조회할 수 있다")
    void shouldAllowDifferentUsersToQueryCompanies() {
        // given
        Long userSeq1 = 7L;
        Long userSeq2 = 8L;
        CompanyFetchResult mockResult1 = CompanyFetchResult.createEmpty();
        CompanyFetchResult mockResult2 = CompanyFetchResult.createEmpty();

        when(companyService.getCompanyByManagerUserSeq(userSeq1)).thenReturn(mockResult1);
        when(companyService.getCompanyByManagerUserSeq(userSeq2)).thenReturn(mockResult2);

        // when
        CompanyFetchResult result1 = companyFetcher.getCompany(userSeq1);
        CompanyFetchResult result2 = companyFetcher.getCompany(userSeq2);

        // then
        assertThat(result1).isEqualTo(mockResult1);
        assertThat(result2).isEqualTo(mockResult2);
        verify(companyService).getCompanyByManagerUserSeq(userSeq1);
        verify(companyService).getCompanyByManagerUserSeq(userSeq2);
    }

    @Test
    @DisplayName("회사 조회 시 CompanyService가 올바르게 호출된다")
    void shouldCallCompanyServiceCorrectly() {
        // given
        Long userSeq = 9L;
        CompanyFetchResult mockResult = CompanyFetchResult.createEmpty();

        when(companyService.getCompanyByManagerUserSeq(userSeq)).thenReturn(mockResult);

        // when
        CompanyFetchResult result = companyFetcher.getCompany(userSeq);

        // then
        assertThat(result).isEqualTo(mockResult);
        verify(companyService).getCompanyByManagerUserSeq(userSeq);
    }

    @Test
    @DisplayName("조회 결과가 null이 아님을 확인한다")
    void shouldEnsureResultIsNotNull() {
        // given
        Long userSeq = 10L;
        CompanyFetchResult mockResult = CompanyFetchResult.createEmpty();

        when(companyService.getCompanyByManagerUserSeq(userSeq)).thenReturn(mockResult);

        // when
        CompanyFetchResult result = companyFetcher.getCompany(userSeq);

        // then
        assertThat(result).isNotNull();
        verify(companyService).getCompanyByManagerUserSeq(userSeq);
    }

    @Test
    @DisplayName("대용량 사용자 ID로도 회사 정보를 조회할 수 있다")
    void shouldGetCompanyWithLargeUserSeq() {
        // given
        Long largeUserSeq = 999999999L;
        CompanyFetchResult mockResult = CompanyFetchResult.createEmpty();

        when(companyService.getCompanyByManagerUserSeq(largeUserSeq)).thenReturn(mockResult);

        // when
        CompanyFetchResult result = companyFetcher.getCompany(largeUserSeq);

        // then
        assertThat(result).isEqualTo(mockResult);
        verify(companyService).getCompanyByManagerUserSeq(largeUserSeq);
    }
}