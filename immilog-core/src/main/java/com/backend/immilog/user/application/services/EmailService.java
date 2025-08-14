package com.backend.immilog.user.application.services;

import com.backend.immilog.user.exception.UserErrorCode;
import com.backend.immilog.user.exception.UserException;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Async
    public void sendHtmlEmail(
            String to,
            String subject,
            String htmlBody
    ) {
        var message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("your-email@gmail.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
        } catch (MessagingException e) {
            log.error("Failed to send email", e);
            throw new UserException(UserErrorCode.EMAIL_SEND_FAILED);
        }

        javaMailSender.send(message);
    }

    @Async
    public void sendVerificationEmail(
            String to,
            String verificationCode
    ) {
        String subject = "이메일 인증";
        String htmlBody = buildVerificationEmailHtml(verificationCode);
        sendHtmlEmail(to, subject, htmlBody);
    }

    @Async
    public void sendPasswordResetEmail(
            String to,
            String resetToken
    ) {
        String subject = "비밀번호 재설정";
        String htmlBody = buildPasswordResetEmailHtml(resetToken);
        sendHtmlEmail(to, subject, htmlBody);
    }

    private String buildVerificationEmailHtml(String verificationCode) {
        return String.format("""
                <html>
                <body>
                    <h2>이메일 인증</h2>
                    <p>아래 인증 코드를 입력해주세요:</p>
                    <h3>%s</h3>
                </body>
                </html>
                """, verificationCode);
    }

    private String buildPasswordResetEmailHtml(String resetToken) {
        return String.format("""
                <html>
                <body>
                    <h2>비밀번호 재설정</h2>
                    <p>비밀번호 재설정을 위해 아래 토큰을 사용해주세요:</p>
                    <h3>%s</h3>
                </body>
                </html>
                """, resetToken);
    }
}