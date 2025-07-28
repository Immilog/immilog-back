package com.backend.immilog.user.application.services;

import com.backend.immilog.user.domain.model.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationService {

    private final EmailService emailService;

    public NotificationService(EmailService emailService) {
        this.emailService = emailService;
    }

    public void notifyUserRegistration(User user) {
        log.info("User registered notification: {}", user.getEmail());

        String welcomeMessage = buildWelcomeMessage(user.getNickname());
        emailService.sendHtmlEmail(
                user.getEmail(),
                "회원가입을 환영합니다!",
                welcomeMessage
        );
    }

    public void notifyPasswordChanged(User user) {
        log.info("Password changed notification: {}", user.getEmail());

        String message = buildPasswordChangedMessage(user.getNickname());
        emailService.sendHtmlEmail(
                user.getEmail(),
                "비밀번호가 변경되었습니다",
                message
        );
    }

    public void notifyProfileUpdated(User user) {
        log.info("Profile updated notification: {}", user.getEmail());

        String message = buildProfileUpdatedMessage(user.getNickname());
        emailService.sendHtmlEmail(
                user.getEmail(),
                "프로필이 업데이트되었습니다",
                message
        );
    }

    public void notifyAccountBlocked(User user) {
        log.info("Account blocked notification: {}", user.getEmail());

        String message = buildAccountBlockedMessage(user.getNickname());
        emailService.sendHtmlEmail(
                user.getEmail(),
                "계정이 차단되었습니다",
                message
        );
    }

    public void notifyAccountActivated(User user) {
        log.info("Account activated notification: {}", user.getEmail());

        String message = buildAccountActivatedMessage(user.getNickname());
        emailService.sendHtmlEmail(
                user.getEmail(),
                "계정이 활성화되었습니다",
                message
        );
    }

    private String buildWelcomeMessage(String nickname) {
        return String.format("""
                <html>
                <body>
                    <h2>환영합니다, %s님!</h2>
                    <p>ImmiLog에 가입해주셔서 감사합니다.</p>
                    <p>다양한 기능을 활용해보세요!</p>
                </body>
                </html>
                """, nickname);
    }

    private String buildPasswordChangedMessage(String nickname) {
        return String.format("""
                <html>
                <body>
                    <h2>안녕하세요, %s님</h2>
                    <p>비밀번호가 성공적으로 변경되었습니다.</p>
                    <p>만약 본인이 변경하지 않았다면 즉시 고객센터로 연락주세요.</p>
                </body>
                </html>
                """, nickname);
    }

    private String buildProfileUpdatedMessage(String nickname) {
        return String.format("""
                <html>
                <body>
                    <h2>안녕하세요, %s님</h2>
                    <p>프로필 정보가 성공적으로 업데이트되었습니다.</p>
                </body>
                </html>
                """, nickname);
    }

    private String buildAccountBlockedMessage(String nickname) {
        return String.format("""
                <html>
                <body>
                    <h2>안녕하세요, %s님</h2>
                    <p>계정이 일시적으로 차단되었습니다.</p>
                    <p>자세한 사항은 고객센터로 문의해주세요.</p>
                </body>
                </html>
                """, nickname);
    }

    private String buildAccountActivatedMessage(String nickname) {
        return String.format("""
                <html>
                <body>
                    <h2>안녕하세요, %s님</h2>
                    <p>계정이 다시 활성화되었습니다.</p>
                    <p>서비스를 자유롭게 이용하실 수 있습니다.</p>
                </body>
                </html>
                """, nickname);
    }
}