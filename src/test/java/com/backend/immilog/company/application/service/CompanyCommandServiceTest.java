package com.backend.immilog.company.application.service;

import com.backend.immilog.company.domain.model.Company;
import com.backend.immilog.company.domain.model.CompanyManager;
import com.backend.immilog.company.domain.model.CompanyMetaData;
import com.backend.immilog.company.domain.model.Industry;
import com.backend.immilog.company.domain.repository.CompanyRepository;
import com.backend.immilog.shared.enums.Country;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("CompanyCommandService 애플리케이션 서비스 테스트")
class CompanyCommandServiceTest {

    private final CompanyRepository companyRepository = mock(CompanyRepository.class);
    private final CompanyCommandService companyCommandService = new CompanyCommandService(companyRepository);

    @Test
    @DisplayName("회사를 저장할 수 있다")
    void shouldSaveCompany() {
        // given
        CompanyManager manager = CompanyManager.of(Country.SOUTH_KOREA, "서울", "1");
        CompanyMetaData metaData = CompanyMetaData.of(Industry.IT, "테스트 회사", "test@company.com", "010-1234-5678", "서울시 강남구", "https://company.com", "logo.png");
        Company company = new Company(null, manager, metaData);
        Company savedCompany = new Company("1", manager, metaData);

        when(companyRepository.save(company)).thenReturn(savedCompany);

        // when
        Company result = companyCommandService.save(company);

        // then
        assertThat(result).isEqualTo(savedCompany);
        assertThat(result.id()).isEqualTo("1");
        verify(companyRepository).save(company);
    }

    @Test
    @DisplayName("기존 회사를 수정할 수 있다")
    void shouldUpdateExistingCompany() {
        // given
        CompanyManager manager = CompanyManager.of(Country.SOUTH_KOREA, "서울", "1");
        CompanyMetaData metaData = CompanyMetaData.of(Industry.IT, "기존 회사", "existing@company.com", "010-1234-5678", null, null, null);
        Company existingCompany = new Company("1", manager, metaData);

        CompanyMetaData updatedMetaData = CompanyMetaData.of(Industry.IT, "수정된 회사", "updated@company.com", "010-9876-5432", "부산시", "https://updated.com", "updated-logo.png");
        Company updatedCompany = new Company("1", manager, updatedMetaData);

        when(companyRepository.save(updatedCompany)).thenReturn(updatedCompany);

        // when
        Company result = companyCommandService.save(updatedCompany);

        // then
        assertThat(result).isEqualTo(updatedCompany);
        assertThat(result.name()).isEqualTo("수정된 회사");
        assertThat(result.email()).isEqualTo("updated@company.com");
        assertThat(result.phone()).isEqualTo("010-9876-5432");
        verify(companyRepository).save(updatedCompany);
    }

    @Test
    @DisplayName("회사를 삭제할 수 있다")
    void shouldDeleteCompany() {
        // given
        CompanyManager manager = CompanyManager.of(Country.SOUTH_KOREA, "서울", "1");
        CompanyMetaData metaData = CompanyMetaData.of(Industry.IT, "삭제할 회사", "delete@company.com", "010-1234-5678", null, null, null);
        Company company = new Company("1", manager, metaData);

        // when
        companyCommandService.delete(company);

        // then
        verify(companyRepository).delete(company);
    }

    @Test
    @DisplayName("새로운 회사 저장 시 repository의 save 메서드가 호출된다")
    void shouldCallRepositorySaveWhenSavingNewCompany() {
        // given
        CompanyManager manager = CompanyManager.of(Country.JAPAN, "도쿄", "2");
        CompanyMetaData metaData = CompanyMetaData.of(Industry.ETC, "새로운 회사", "new@company.com", "090-1234-5678", "도쿄시", "https://new.com", "new-logo.png");
        Company newCompany = new Company(null, manager, metaData);
        Company savedCompany = new Company("2", manager, metaData);

        when(companyRepository.save(newCompany)).thenReturn(savedCompany);

        // when
        Company result = companyCommandService.save(newCompany);

        // then
        assertThat(result.id()).isEqualTo("2");
        assertThat(result.name()).isEqualTo("새로운 회사");
        assertThat(result.country()).isEqualTo(Country.JAPAN);
        verify(companyRepository).save(newCompany);
    }

    @Test
    @DisplayName("회사 삭제 시 repository의 delete 메서드가 호출된다")
    void shouldCallRepositoryDeleteWhenDeletingCompany() {
        // given
        CompanyManager manager = CompanyManager.of(Country.SOUTH_KOREA, "부산", "3");
        CompanyMetaData metaData = CompanyMetaData.of(Industry.IT, "삭제 대상 회사", "target@company.com", "051-1234-5678", "부산시", null, null);
        Company companyToDelete = new Company("3", manager, metaData);

        // when
        companyCommandService.delete(companyToDelete);

        // then
        verify(companyRepository).delete(companyToDelete);
    }

    @Test
    @DisplayName("저장된 회사의 모든 정보가 올바르게 반환된다")
    void shouldReturnCorrectCompanyInformationAfterSave() {
        // given
        CompanyManager manager = CompanyManager.of(Country.SOUTH_KOREA, "대구", "4");
        CompanyMetaData metaData = CompanyMetaData.of(Industry.IT, "완전한 정보 회사", "complete@company.com", "053-1234-5678", "대구시 중구", "https://complete.com", "complete-logo.png");
        Company company = new Company(null, manager, metaData);
        Company savedCompany = new Company("4", manager, metaData);

        when(companyRepository.save(company)).thenReturn(savedCompany);

        // when
        Company result = companyCommandService.save(company);

        // then
        assertThat(result.id()).isEqualTo("4");
        assertThat(result.name()).isEqualTo("완전한 정보 회사");
        assertThat(result.email()).isEqualTo("complete@company.com");
        assertThat(result.phone()).isEqualTo("053-1234-5678");
        assertThat(result.address()).isEqualTo("대구시 중구");
        assertThat(result.homepage()).isEqualTo("https://complete.com");
        assertThat(result.logo()).isEqualTo("complete-logo.png");
        assertThat(result.industry()).isEqualTo(Industry.IT);
        assertThat(result.country()).isEqualTo(Country.SOUTH_KOREA);
        assertThat(result.region()).isEqualTo("대구");
        assertThat(result.managerUserId()).isEqualTo("4");
    }
}