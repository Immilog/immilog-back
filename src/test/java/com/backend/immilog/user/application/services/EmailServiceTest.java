package com.backend.immilog.user.application.services;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.BDDMockito.*;

@DisplayName("EmailService 테스트")
class EmailServiceTest {

    private final JavaMailSender javaMailSender = mock(JavaMailSender.class);
    private EmailService emailService;
    private MimeMessage mimeMessage;

    @BeforeEach
    void setUp() {
        emailService = new EmailService(javaMailSender);
        mimeMessage = mock(MimeMessage.class);
    }

    @Test
    @DisplayName("HTML 이메일을 정상적으로 발송할 수 있다")
    void sendHtmlEmailSuccessfully() {
        // given
        String to = "test@example.com";
        String subject = "테스트 제목";
        String htmlBody = "<html><body><h1>테스트 내용</h1></body></html>";

        given(javaMailSender.createMimeMessage()).willReturn(mimeMessage);

        // when
        emailService.sendHtmlEmail(to, subject, htmlBody);

        // then
        verify(javaMailSender).createMimeMessage();
        verify(javaMailSender).send(mimeMessage);
    }


    @Test
    @DisplayName("인증 이메일을 정상적으로 발송할 수 있다")
    void sendVerificationEmailSuccessfully() {
        // given
        String to = "test@example.com";
        String verificationCode = "123456";

        given(javaMailSender.createMimeMessage()).willReturn(mimeMessage);

        // when
        emailService.sendVerificationEmail(to, verificationCode);

        // then
        verify(javaMailSender).createMimeMessage();
        verify(javaMailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("비밀번호 재설정 이메일을 정상적으로 발송할 수 있다")
    void sendPasswordResetEmailSuccessfully() {
        // given
        String to = "test@example.com";
        String resetToken = "reset-token-123";

        given(javaMailSender.createMimeMessage()).willReturn(mimeMessage);

        // when
        emailService.sendPasswordResetEmail(to, resetToken);

        // then
        verify(javaMailSender).createMimeMessage();
        verify(javaMailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("다양한 이메일 주소로 이메일을 발송할 수 있다")
    void sendEmailToVariousAddresses() {
        // given
        String[] emails = {
                "user1@example.com",
                "user2@gmail.com",
                "test@naver.com",
                "admin@company.co.kr"
        };
        String subject = "테스트";
        String htmlBody = "<html><body>테스트</body></html>";

        given(javaMailSender.createMimeMessage()).willReturn(mimeMessage);

        // when
        for (String email : emails) {
            emailService.sendHtmlEmail(email, subject, htmlBody);
        }

        // then
        verify(javaMailSender, times(4)).createMimeMessage();
        verify(javaMailSender, times(4)).send(mimeMessage);
    }

    @Test
    @DisplayName("긴 HTML 내용으로 이메일을 발송할 수 있다")
    void sendEmailWithLongHtmlContent() {
        // given
        String to = "test@example.com";
        String subject = "긴 내용 테스트";
        String htmlBody = """
                <html>
                <head><title>테스트</title></head>
                <body>
                    <h1>환영합니다!</h1>
                    <p>이것은 매우 긴 HTML 내용입니다.</p>
                    <ul>
                        <li>항목 1</li>
                        <li>항목 2</li>
                        <li>항목 3</li>
                    </ul>
                    <div>
                        <span>추가 내용</span>
                    </div>
                </body>
                </html>
                """;

        given(javaMailSender.createMimeMessage()).willReturn(mimeMessage);

        // when
        emailService.sendHtmlEmail(to, subject, htmlBody);

        // then
        verify(javaMailSender).createMimeMessage();
        verify(javaMailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("특수 문자가 포함된 제목으로 이메일을 발송할 수 있다")
    void sendEmailWithSpecialCharactersInSubject() {
        // given
        String to = "test@example.com";
        String subject = "특수문자 테스트 !@#$%^&*()";
        String htmlBody = "<html><body>테스트</body></html>";

        given(javaMailSender.createMimeMessage()).willReturn(mimeMessage);

        // when
        emailService.sendHtmlEmail(to, subject, htmlBody);

        // then
        verify(javaMailSender).createMimeMessage();
        verify(javaMailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("다양한 인증 코드로 인증 이메일을 발송할 수 있다")
    void sendVerificationEmailWithVariousCodes() {
        // given
        String to = "test@example.com";
        String[] verificationCodes = {
                "123456",
                "ABC123",
                "999999",
                "000000",
                "ABCDEF"
        };

        given(javaMailSender.createMimeMessage()).willReturn(mimeMessage);

        // when
        for (String code : verificationCodes) {
            emailService.sendVerificationEmail(to, code);
        }

        // then
        verify(javaMailSender, times(5)).createMimeMessage();
        verify(javaMailSender, times(5)).send(mimeMessage);
    }

    @Test
    @DisplayName("다양한 리셋 토큰으로 비밀번호 재설정 이메일을 발송할 수 있다")
    void sendPasswordResetEmailWithVariousTokens() {
        // given
        String to = "test@example.com";
        String[] resetTokens = {
                "reset-token-123",
                "very-long-reset-token-with-many-characters-12345",
                "short-token",
                "TOKEN123",
                "reset_token_456"
        };

        given(javaMailSender.createMimeMessage()).willReturn(mimeMessage);

        // when
        for (String token : resetTokens) {
            emailService.sendPasswordResetEmail(to, token);
        }

        // then
        verify(javaMailSender, times(5)).createMimeMessage();
        verify(javaMailSender, times(5)).send(mimeMessage);
    }

    @Test
    @DisplayName("UTF-8 인코딩이 필요한 한글 내용으로 이메일을 발송할 수 있다")
    void sendEmailWithKoreanContent() {
        // given
        String to = "test@example.com";
        String subject = "안녕하세요 한글 제목입니다";
        String htmlBody = """
                <html>
                <body>
                    <h2>안녕하세요, 사용자님!</h2>
                    <p>한글 내용이 포함된 이메일입니다.</p>
                    <p>정상적으로 인코딩되어 전송되어야 합니다.</p>
                </body>
                </html>
                """;

        given(javaMailSender.createMimeMessage()).willReturn(mimeMessage);

        // when
        emailService.sendHtmlEmail(to, subject, htmlBody);

        // then
        verify(javaMailSender).createMimeMessage();
        verify(javaMailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("빈 문자열 내용으로도 이메일을 발송할 수 있다")
    void sendEmailWithEmptyContent() {
        // given
        String to = "test@example.com";
        String subject = "";
        String htmlBody = "";

        given(javaMailSender.createMimeMessage()).willReturn(mimeMessage);

        // when
        emailService.sendHtmlEmail(to, subject, htmlBody);

        // then
        verify(javaMailSender).createMimeMessage();
        verify(javaMailSender).send(mimeMessage);
    }
}