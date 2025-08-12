package com.backend.immilog.jobboard.presentation.controller;

import com.backend.immilog.jobboard.application.usecase.FetchJobBoardUseCase;
import com.backend.immilog.jobboard.application.usecase.UploadJobBoardUseCase;
import com.backend.immilog.jobboard.presentation.payload.JobBoardCreateRequest;
import com.backend.immilog.jobboard.presentation.payload.JobBoardResponse;
import com.backend.immilog.shared.annotation.CurrentUser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobboards")
public class JobBoardController {
    private final FetchJobBoardUseCase fetchJobBoardUseCase;
    private final UploadJobBoardUseCase uploadJobBoardUseCase;

    public JobBoardController(
            FetchJobBoardUseCase fetchJobBoardUseCase,
            UploadJobBoardUseCase uploadJobBoardUseCase
    ) {
        this.fetchJobBoardUseCase = fetchJobBoardUseCase;
        this.uploadJobBoardUseCase = uploadJobBoardUseCase;
    }

    @GetMapping
    public ResponseEntity<JobBoardResponse> getJobBoards(
            @RequestParam("country") String countryId,
            @RequestParam(value = "page", defaultValue = "0") Integer page
    ) {
        var jobBoards = fetchJobBoardUseCase.getJobBoards(countryId, page);
        return ResponseEntity.ok(JobBoardResponse.success(jobBoards));
    }

    @GetMapping("/{jobBoardId}")
    public ResponseEntity<JobBoardResponse> getJobBoardDetail(
            @PathVariable("jobBoardId") String jobBoardId
    ) {
        var jobBoard = fetchJobBoardUseCase.getJobBoardDetail(jobBoardId);
        return ResponseEntity.ok(JobBoardResponse.success(jobBoard));
    }

    @PostMapping
    public ResponseEntity<JobBoardResponse> createJobBoard(
            @CurrentUser String userId,
            @RequestBody JobBoardCreateRequest request
    ) {
        var command = request.toCommand(userId);
        var result = uploadJobBoardUseCase.uploadJobBoard(command);
        return ResponseEntity.ok(JobBoardResponse.success(result));
    }
}