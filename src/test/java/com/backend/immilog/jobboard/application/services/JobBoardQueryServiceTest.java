package com.backend.immilog.jobboard.application.services;

import com.backend.immilog.jobboard.application.dto.JobBoardResult;
import com.backend.immilog.jobboard.domain.model.*;
import com.backend.immilog.jobboard.domain.repositories.JobBoardRepository;
import com.backend.immilog.shared.enums.Country;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@DisplayName("JobBoardQueryService 테스트")
class JobBoardQueryServiceTest {

    private final JobBoardRepository mockJobBoardRepository = mock(JobBoardRepository.class);

    private JobBoardQueryService jobBoardQueryService;

    @BeforeEach
    void setUp() {
        jobBoardQueryService = new JobBoardQueryService(mockJobBoardRepository);
    }

    @Test
    @DisplayName("국가별 채용공고 목록 조회 성공")
    void getJobBoards_Success() {
        // given
        Country country = Country.SOUTH_KOREA;
        Pageable pageable = PageRequest.of(0, 10);
        
        List<JobBoard> jobBoards = List.of(
                createSampleJobBoard("job1", "Software Engineer"),
                createSampleJobBoard("job2", "DevOps Engineer")
        );
        Page<JobBoard> jobBoardPage = new PageImpl<>(jobBoards, pageable, 2);

        when(mockJobBoardRepository.findJobBoards(country, pageable))
                .thenReturn(jobBoardPage);

        // when
        Page<JobBoardResult> result = jobBoardQueryService.getJobBoards(country, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent().get(0).title()).isEqualTo("Software Engineer");
        assertThat(result.getContent().get(1).title()).isEqualTo("DevOps Engineer");

        verify(mockJobBoardRepository).findJobBoards(country, pageable);
    }

    @Test
    @DisplayName("빈 채용공고 목록 조회")
    void getJobBoards_EmptyResult() {
        // given
        Country country = Country.SOUTH_KOREA;
        Pageable pageable = PageRequest.of(0, 10);
        Page<JobBoard> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(mockJobBoardRepository.findJobBoards(country, pageable))
                .thenReturn(emptyPage);

        // when
        Page<JobBoardResult> result = jobBoardQueryService.getJobBoards(country, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);

        verify(mockJobBoardRepository).findJobBoards(country, pageable);
    }

    @Test
    @DisplayName("채용공고 상세 조회 성공")
    void getJobBoardDetail_Success() {
        // given
        String jobBoardId = "job123";
        JobBoard jobBoard = createSampleJobBoard(jobBoardId, "Senior Java Developer");

        when(mockJobBoardRepository.findById(jobBoardId))
                .thenReturn(Optional.of(jobBoard));

        // when
        JobBoardResult result = jobBoardQueryService.getJobBoardDetail(jobBoardId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(jobBoardId);
        assertThat(result.title()).isEqualTo("Senior Java Developer");
        assertThat(result.companyName()).isEqualTo("TestCompany");
        assertThat(result.isActive()).isTrue();

        verify(mockJobBoardRepository).findById(jobBoardId);
    }

    @Test
    @DisplayName("존재하지 않는 채용공고 상세 조회 시 예외 발생")
    void getJobBoardDetail_NotFound_ThrowsException() {
        // given
        String jobBoardId = "nonexistent";
        when(mockJobBoardRepository.findById(jobBoardId))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> jobBoardQueryService.getJobBoardDetail(jobBoardId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("JobBoard not found");

        verify(mockJobBoardRepository).findById(jobBoardId);
    }

    @Test
    @DisplayName("null 국가로 채용공고 조회")
    void getJobBoards_WithNullCountry() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<JobBoard> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(mockJobBoardRepository.findJobBoards(null, pageable))
                .thenReturn(emptyPage);

        // when
        Page<JobBoardResult> result = jobBoardQueryService.getJobBoards(null, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();

        verify(mockJobBoardRepository).findJobBoards(null, pageable);
    }

    private JobBoard createSampleJobBoard(String id, String titleStr) {
        return JobBoard.restore(
                JobBoardId.of(id),
                "user123",
                new JobBoardCompany("TestCompany", "Seoul", Industry.IT),
                new JobTitle(titleStr),
                new JobLocation("강남구"),
                WorkType.FULL_TIME,
                Experience.MIDDLE,
                Industry.IT,
                new Salary(new java.math.BigDecimal("50000000"), "KRW"),
                new JobDescription("Java 개발자를 찾습니다."),
                new JobRequirements("Java, Spring Boot 경험 필수"),
                new JobBenefits("건강보험, 연차"),
                new ApplicationDeadline(LocalDate.now().plusDays(30)),
                new ContactEmail("hr@testcompany.com"),
                true,
                100L,
                Country.SOUTH_KOREA,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}