package com.backend.immilog.jobboard.presentation.payload;

import com.backend.immilog.jobboard.application.dto.JobBoardResult;
import com.backend.immilog.jobboard.domain.model.Experience;
import com.backend.immilog.jobboard.domain.model.Industry;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record JobBoardResponse(
        @Schema(description = "상태 코드", example = "200") int status,
        @Schema(description = "응답 메시지", example = "success") String message,
        @Schema(description = "구인구직 데이터", oneOf = {JobBoardInformation.class, Page.class}) Object data
) {
    public static JobBoardResponse success(JobBoardResult data) {
        return new JobBoardResponse(200, "success", data.toInfraDTO());
    }

    public static JobBoardResponse success(Page<JobBoardResult> data) {
        var jobBoardInfoPage = data.map(JobBoardResult::toInfraDTO);
        return new JobBoardResponse(200, "success", jobBoardInfoPage);
    }

    public record JobBoardInformation(
            @Schema(description = "구인구직 ID", example = "job123") String id,
            @Schema(description = "사용자 ID", example = "user123") String userId,
            @Schema(description = "회사명", example = "테크 컴퍼니") String companyName,
            @Schema(description = "회사 위치", example = "서울시 강남구") String companyLocation,
            @Schema(description = "직무 제목", example = "백엔드 개발자") String title,
            @Schema(description = "근무 위치", example = "서울시 강남구") String location,
            @Schema(description = "근무 형태", example = "FULL_TIME") String workType,
            @Schema(description = "경력 요구사항") Experience experience,
            @Schema(description = "산업 분야") Industry industry,
            @Schema(description = "급여", example = "5000") BigDecimal salaryAmount,
            @Schema(description = "급여 통화", example = "USD") String salaryCurrency,
            @Schema(description = "직무 설명") String description,
            @Schema(description = "요구 사항") String requirements,
            @Schema(description = "복지 혜택") String benefits,
            @Schema(description = "지원 마감일") LocalDate applicationDeadline,
            @Schema(description = "연락처 이메일") String contactEmail,
            @Schema(description = "활성 상태", example = "true") Boolean isActive,
            @Schema(description = "조회수", example = "100") Long viewCount,
            @Schema(description = "국가") String countryId,
            @Schema(description = "생성일") LocalDateTime createdAt,
            @Schema(description = "수정일") LocalDateTime updatedAt,
            @Schema(description = "지원 가능 여부", example = "true") Boolean canApply,
            @Schema(description = "만료 여부", example = "false") Boolean isExpired
    ) {
    }
}