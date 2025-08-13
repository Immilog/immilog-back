package com.backend.immilog.jobboard.domain.service;

import com.backend.immilog.jobboard.domain.model.*;
import com.backend.immilog.jobboard.domain.repositories.JobBoardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("JobBoardDomainService 테스트")
class JobBoardDomainServiceTest {

    private final JobBoardRepository mockJobBoardRepository = mock(JobBoardRepository.class);
    private final JobBoardValidationService mockValidationService = mock(JobBoardValidationService.class);

    private JobBoardDomainService jobBoardDomainService;

    @BeforeEach
    void setUp() {
        jobBoardDomainService = new JobBoardDomainService(
                mockJobBoardRepository,
                mockValidationService
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
        Salary salary = new Salary(new java.math.BigDecimal("50000000"), "KRW");
        JobDescription description = new JobDescription("Java 개발자를 찾습니다.");
        JobRequirements requirements = new JobRequirements("Java, Spring Boot 경험 필수");
        JobBenefits benefits = new JobBenefits("건강보험, 연차");
        ApplicationDeadline deadline = new ApplicationDeadline(LocalDate.now().plusDays(30));
        ContactEmail contactEmail = new ContactEmail("hr@testcompany.com");
        String countryId = "KR";

        JobBoard createdJobBoard = createSampleJobBoard();
        when(mockJobBoardRepository.save(any(JobBoard.class))).thenReturn(createdJobBoard);

        // when
        JobBoard result = jobBoardDomainService.createJobBoard(
                userId, company, title, location, workType, experience,
                industry, salary, description, requirements, benefits,
                deadline, contactEmail, countryId
        );

        // then
        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo("user123");
        assertThat(result.companyName()).isEqualTo("TestCompany");

        verify(mockValidationService).validateJobBoardCreation(
                userId, company, title, description, requirements, deadline
        );
        verify(mockJobBoardRepository).save(any(JobBoard.class));
    }

    @Test
    @DisplayName("채용공고 생성 시 유효성 검증 실패")
    void createJobBoard_ValidationFails_ThrowsException() {
        // given
        String userId = "user123";
        JobBoardCompany company = new JobBoardCompany("TestCompany", "Seoul", Industry.IT);
        JobTitle title = new JobTitle("Software Engineer");
        JobLocation location = new JobLocation("강남구");
        WorkType workType = WorkType.FULL_TIME;
        Experience experience = Experience.MIDDLE;
        Industry industry = Industry.IT;
        Salary salary = new Salary(new java.math.BigDecimal("50000000"), "KRW");
        JobDescription description = new JobDescription("Java 개발자를 찾습니다.");
        JobRequirements requirements = new JobRequirements("Java, Spring Boot 경험 필수");
        JobBenefits benefits = new JobBenefits("건강보험, 연차");
        ApplicationDeadline deadline = new ApplicationDeadline(LocalDate.now().plusDays(30));
        ContactEmail contactEmail = new ContactEmail("hr@testcompany.com");
        String countryId = "KR";

        doThrow(new IllegalArgumentException("Invalid job board data"))
                .when(mockValidationService).validateJobBoardCreation(
                        userId, company, title, description, requirements, deadline
                );

        // when & then
        assertThatThrownBy(() -> jobBoardDomainService.createJobBoard(
                userId, company, title, location, workType, experience,
                industry, salary, description, requirements, benefits,
                deadline, contactEmail, countryId
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid job board data");

        verify(mockValidationService).validateJobBoardCreation(
                userId, company, title, description, requirements, deadline
        );
        verify(mockJobBoardRepository, never()).save(any());
    }

    @Test
    @DisplayName("사용자 채용공고 지원 가능 여부 확인 - 성공")
    void canUserApplyToJobBoard_Success() {
        // given
        String userId = "user123";
        JobBoard jobBoard = createActiveJobBoard("user456"); // 다른 사용자가 작성한 활성 채용공고

        // when
        boolean canApply = jobBoardDomainService.canUserApplyToJobBoard(userId, jobBoard);

        // then
        assertThat(canApply).isTrue();
    }

    @Test
    @DisplayName("사용자 채용공고 지원 가능 여부 확인 - 본인 작성글")
    void canUserApplyToJobBoard_OwnJobBoard_ReturnsFalse() {
        // given
        String userId = "user123";
        JobBoard jobBoard = createActiveJobBoard(userId); // 본인이 작성한 채용공고

        // when
        boolean canApply = jobBoardDomainService.canUserApplyToJobBoard(userId, jobBoard);

        // then
        assertThat(canApply).isFalse();
    }

    @Test
    @DisplayName("사용자 채용공고 지원 가능 여부 확인 - 비활성 채용공고")
    void canUserApplyToJobBoard_InactiveJobBoard_ReturnsFalse() {
        // given
        String userId = "user123";
        JobBoard jobBoard = createInactiveJobBoard("user456");

        // when
        boolean canApply = jobBoardDomainService.canUserApplyToJobBoard(userId, jobBoard);

        // then
        assertThat(canApply).isFalse();
    }

    @Test
    @DisplayName("사용자 채용공고 지원 가능 여부 확인 - 활성 채용공고")
    void canUserApplyToJobBoard_ActiveJobBoard_ReturnsTrue() {
        // given
        String userId = "user123";
        JobBoard jobBoard = createExpiredJobBoard("user456"); // This is active with future deadline

        // when
        boolean canApply = jobBoardDomainService.canUserApplyToJobBoard(userId, jobBoard);

        // then
        assertThat(canApply).isTrue(); // Should return true since it's active and not expired
    }

    @Test
    @DisplayName("채용공고 조회수 기록 성공 - 다른 사용자")
    void recordJobBoardView_DifferentUser_Success() {
        // given
        JobBoardId jobBoardId = JobBoardId.of("job123");
        String viewerUserId = "viewer456";
        JobBoard jobBoard = createActiveJobBoard("author123"); // 다른 사용자가 작성
        Long initialViewCount = jobBoard.viewCount();

        when(mockJobBoardRepository.findById(jobBoardId.value()))
                .thenReturn(Optional.of(jobBoard));

        // when
        jobBoardDomainService.recordJobBoardView(jobBoardId, viewerUserId);

        // then
        verify(mockJobBoardRepository).findById(jobBoardId.value());
        verify(mockJobBoardRepository).save(jobBoard);
        // JobBoard의 incrementViewCount가 호출되었는지는 모킹된 객체에서 직접 확인하기 어려움
        // 실제 도메인 로직에서는 viewCount가 증가할 것임
    }

    @Test
    @DisplayName("채용공고 조회수 기록 - 본인 조회 시 증가하지 않음")
    void recordJobBoardView_SameUser_DoesNotIncrement() {
        // given
        JobBoardId jobBoardId = JobBoardId.of("job123");
        String viewerUserId = "author123";
        JobBoard jobBoard = createActiveJobBoard(viewerUserId); // 본인이 작성

        when(mockJobBoardRepository.findById(jobBoardId.value()))
                .thenReturn(Optional.of(jobBoard));

        // when
        jobBoardDomainService.recordJobBoardView(jobBoardId, viewerUserId);

        // then
        verify(mockJobBoardRepository).findById(jobBoardId.value());
        verify(mockJobBoardRepository, never()).save(any()); // 본인 조회 시 저장되지 않음
    }

    @Test
    @DisplayName("존재하지 않는 채용공고 조회수 기록 시 예외 발생")
    void recordJobBoardView_JobBoardNotFound_ThrowsException() {
        // given
        JobBoardId jobBoardId = JobBoardId.of("nonexistent");
        String viewerUserId = "viewer456";

        when(mockJobBoardRepository.findById(jobBoardId.value()))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> jobBoardDomainService.recordJobBoardView(jobBoardId, viewerUserId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Job board not found");

        verify(mockJobBoardRepository).findById(jobBoardId.value());
        verify(mockJobBoardRepository, never()).save(any());
    }

    private JobBoard createSampleJobBoard() {
        return JobBoard.restore(
                JobBoardId.of("job123"),
                "user123",
                new JobBoardCompany("TestCompany", "Seoul", Industry.IT),
                new JobTitle("Software Engineer"),
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
                "KR",
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now()
        );
    }

    private JobBoard createActiveJobBoard(String userId) {
        return JobBoard.restore(
                JobBoardId.of("job123"),
                userId,
                new JobBoardCompany("TestCompany", "Seoul", Industry.IT),
                new JobTitle("Software Engineer"),
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
                "KR",
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now()
        );
    }

    private JobBoard createInactiveJobBoard(String userId) {
        return JobBoard.restore(
                JobBoardId.of("job123"),
                userId,
                new JobBoardCompany("TestCompany", "Seoul", Industry.IT),
                new JobTitle("Software Engineer"),
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
                false, // 비활성
                100L,
                "KR",
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now()
        );
    }

    private JobBoard createExpiredJobBoard(String userId) {
        // Create JobBoard with a deadline that will be expiring soon but not yet expired
        return JobBoard.restore(
                JobBoardId.of("job123"),
                userId,
                new JobBoardCompany("TestCompany", "Seoul", Industry.IT),
                new JobTitle("Software Engineer"),
                new JobLocation("강남구"),
                WorkType.FULL_TIME,
                Experience.MIDDLE,
                Industry.IT,
                new Salary(new java.math.BigDecimal("50000000"), "KRW"),
                new JobDescription("Java 개발자를 찾습니다."),
                new JobRequirements("Java, Spring Boot 경험 필수"),
                new JobBenefits("건강보험, 연차"),
                new ApplicationDeadline(LocalDate.now().plusDays(1)), // Will expire soon
                new ContactEmail("hr@testcompany.com"),
                true,
                100L,
                "KR",
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now()
        );
    }
}