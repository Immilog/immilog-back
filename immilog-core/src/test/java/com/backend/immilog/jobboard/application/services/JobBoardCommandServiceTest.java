package com.backend.immilog.jobboard.application.services;

import com.backend.immilog.jobboard.domain.model.*;
import com.backend.immilog.jobboard.domain.repositories.JobBoardRepository;
import com.backend.immilog.jobboard.domain.service.JobBoardDomainService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("JobBoardCommandService 테스트")
class JobBoardCommandServiceTest {

    private final JobBoardRepository mockJobBoardRepository = mock(JobBoardRepository.class);
    private final JobBoardDomainService mockJobBoardDomainService = mock(JobBoardDomainService.class);

    private JobBoardCommandService jobBoardCommandService;

    @BeforeEach
    void setUp() {
        jobBoardCommandService = new JobBoardCommandService(
                mockJobBoardRepository,
                mockJobBoardDomainService
        );
    }

    @Test
    @DisplayName("채용공고 생성 성공")
    void createJobBoard_Success() {
        // given
        String userId = "user123";
        JobBoardCompany company = new JobBoardCompany("TestCompany", "Seoul", Industry.IT);
        JobTitle title = new JobTitle("Software Engineer");
        JobLocation location = new JobLocation("강남구");
        WorkType workType = WorkType.FULL_TIME;
        Experience experience = Experience.MIDDLE;
        Industry industry = Industry.IT;
        Salary salary = new Salary(new BigDecimal("50000000"), "KRW");
        JobDescription description = new JobDescription("Java 개발자를 찾습니다.");
        JobRequirements requirements = new JobRequirements("Java, Spring Boot 경험 필수");
        JobBenefits benefits = new JobBenefits("건강보험, 연차");
        ApplicationDeadline deadline = new ApplicationDeadline(LocalDate.now().plusDays(30));
        ContactEmail contactEmail = new ContactEmail("hr@testcompany.com");
        String countryId = "KR";

        JobBoard expectedJobBoard = JobBoard.create(
                userId, company, title, location, workType, experience,
                industry, salary, description, requirements, benefits,
                deadline, contactEmail, countryId
        );

        when(mockJobBoardDomainService.createJobBoard(
                userId, company, title, location, workType, experience,
                industry, salary, description, requirements, benefits,
                deadline, contactEmail, countryId
        )).thenReturn(expectedJobBoard);

        // when
        JobBoard result = jobBoardCommandService.createJobBoard(
                userId, company, title, location, workType, experience,
                industry, salary, description, requirements, benefits,
                deadline, contactEmail, countryId
        );

        // then
        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.companyName()).isEqualTo("TestCompany");
        verify(mockJobBoardDomainService).createJobBoard(
                userId, company, title, location, workType, experience,
                industry, salary, description, requirements, benefits,
                deadline, contactEmail, countryId
        );
    }

    @Test
    @DisplayName("채용공고 비활성화 성공")
    void deactivateJobBoard_Success() {
        // given
        String jobBoardId = "job123";
        JobBoard mockJobBoard = createMockJobBoard(jobBoardId, true);
        
        when(mockJobBoardRepository.findById(jobBoardId)).thenReturn(Optional.of(mockJobBoard));

        // when
        jobBoardCommandService.deactivateJobBoard(jobBoardId);

        // then
        verify(mockJobBoardRepository).findById(jobBoardId);
        verify(mockJobBoard).deactivate();
        verify(mockJobBoardRepository).save(mockJobBoard);
    }

    @Test
    @DisplayName("존재하지 않는 채용공고 비활성화 시 예외 발생")
    void deactivateJobBoard_NotFound_ThrowsException() {
        // given
        String jobBoardId = "nonexistent";
        when(mockJobBoardRepository.findById(jobBoardId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> jobBoardCommandService.deactivateJobBoard(jobBoardId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("JobBoard not found");

        verify(mockJobBoardRepository).findById(jobBoardId);
        verify(mockJobBoardRepository, never()).save(any());
    }

    @Test
    @DisplayName("채용공고 활성화 성공")
    void activateJobBoard_Success() {
        // given
        String jobBoardId = "job123";
        JobBoard mockJobBoard = createMockJobBoard(jobBoardId, false);
        
        when(mockJobBoardRepository.findById(jobBoardId)).thenReturn(Optional.of(mockJobBoard));

        // when
        jobBoardCommandService.activateJobBoard(jobBoardId);

        // then
        verify(mockJobBoardRepository).findById(jobBoardId);
        verify(mockJobBoard).activate();
        verify(mockJobBoardRepository).save(mockJobBoard);
    }

    @Test
    @DisplayName("존재하지 않는 채용공고 활성화 시 예외 발생")
    void activateJobBoard_NotFound_ThrowsException() {
        // given
        String jobBoardId = "nonexistent";
        when(mockJobBoardRepository.findById(jobBoardId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> jobBoardCommandService.activateJobBoard(jobBoardId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("JobBoard not found");

        verify(mockJobBoardRepository).findById(jobBoardId);
        verify(mockJobBoardRepository, never()).save(any());
    }

    @Test
    @DisplayName("채용공고 조회수 증가 성공")
    void recordJobBoardView_Success() {
        // given
        String jobBoardId = "job123";
        String viewerUserId = "viewer456";
        JobBoardId jobBoardIdObj = JobBoardId.of(jobBoardId);

        // when
        jobBoardCommandService.recordJobBoardView(jobBoardId, viewerUserId);

        // then
        verify(mockJobBoardDomainService).recordJobBoardView(jobBoardIdObj, viewerUserId);
    }

    @Test
    @DisplayName("채용공고 삭제 성공")
    void deleteJobBoard_Success() {
        // given
        String jobBoardId = "job123";

        // when
        jobBoardCommandService.deleteJobBoard(jobBoardId);

        // then
        verify(mockJobBoardRepository).deleteById(jobBoardId);
    }

    private JobBoard createMockJobBoard(String jobBoardId, boolean isActive) {
        JobBoard mockJobBoard = mock(JobBoard.class);
        when(mockJobBoard.id()).thenReturn(JobBoardId.of(jobBoardId));
        when(mockJobBoard.isActive()).thenReturn(isActive);
        return mockJobBoard;
    }
}