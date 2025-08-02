package com.backend.immilog.company.presentation;

import com.backend.immilog.company.application.usecase.GetCompanyUseCase;
import com.backend.immilog.company.application.usecase.RegisterCompanyUseCase;
import com.backend.immilog.company.application.usecase.UpdateCompanyUseCase;
import com.backend.immilog.shared.presentation.GeneralPayload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Tag(name = "Company API", description = "회사정보 관련 API")
@RequestMapping("/api/v1/companies")
@RestController
public class CompanyController {
    private final RegisterCompanyUseCase registerCompanyUseCase;
    private final UpdateCompanyUseCase updateCompanyUseCase;
    private final GetCompanyUseCase getCompanyUseCase;

    public CompanyController(
            RegisterCompanyUseCase registerCompanyUseCase,
            UpdateCompanyUseCase updateCompanyUseCase,
            GetCompanyUseCase getCompanyUseCase
    ) {
        this.registerCompanyUseCase = registerCompanyUseCase;
        this.updateCompanyUseCase = updateCompanyUseCase;
        this.getCompanyUseCase = getCompanyUseCase;
    }

    @PostMapping("/users/{userId}")
    @Operation(summary = "회사정보 등록", description = "회사정보를 등록합니다.")
    public ResponseEntity<GeneralPayload> registerCompany(
            @Parameter(description = "사용자 고유번호") @PathVariable("userId") String userId,
            @RequestBody CompanyPayload.CompanyRegisterRequest param
    ) {
        registerCompanyUseCase.registerCompany(userId, param.toCommand());
        return ResponseEntity.status(CREATED).build();
    }

    @PutMapping("/users/{userId}")
    @Operation(summary = "회사정보 수정", description = "회사정보를 수정합니다.")
    public ResponseEntity<GeneralPayload> updateCompany(
            @Parameter(description = "사용자 고유번호") @PathVariable("userId") String userId,
            @RequestBody CompanyPayload.CompanyRegisterRequest param
    ) {
        updateCompanyUseCase.updateCompany(userId, param.toCommand());
        return ResponseEntity.status(OK).build();
    }

    @GetMapping("/users/{userId}")
    @Operation(summary = "본인 회사정보 조회", description = "본인 회사정보를 조회합니다.")
    public ResponseEntity<CompanyPayload.CompanyResponse> getCompany(
            @Parameter(description = "사용자 고유번호") @PathVariable("userId") String userId
    ) {
        final var result = getCompanyUseCase.getCompany(userId);
        return ResponseEntity.status(OK).body(result.toResponse());
    }
}
