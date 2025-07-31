package com.backend.immilog.user.presentation.controller;

import com.backend.immilog.report.application.usecase.ReportUseCase;
import com.backend.immilog.user.application.services.EmailService;
import com.backend.immilog.user.application.usecase.FetchLocationUseCase;
import com.backend.immilog.user.application.usecase.LoginUserUseCase;
import com.backend.immilog.user.application.usecase.SignUpUserUseCase;
import com.backend.immilog.user.application.usecase.UpdateProfileUseCase;
import com.backend.immilog.user.presentation.payload.UserInformationPayload;
import com.backend.immilog.user.presentation.payload.UserSignUpPayload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.backend.immilog.user.presentation.controller.EmailComponents.*;
import static org.springframework.http.HttpStatus.CREATED;

@Tag(name = "User API", description = "사용자 관련 API")
@RequestMapping("/api/v1/users")
@RestController
public class UserController {
    private final SignUpUserUseCase userSignUpProcessor;
    private final LoginUserUseCase userLoginProcessor;
    private final UpdateProfileUseCase userUpdater;
    private final ReportUseCase userReporter;
    private final FetchLocationUseCase locationFetcher;
    private final EmailService emailSender;

    public UserController(
            SignUpUserUseCase userSignUpProcessor,
            LoginUserUseCase userLoginProcessor,
            UpdateProfileUseCase userUpdater,
            ReportUseCase userReporter,
            FetchLocationUseCase locationFetcher,
            EmailService emailSender
    ) {
        this.userSignUpProcessor = userSignUpProcessor;
        this.userLoginProcessor = userLoginProcessor;
        this.userUpdater = userUpdater;
        this.userReporter = userReporter;
        this.locationFetcher = locationFetcher;
        this.emailSender = emailSender;
    }

    @PostMapping
    @Operation(summary = "사용자 회원가입", description = "사용자 회원가입을 진행합니다.")
    public ResponseEntity<Void> signUp(
            @Valid @RequestBody UserSignUpPayload.UserSignUpRequest request
    ) {
        var userIdAndName = userSignUpProcessor.signUp(request.toCommand());
        var url = String.format(API_LINK, userIdAndName.userId());
        var mailForm = String.format(HTML_SIGN_UP_CONTENT, userIdAndName.nickName(), url);
        emailSender.sendHtmlEmail(request.email(), EMAIL_SIGN_UP_SUBJECT, mailForm);

        return ResponseEntity.status(CREATED).build();
    }

    @PutMapping("/{userId}")
    @Operation(summary = "사용자 정보 수정", description = "사용자 정보를 수정합니다.")
    public ResponseEntity<Void> updateUser(
            @Parameter(description = "사용자 고유번호") @PathVariable String userId,
            @RequestBody UserInformationPayload.UserInfoUpdateRequest request
    ) {
        var country = locationFetcher.getCountry(request.latitude(), request.longitude());
        userUpdater.updateInformation(userId, country, request.toCommand());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{userId}/password")
    @Operation(summary = "비밀번호 변경", description = "사용자 비밀번호를 변경합니다.")
    public ResponseEntity<Void> changePassword(
            @Parameter(description = "사용자 고유번호") @PathVariable String userId,
            @RequestBody UserInformationPayload.UserPasswordChangeRequest request
    ) {
        userUpdater.changePassword(userId, request.toCommand());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/nicknames/{nickname}/availability")
    @Operation(summary = "닉네임 중복 체크", description = "닉네임 사용 가능 여부를 체크합니다.")
    public ResponseEntity<UserInformationPayload.UserNicknameResponse> checkNicknameAvailability(
            @Parameter(description = "닉네임") @PathVariable String nickname
    ) {
        var isNicknameAvailable = userSignUpProcessor.isNicknameAvailable(nickname);
        return ResponseEntity.ok(new UserInformationPayload.UserNicknameResponse(isNicknameAvailable));
    }

    @PostMapping("/{userId}/blocks/{targetId}")
    @Operation(summary = "사용자 차단", description = "특정 사용자를 차단합니다.")
    public ResponseEntity<Void> blockUser(
            @Parameter(description = "사용자 고유번호") @PathVariable String userId,
            @Parameter(description = "차단할 사용자 고유번호") @PathVariable String targetId
    ) {
        userUpdater.blockOrUnblockUser(targetId, userId, "BLOCK");
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}/blocks/{targetId}")
    @Operation(summary = "사용자 차단 해제", description = "차단된 사용자를 해제합니다.")
    public ResponseEntity<Void> unblockUser(
            @Parameter(description = "사용자 고유번호") @PathVariable String userId,
            @Parameter(description = "차단 해제할 사용자 고유번호") @PathVariable String targetId
    ) {
        userUpdater.blockOrUnblockUser(targetId, userId, "UNBLOCK");
        return ResponseEntity.noContent().build();
    }

}
