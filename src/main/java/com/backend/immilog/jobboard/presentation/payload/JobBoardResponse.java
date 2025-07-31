package com.backend.immilog.jobboard.presentation.payload;

import com.backend.immilog.jobboard.application.dto.JobBoardResult;
import org.springframework.data.domain.Page;

public record JobBoardResponse(
        int status,
        String message,
        Object data
) {
    public static JobBoardResponse success(JobBoardResult data) {
        return new JobBoardResponse(200, "success", data);
    }

    public static JobBoardResponse success(Page<JobBoardResult> data) {
        return new JobBoardResponse(200, "success", data);
    }
}