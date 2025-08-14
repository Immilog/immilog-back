package com.backend.immilog.company.application.usecase;

import com.backend.immilog.company.application.dto.CompanyFetchResult;
import com.backend.immilog.company.application.service.CompanyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("GetCompanyUseCase 유즈케이스 테스트")
class GetCompanyUseCaseTest {

    private final CompanyService companyService = mock(CompanyService.class);
    private final GetCompanyUseCase.CompanyFetcher companyFetcher = new GetCompanyUseCase.CompanyFetcher(companyService);

    @Test
    @DisplayName("매니저 사용자 ID로 회사 정보를 조회할 수 있다")
    void shouldGetCompanyByManagerUserId() {
        // given
        String userId = "1";
        CompanyFetchResult expectedResult = CompanyFetchResult.createEmpty();

        when(companyService.getCompanyByManagerUserId(userId)).thenReturn(expectedResult);

        // when
        CompanyFetchResult result = companyFetcher.getCompany(userId);

        // then
        assertThat(result).isEqualTo(expectedResult);
        verify(companyService).getCompanyByManagerUserId(userId);
    }

    @Test
    @DisplayName("존재하는 회사 정보를 조회할 수 있다")
    void shouldGetExistingCompany() {
        // given
        String userId = "2";
        CompanyFetchResult mockResult = CompanyFetchResult.createEmpty(); // Mock result for existing company

        when(companyService.getCompanyByManagerUserId(userId)).thenReturn(mockResult);

        // when
        CompanyFetchResult result = companyFetcher.getCompany(userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(mockResult);
        verify(companyService).getCompanyByManagerUserId(userId);
    }

    @Test
    @DisplayName("존재하지 않는 회사를 조회하면 빈 결과를 반환한다")
    void shouldReturnEmptyResultWhenCompanyNotExists() {
        // given
        String userId = "3";
        CompanyFetchResult emptyResult = CompanyFetchResult.createEmpty();

        when(companyService.getCompanyByManagerUserId(userId)).thenReturn(emptyResult);

        // when
        CompanyFetchResult result = companyFetcher.getCompany(userId);

        // then
        assertThat(result).isEqualTo(emptyResult);
        verify(companyService).getCompanyByManagerUserId(userId);
    }

    @Test
    @DisplayName("다른 사용자 ID로 회사를 조회할 수 있다")
    void shouldGetCompanyWithDifferentUserId() {
        // given
        String userId = "4";
        CompanyFetchResult mockResult = CompanyFetchResult.createEmpty();

        when(companyService.getCompanyByManagerUserId(userId)).thenReturn(mockResult);

        // when
        CompanyFetchResult result = companyFetcher.getCompany(userId);

        // then
        assertThat(result).isEqualTo(mockResult);
        verify(companyService).getCompanyByManagerUserId(userId);
    }

    @Test
    @DisplayName("매니저가 변경된 회사를 조회할 수 있다")
    void shouldGetCompanyWithChangedManager() {
        // given
        String newManagerUserId = "5";
        CompanyFetchResult mockResult = CompanyFetchResult.createEmpty();

        when(companyService.getCompanyByManagerUserId(newManagerUserId)).thenReturn(mockResult);

        // when
        CompanyFetchResult result = companyFetcher.getCompany(newManagerUserId);

        // then
        assertThat(result).isEqualTo(mockResult);
        verify(companyService).getCompanyByManagerUserId(newManagerUserId);
    }

    @Test
    @DisplayName("여러 번 회사 정보를 조회할 수 있다")
    void shouldAllowMultipleQueries() {
        // given
        String userId = "6";
        CompanyFetchResult mockResult = CompanyFetchResult.createEmpty();

        when(companyService.getCompanyByManagerUserId(userId)).thenReturn(mockResult);

        // when
        CompanyFetchResult firstResult = companyFetcher.getCompany(userId);
        CompanyFetchResult secondResult = companyFetcher.getCompany(userId);

        // then
        assertThat(firstResult).isEqualTo(mockResult);
        assertThat(secondResult).isEqualTo(mockResult);
        assertThat(firstResult).isEqualTo(secondResult);
        verify(companyService, org.mockito.Mockito.times(2)).getCompanyByManagerUserId(userId);
    }

    @Test
    @DisplayName("서로 다른 사용자가 각각 회사 정보를 조회할 수 있다")
    void shouldAllowDifferentUsersToQueryCompanies() {
        // given
        String userId1 = "7";
        String userId2 = "8";
        CompanyFetchResult mockResult1 = CompanyFetchResult.createEmpty();
        CompanyFetchResult mockResult2 = CompanyFetchResult.createEmpty();

        when(companyService.getCompanyByManagerUserId(userId1)).thenReturn(mockResult1);
        when(companyService.getCompanyByManagerUserId(userId2)).thenReturn(mockResult2);

        // when
        CompanyFetchResult result1 = companyFetcher.getCompany(userId1);
        CompanyFetchResult result2 = companyFetcher.getCompany(userId2);

        // then
        assertThat(result1).isEqualTo(mockResult1);
        assertThat(result2).isEqualTo(mockResult2);
        verify(companyService).getCompanyByManagerUserId(userId1);
        verify(companyService).getCompanyByManagerUserId(userId2);
    }

    @Test
    @DisplayName("회사 조회 시 CompanyService가 올바르게 호출된다")
    void shouldCallCompanyServiceCorrectly() {
        // given
        String userId = "9";
        CompanyFetchResult mockResult = CompanyFetchResult.createEmpty();

        when(companyService.getCompanyByManagerUserId(userId)).thenReturn(mockResult);

        // when
        CompanyFetchResult result = companyFetcher.getCompany(userId);

        // then
        assertThat(result).isEqualTo(mockResult);
        verify(companyService).getCompanyByManagerUserId(userId);
    }

    @Test
    @DisplayName("조회 결과가 null이 아님을 확인한다")
    void shouldEnsureResultIsNotNull() {
        // given
        String userId = "10";
        CompanyFetchResult mockResult = CompanyFetchResult.createEmpty();

        when(companyService.getCompanyByManagerUserId(userId)).thenReturn(mockResult);

        // when
        CompanyFetchResult result = companyFetcher.getCompany(userId);

        // then
        assertThat(result).isNotNull();
        verify(companyService).getCompanyByManagerUserId(userId);
    }

    @Test
    @DisplayName("대용량 사용자 ID로도 회사 정보를 조회할 수 있다")
    void shouldGetCompanyWithLargeUserId() {
        // given
        String largeUserId = "999999999";
        CompanyFetchResult mockResult = CompanyFetchResult.createEmpty();

        when(companyService.getCompanyByManagerUserId(largeUserId)).thenReturn(mockResult);

        // when
        CompanyFetchResult result = companyFetcher.getCompany(largeUserId);

        // then
        assertThat(result).isEqualTo(mockResult);
        verify(companyService).getCompanyByManagerUserId(largeUserId);
    }
}