package com.backend.immilog.jobboard.domain.service;

import com.backend.immilog.jobboard.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("JobBoardValidationService 테스트")
class JobBoardValidationServiceTest {

    private JobBoardValidationService jobBoardValidationService;

    @BeforeEach
    void setUp() {
        jobBoardValidationService = new JobBoardValidationService();
    }

    @Test
    @DisplayName("채용공고 생성 유효성 검증 성공")
    void validateJobBoardCreation_Success() {
        // given
        String userId = "user123";
        JobBoardCompany company = new JobBoardCompany("TestCompany", "Seoul", Industry.IT);
        JobTitle title = new JobTitle("Software Engineer");
        JobDescription description = new JobDescription("Java 개발자를 찾습니다.");
        JobRequirements requirements = new JobRequirements("Java, Spring Boot 경험 필수");
        ApplicationDeadline deadline = new ApplicationDeadline(LocalDate.now().plusDays(30));

        // when & then
        assertDoesNotThrow(() -> jobBoardValidationService.validateJobBoardCreation(
                userId, company, title, description, requirements, deadline
        ));
    }

    @Test
    @DisplayName("채용공고 생성 유효성 검증 실패 - null userId")
    void validateJobBoardCreation_NullUserId_ThrowsException() {
        // given
        String userId = null;
        JobBoardCompany company = new JobBoardCompany("TestCompany", "Seoul", Industry.IT);
        JobTitle title = new JobTitle("Software Engineer");
        JobDescription description = new JobDescription("Java 개발자를 찾습니다.");
        JobRequirements requirements = new JobRequirements("Java, Spring Boot 경험 필수");
        ApplicationDeadline deadline = new ApplicationDeadline(LocalDate.now().plusDays(30));

        // when & then
        assertThatThrownBy(() -> jobBoardValidationService.validateJobBoardCreation(
                userId, company, title, description, requirements, deadline
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User ID cannot be null or empty");
    }

    @Test
    @DisplayName("채용공고 생성 유효성 검증 실패 - 빈 userId")
    void validateJobBoardCreation_EmptyUserId_ThrowsException() {
        // given
        String userId = "   ";
        JobBoardCompany company = new JobBoardCompany("TestCompany", "Seoul", Industry.IT);
        JobTitle title = new JobTitle("Software Engineer");
        JobDescription description = new JobDescription("Java 개발자를 찾습니다.");
        JobRequirements requirements = new JobRequirements("Java, Spring Boot 경험 필수");
        ApplicationDeadline deadline = new ApplicationDeadline(LocalDate.now().plusDays(30));

        // when & then
        assertThatThrownBy(() -> jobBoardValidationService.validateJobBoardCreation(
                userId, company, title, description, requirements, deadline
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User ID cannot be null or empty");
    }

    @Test
    @DisplayName("채용공고 생성 유효성 검증 실패 - null company")
    void validateJobBoardCreation_NullCompany_ThrowsException() {
        // given
        String userId = "user123";
        JobBoardCompany company = null;
        JobTitle title = new JobTitle("Software Engineer");
        JobDescription description = new JobDescription("Java 개발자를 찾습니다.");
        JobRequirements requirements = new JobRequirements("Java, Spring Boot 경험 필수");
        ApplicationDeadline deadline = new ApplicationDeadline(LocalDate.now().plusDays(30));

        // when & then
        assertThatThrownBy(() -> jobBoardValidationService.validateJobBoardCreation(
                userId, company, title, description, requirements, deadline
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Company information is required");
    }

    @Test
    @DisplayName("채용공고 생성 유효성 검증 실패 - null title")
    void validateJobBoardCreation_NullTitle_ThrowsException() {
        // given
        String userId = "user123";
        JobBoardCompany company = new JobBoardCompany("TestCompany", "Seoul", Industry.IT);
        JobTitle title = null;
        JobDescription description = new JobDescription("Java 개발자를 찾습니다.");
        JobRequirements requirements = new JobRequirements("Java, Spring Boot 경험 필수");
        ApplicationDeadline deadline = new ApplicationDeadline(LocalDate.now().plusDays(30));

        // when & then
        assertThatThrownBy(() -> jobBoardValidationService.validateJobBoardCreation(
                userId, company, title, description, requirements, deadline
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Job title is required");
    }

    @Test
    @DisplayName("채용공고 생성 유효성 검증 실패 - null description")
    void validateJobBoardCreation_NullDescription_ThrowsException() {
        // given
        String userId = "user123";
        JobBoardCompany company = new JobBoardCompany("TestCompany", "Seoul", Industry.IT);
        JobTitle title = new JobTitle("Software Engineer");
        JobDescription description = null;
        JobRequirements requirements = new JobRequirements("Java, Spring Boot 경험 필수");
        ApplicationDeadline deadline = new ApplicationDeadline(LocalDate.now().plusDays(30));

        // when & then
        assertThatThrownBy(() -> jobBoardValidationService.validateJobBoardCreation(
                userId, company, title, description, requirements, deadline
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Job description is required");
    }

    @Test
    @DisplayName("채용공고 생성 유효성 검증 실패 - null requirements")
    void validateJobBoardCreation_NullRequirements_ThrowsException() {
        // given
        String userId = "user123";
        JobBoardCompany company = new JobBoardCompany("TestCompany", "Seoul", Industry.IT);
        JobTitle title = new JobTitle("Software Engineer");
        JobDescription description = new JobDescription("Java 개발자를 찾습니다.");
        JobRequirements requirements = null;
        ApplicationDeadline deadline = new ApplicationDeadline(LocalDate.now().plusDays(30));

        // when & then
        assertThatThrownBy(() -> jobBoardValidationService.validateJobBoardCreation(
                userId, company, title, description, requirements, deadline
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Job requirements are required");
    }

    @Test
    @DisplayName("채용공고 생성 유효성 검증 실패 - null deadline")
    void validateJobBoardCreation_NullDeadline_ThrowsException() {
        // given
        String userId = "user123";
        JobBoardCompany company = new JobBoardCompany("TestCompany", "Seoul", Industry.IT);
        JobTitle title = new JobTitle("Software Engineer");
        JobDescription description = new JobDescription("Java 개발자를 찾습니다.");
        JobRequirements requirements = new JobRequirements("Java, Spring Boot 경험 필수");
        ApplicationDeadline deadline = null;

        // when & then
        assertThatThrownBy(() -> jobBoardValidationService.validateJobBoardCreation(
                userId, company, title, description, requirements, deadline
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Application deadline is required");
    }

    @Test
    @DisplayName("채용공고 생성 유효성 검증 실패 - 만료된 deadline")
    void validateJobBoardCreation_ExpiredDeadline_ThrowsException() {
        // given
        String userId = "user123";
        JobBoardCompany company = new JobBoardCompany("TestCompany", "Seoul", Industry.IT);
        JobTitle title = new JobTitle("Software Engineer");
        JobDescription description = new JobDescription("Java 개발자를 찾습니다.");
        JobRequirements requirements = new JobRequirements("Java, Spring Boot 경험 필수");

        // when & then - The exception should be thrown when creating ApplicationDeadline with past date
        assertThatThrownBy(() -> {
            ApplicationDeadline deadline = new ApplicationDeadline(LocalDate.now().minusDays(1)); // 과거 날짜
            jobBoardValidationService.validateJobBoardCreation(
                    userId, company, title, description, requirements, deadline
            );
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Application deadline cannot be in the past");
    }

    @Test
    @DisplayName("채용공고 수정 유효성 검증 성공")
    void validateJobBoardUpdate_Success() {
        // given
        JobBoard activeJobBoard = createActiveJobBoard();
        JobTitle newTitle = new JobTitle("Senior Software Engineer");
        JobDescription newDescription = new JobDescription("시니어 Java 개발자를 찾습니다.");
        JobRequirements newRequirements = new JobRequirements("Java, Spring Boot, AWS 경험 필수");

        // when & then
        assertDoesNotThrow(() -> jobBoardValidationService.validateJobBoardUpdate(
                activeJobBoard, newTitle, newDescription, newRequirements
        ));
    }

    @Test
    @DisplayName("채용공고 수정 유효성 검증 실패 - 비활성 채용공고")
    void validateJobBoardUpdate_InactiveJobBoard_ThrowsException() {
        // given
        JobBoard inactiveJobBoard = createInactiveJobBoard();
        JobTitle newTitle = new JobTitle("Senior Software Engineer");
        JobDescription newDescription = new JobDescription("시니어 Java 개발자를 찾습니다.");
        JobRequirements newRequirements = new JobRequirements("Java, Spring Boot, AWS 경험 필수");

        // when & then
        assertThatThrownBy(() -> jobBoardValidationService.validateJobBoardUpdate(
                inactiveJobBoard, newTitle, newDescription, newRequirements
        )).isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot update inactive job board");
    }

    @Test
    @DisplayName("채용공고 수정 유효성 검증 실패 - 만료된 채용공고")
    void validateJobBoardUpdate_ExpiredJobBoard_ThrowsException() {
        // given
        JobBoard expiredJobBoard = createExpiredJobBoard();
        JobTitle newTitle = new JobTitle("Senior Software Engineer");
        JobDescription newDescription = new JobDescription("시니어 Java 개발자를 찾습니다.");
        JobRequirements newRequirements = new JobRequirements("Java, Spring Boot, AWS 경험 필수");

        // when & then
        assertThatThrownBy(() -> jobBoardValidationService.validateJobBoardUpdate(
                expiredJobBoard, newTitle, newDescription, newRequirements
        )).isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot update expired job board");
    }

    @Test
    @DisplayName("채용공고 수정 유효성 검증 실패 - null title")
    void validateJobBoardUpdate_NullTitle_ThrowsException() {
        // given
        JobBoard activeJobBoard = createActiveJobBoard();
        JobTitle newTitle = null;
        JobDescription newDescription = new JobDescription("시니어 Java 개발자를 찾습니다.");
        JobRequirements newRequirements = new JobRequirements("Java, Spring Boot, AWS 경험 필수");

        // when & then
        assertThatThrownBy(() -> jobBoardValidationService.validateJobBoardUpdate(
                activeJobBoard, newTitle, newDescription, newRequirements
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Job title is required");
    }

    @Test
    @DisplayName("채용공고 수정 유효성 검증 실패 - null description")
    void validateJobBoardUpdate_NullDescription_ThrowsException() {
        // given
        JobBoard activeJobBoard = createActiveJobBoard();
        JobTitle newTitle = new JobTitle("Senior Software Engineer");
        JobDescription newDescription = null;
        JobRequirements newRequirements = new JobRequirements("Java, Spring Boot, AWS 경험 필수");

        // when & then
        assertThatThrownBy(() -> jobBoardValidationService.validateJobBoardUpdate(
                activeJobBoard, newTitle, newDescription, newRequirements
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Job description is required");
    }

    @Test
    @DisplayName("채용공고 수정 유효성 검증 실패 - null requirements")
    void validateJobBoardUpdate_NullRequirements_ThrowsException() {
        // given
        JobBoard activeJobBoard = createActiveJobBoard();
        JobTitle newTitle = new JobTitle("Senior Software Engineer");
        JobDescription newDescription = new JobDescription("시니어 Java 개발자를 찾습니다.");
        JobRequirements newRequirements = null;

        // when & then
        assertThatThrownBy(() -> jobBoardValidationService.validateJobBoardUpdate(
                activeJobBoard, newTitle, newDescription, newRequirements
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Job requirements are required");
    }

    private JobBoard createActiveJobBoard() {
        JobBoard mockJobBoard = mock(JobBoard.class);
        when(mockJobBoard.isActive()).thenReturn(true);
        when(mockJobBoard.isExpired()).thenReturn(false);
        return mockJobBoard;
    }

    private JobBoard createInactiveJobBoard() {
        JobBoard mockJobBoard = mock(JobBoard.class);
        when(mockJobBoard.isActive()).thenReturn(false);
        when(mockJobBoard.isExpired()).thenReturn(false);
        return mockJobBoard;
    }

    private JobBoard createExpiredJobBoard() {
        JobBoard mockJobBoard = mock(JobBoard.class);
        when(mockJobBoard.isActive()).thenReturn(true);
        when(mockJobBoard.isExpired()).thenReturn(true);
        return mockJobBoard;
    }
}