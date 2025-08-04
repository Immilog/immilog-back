package com.backend.immilog.jobboard.application.usecase;

import com.backend.immilog.jobboard.application.dto.JobBoardResult;
import com.backend.immilog.jobboard.application.services.JobBoardQueryService;
import com.backend.immilog.jobboard.domain.model.Experience;
import com.backend.immilog.jobboard.domain.model.Industry;
import com.backend.immilog.shared.enums.Country;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@DisplayName("FetchJobBoardUseCase 테스트")
class FetchJobBoardUseCaseTest {

    private final JobBoardQueryService mockJobBoardQueryService = mock(JobBoardQueryService.class);

    private FetchJobBoardUseCase.FetcherJobBoard fetchJobBoardUseCase;

    @BeforeEach
    void setUp() {
        fetchJobBoardUseCase = new FetchJobBoardUseCase.FetcherJobBoard(mockJobBoardQueryService);
    }

    @Test
    @DisplayName("채용공고 목록 조회 성공 - 페이지 지정")
    void getJobBoards_WithPage_Success() {
        // given
        Country country = Country.SOUTH_KOREA;
        Integer page = 1;
        var pageable = PageRequest.of(page, 10);
        
        JobBoardResult jobBoardResult = createSampleJobBoardResult("job1", "Software Engineer");
        Page<JobBoardResult> expectedPage = new PageImpl<>(
                List.of(jobBoardResult), pageable, 1
        );

        when(mockJobBoardQueryService.getJobBoards(country, pageable))
                .thenReturn(expectedPage);

        // when
        Page<JobBoardResult> result = fetchJobBoardUseCase.getJobBoards(country, page);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).title()).isEqualTo("Software Engineer");
        assertThat(result.getNumber()).isEqualTo(1);
        assertThat(result.getSize()).isEqualTo(10);

        verify(mockJobBoardQueryService).getJobBoards(country, pageable);
    }

    @Test
    @DisplayName("채용공고 목록 조회 성공 - 페이지 null (기본값 0)")
    void getJobBoards_WithNullPage_DefaultsToZero() {
        // given
        Country country = Country.SOUTH_KOREA;
        Integer page = null;
        var pageable = PageRequest.of(0, 10);
        
        JobBoardResult jobBoardResult = createSampleJobBoardResult("job1", "DevOps Engineer");
        Page<JobBoardResult> expectedPage = new PageImpl<>(
                List.of(jobBoardResult), pageable, 1
        );

        when(mockJobBoardQueryService.getJobBoards(country, pageable))
                .thenReturn(expectedPage);

        // when
        Page<JobBoardResult> result = fetchJobBoardUseCase.getJobBoards(country, page);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getNumber()).isEqualTo(0);

        verify(mockJobBoardQueryService).getJobBoards(country, pageable);
    }

    @Test
    @DisplayName("채용공고 목록 조회 - 빈 결과")
    void getJobBoards_EmptyResult() {
        // given
        Country country = Country.SOUTH_KOREA;
        Integer page = 0;
        var pageable = PageRequest.of(0, 10);
        Page<JobBoardResult> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(mockJobBoardQueryService.getJobBoards(country, pageable))
                .thenReturn(emptyPage);

        // when
        Page<JobBoardResult> result = fetchJobBoardUseCase.getJobBoards(country, page);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);

        verify(mockJobBoardQueryService).getJobBoards(country, pageable);
    }

    @Test
    @DisplayName("채용공고 목록 조회 - null 국가")
    void getJobBoards_WithNullCountry() {
        // given
        Country country = null;
        Integer page = 0;
        var pageable = PageRequest.of(0, 10);
        Page<JobBoardResult> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(mockJobBoardQueryService.getJobBoards(null, pageable))
                .thenReturn(emptyPage);

        // when
        Page<JobBoardResult> result = fetchJobBoardUseCase.getJobBoards(country, page);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();

        verify(mockJobBoardQueryService).getJobBoards(null, pageable);
    }

    @Test
    @DisplayName("채용공고 상세 조회 성공")
    void getJobBoardDetail_Success() {
        // given
        String jobBoardId = "job123";
        JobBoardResult expectedResult = createSampleJobBoardResult(jobBoardId, "Senior Java Developer");

        when(mockJobBoardQueryService.getJobBoardDetail(jobBoardId))
                .thenReturn(expectedResult);

        // when
        JobBoardResult result = fetchJobBoardUseCase.getJobBoardDetail(jobBoardId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(jobBoardId);
        assertThat(result.title()).isEqualTo("Senior Java Developer");

        verify(mockJobBoardQueryService).getJobBoardDetail(jobBoardId);
    }

    @Test
    @DisplayName("다양한 페이지 크기로 채용공고 목록 조회")
    void getJobBoards_VerifyPageSize() {
        // given
        Country country = Country.SOUTH_KOREA;
        Integer page = 2;
        var expectedPageable = PageRequest.of(2, 10);

        when(mockJobBoardQueryService.getJobBoards(eq(country), any()))
                .thenReturn(new PageImpl<>(List.of(), expectedPageable, 0));

        // when
        fetchJobBoardUseCase.getJobBoards(country, page);

        // then
        verify(mockJobBoardQueryService).getJobBoards(country, expectedPageable);
    }

    private JobBoardResult createSampleJobBoardResult(String id, String title) {
        return new JobBoardResult(
                id,
                "user123",
                "TestCompany",
                "Seoul",
                title,
                "강남구",
                "FULL_TIME",
                Experience.JUNIOR,
                Industry.IT,
                new java.math.BigDecimal("60000000"),
                "KRW",
                "Java 개발자를 찾습니다.",
                "Java, Spring Boot 경험 필수",
                "건강보험, 연차",
                java.time.LocalDate.now().plusDays(30),
                "hr@testcompany.com",
                true,
                100L,
                com.backend.immilog.shared.enums.Country.SOUTH_KOREA,
                java.time.LocalDateTime.now(),
                null,
                true,
                false
        );
    }
}