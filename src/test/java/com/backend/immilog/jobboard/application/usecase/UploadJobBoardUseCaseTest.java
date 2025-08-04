package com.backend.immilog.jobboard.application.usecase;

import com.backend.immilog.jobboard.application.dto.JobBoardResult;
import com.backend.immilog.jobboard.application.dto.JobBoardUploadCommand;
import com.backend.immilog.jobboard.application.services.JobBoardCommandService;
import com.backend.immilog.jobboard.domain.model.*;
import com.backend.immilog.shared.enums.Country;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("UploadJobBoardUseCase 테스트")
class UploadJobBoardUseCaseTest {

    private final JobBoardCommandService mockJobBoardCommandService = mock(JobBoardCommandService.class);

    private UploadJobBoardUseCase.UploaderJobBoard uploadJobBoardUseCase;

    @BeforeEach
    void setUp() {
        uploadJobBoardUseCase = new UploadJobBoardUseCase.UploaderJobBoard(mockJobBoardCommandService);
    }

    @Test
    @DisplayName("채용공고 업로드 성공")
    void uploadJobBoard_Success() {
        // given
        JobBoardUploadCommand command = createSampleUploadCommand();
        JobBoard savedJobBoard = createSampleJobBoard();

        when(mockJobBoardCommandService.createJobBoard(
                any(String.class),
                any(JobBoardCompany.class),
                any(JobTitle.class),
                any(JobLocation.class),
                any(WorkType.class),
                any(Experience.class),
                any(Industry.class),
                any(Salary.class),
                any(JobDescription.class),
                any(JobRequirements.class),
                any(JobBenefits.class),
                any(ApplicationDeadline.class),
                any(ContactEmail.class),
                any(Country.class)
        )).thenReturn(savedJobBoard);

        // when
        JobBoardResult result = uploadJobBoardUseCase.uploadJobBoard(command);

        // then
        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo("user123");
        assertThat(result.companyName()).isEqualTo("TestCompany");
        assertThat(result.title()).isEqualTo("Software Engineer");

        verify(mockJobBoardCommandService).createJobBoard(
                command.userId(),
                command.toJobBoardCompany(),
                command.toJobTitle(),
                command.toJobLocation(),
                command.workType(),
                command.experience(),
                command.industry(),
                command.toSalary(),
                command.toJobDescription(),
                command.toJobRequirements(),
                command.toJobBenefits(),
                command.toApplicationDeadline(),
                command.toContactEmail(),
                command.country()
        );
    }

    @Test
    @DisplayName("채용공고 업로드 - 최소 필수 정보만 포함")
    void uploadJobBoard_MinimalInfo_Success() {
        // given
        JobBoardUploadCommand minimalCommand = createMinimalUploadCommand();
        JobBoard savedJobBoard = createSampleJobBoard();

        when(mockJobBoardCommandService.createJobBoard(
                any(String.class),
                any(JobBoardCompany.class),
                any(JobTitle.class),
                any(JobLocation.class),
                any(WorkType.class),
                any(Experience.class),
                any(Industry.class),
                any(Salary.class),
                any(JobDescription.class),
                any(JobRequirements.class),
                any(JobBenefits.class),
                any(ApplicationDeadline.class),
                any(ContactEmail.class),
                any(Country.class)
        )).thenReturn(savedJobBoard);

        // when
        JobBoardResult result = uploadJobBoardUseCase.uploadJobBoard(minimalCommand);

        // then
        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo("user123");

        verify(mockJobBoardCommandService).createJobBoard(
                eq(minimalCommand.userId()),
                any(JobBoardCompany.class),
                any(JobTitle.class),
                any(JobLocation.class),
                any(WorkType.class),
                any(Experience.class),
                any(Industry.class),
                any(Salary.class),
                any(JobDescription.class),
                any(JobRequirements.class),
                any(JobBenefits.class),
                any(ApplicationDeadline.class),
                any(ContactEmail.class),
                any(Country.class)
        );
    }

