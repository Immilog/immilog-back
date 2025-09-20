package com.backend.immilog.report.presentation;

import com.backend.immilog.report.application.usecase.ReportUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.NO_CONTENT;

@Tag(name = "Report API", description = "신고 관련 API")
@RequestMapping("/api/v1/report")
@RestController
public class ReportController {
    private final ReportUseCase reportUseCase;

    public ReportController(ReportUseCase reportUseCase) {
        this.reportUseCase = reportUseCase;
    }

    @PostMapping("/target/{targetId}")
    @Operation(summary = "사용자/게시물 신고", description = "사용자 신고 진행")
    public ResponseEntity<Void> reportUser(
            @Parameter(description = "대상 사용자/게시물 고유번호") @PathVariable("targetId") String targetId,
            @Valid @RequestBody ReportPayload.ReportRequest request
    ) {
        reportUseCase.execute(targetId, request.toCommand());
        return ResponseEntity.status(NO_CONTENT).build();
    }

}