    @Test
    @DisplayName("채용공고 업로드 - 다양한 WorkType으로 테스트")
    void uploadJobBoard_DifferentWorkTypes() {
        // given
        JobBoardUploadCommand command = createSampleUploadCommandWithWorkType(WorkType.PART_TIME);
        JobBoard savedJobBoard = createSampleJobBoard();

        when(mockJobBoardCommandService.createJobBoard(
                any(String.class),
                any(JobBoardCompany.class),
                any(JobTitle.class),
                any(JobLocation.class),
                any(WorkType.class),
                any(Experience.class),
                any(Industry.class),
                any(Salary.class),
                any(JobDescription.class),
                any(JobRequirements.class),
                any(JobBenefits.class),
                any(ApplicationDeadline.class),
                any(ContactEmail.class),
                any(Country.class)
        )).thenReturn(savedJobBoard);

        // when
        JobBoardResult result = uploadJobBoardUseCase.uploadJobBoard(command);

        // then
        assertThat(result).isNotNull();
        verify(mockJobBoardCommandService).createJobBoard(
                eq("user123"),
                any(JobBoardCompany.class),
                any(JobTitle.class),
                any(JobLocation.class),
                eq(WorkType.PART_TIME),
                any(Experience.class),
                any(Industry.class),
                any(Salary.class),
                any(JobDescription.class),
                any(JobRequirements.class),
                any(JobBenefits.class),
                any(ApplicationDeadline.class),
                any(ContactEmail.class),
                eq(Country.SOUTH_KOREA)
        );
    }

    private JobBoardUploadCommand createSampleUploadCommand() {
        return new JobBoardUploadCommand(
                "user123",
                "TestCompany",
                "Seoul",
                "Software Engineer",
                "강남구",
                WorkType.FULL_TIME,
                Experience.MIDDLE,
                new BigDecimal("50000000"),
                "KRW",
                "Java 개발자를 찾습니다.",
                "Java, Spring Boot 경험 필수",
                "건강보험, 연차",
                LocalDate.now().plusDays(30),
                "hr@testcompany.com",
                Country.SOUTH_KOREA,
                Industry.IT
        );
    }

    private JobBoardUploadCommand createMinimalUploadCommand() {
        return new JobBoardUploadCommand(
                "user123",
                "StartupCompany",
                "Busan",
                "Junior Developer",
                "해운대구",
                WorkType.FULL_TIME,
                Experience.JUNIOR,
                new BigDecimal("35000000"),
                "KRW",
                "신입 개발자 모집",
                "컴퓨터 관련 전공",
                "교육지원",
                LocalDate.now().plusDays(20),
                "recruit@startup.com",
                Country.SOUTH_KOREA,
                Industry.IT
        );
    }

    private JobBoardUploadCommand createSampleUploadCommandWithWorkType(WorkType workType) {
        return new JobBoardUploadCommand(
                "user123",
                "TestCompany",
                "Seoul",
                "Part-time Developer",
                "강남구",
                workType,
                Experience.JUNIOR,
                new BigDecimal("30000000"),
                "KRW",
                "파트타임 개발자를 찾습니다.",
                "Java 기초 경험",
                "유연근무",
                LocalDate.now().plusDays(15),
                "hr@testcompany.com",
                Country.SOUTH_KOREA,
                Industry.IT
        );
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
                new Salary(new BigDecimal("50000000"), "KRW"),
                new JobDescription("Java 개발자를 찾습니다."),
                new JobRequirements("Java, Spring Boot 경험 필수"),
                new JobBenefits("건강보험, 연차"),
                new ApplicationDeadline(LocalDate.now().plusDays(30)),
                new ContactEmail("hr@testcompany.com"),
                true,
                0L,
                Country.SOUTH_KOREA,
                LocalDateTime.now(),
                null
        );
    }
}