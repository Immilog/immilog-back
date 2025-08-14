package com.backend.immilog.user.application.services.query;

import com.backend.immilog.shared.infrastructure.DataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@DisplayName("TokenQueryService 테스트")
class TokenQueryServiceTest {

    private final DataRepository dataRepository = mock(DataRepository.class);
    private TokenQueryService tokenQueryService;

    @BeforeEach
    void setUp() {
        tokenQueryService = new TokenQueryService(dataRepository);
    }

    @Test
    @DisplayName("키로 값을 정상적으로 조회할 수 있다")
    void getValueByKeySuccessfully() {
        // given
        String key = "access_token:user123";
        String expectedValue = "jwt-token-value";

        given(dataRepository.findByKey(key)).willReturn(expectedValue);

        // when
        String result = tokenQueryService.getValueByKey(key);

        // then
        assertThat(result).isEqualTo(expectedValue);
        verify(dataRepository).findByKey(key);
    }

    @Test
    @DisplayName("존재하지 않는 키로 조회 시 null을 반환한다")
    void getValueByKeyReturnsNullWhenNotFound() {
        // given
        String key = "non_existent:key123";

        given(dataRepository.findByKey(key)).willReturn(null);

        // when
        String result = tokenQueryService.getValueByKey(key);

        // then
        assertThat(result).isNull();
        verify(dataRepository).findByKey(key);
    }

    @Test
    @DisplayName("리프레시 토큰을 조회할 수 있다")
    void getRefreshToken() {
        // given
        String key = "refresh_token:user123";
        String expectedToken = "refresh-jwt-token-value";

        given(dataRepository.findByKey(key)).willReturn(expectedToken);

        // when
        String result = tokenQueryService.getValueByKey(key);

        // then
        assertThat(result).isEqualTo(expectedToken);
        verify(dataRepository).findByKey(key);
    }

    @Test
    @DisplayName("인증 코드를 조회할 수 있다")
    void getVerificationCode() {
        // given
        String key = "verification_code:test@example.com";
        String expectedCode = "123456";

        given(dataRepository.findByKey(key)).willReturn(expectedCode);

        // when
        String result = tokenQueryService.getValueByKey(key);

        // then
        assertThat(result).isEqualTo(expectedCode);
        verify(dataRepository).findByKey(key);
    }

    @Test
    @DisplayName("비밀번호 재설정 토큰을 조회할 수 있다")
    void getPasswordResetToken() {
        // given
        String key = "password_reset:user123";
        String expectedToken = "reset-token-12345";

        given(dataRepository.findByKey(key)).willReturn(expectedToken);

        // when
        String result = tokenQueryService.getValueByKey(key);

        // then
        assertThat(result).isEqualTo(expectedToken);
        verify(dataRepository).findByKey(key);
    }

    @Test
    @DisplayName("다양한 토큰 타입을 조회할 수 있다")
    void getDifferentTokenTypes() {
        // given
        String accessTokenKey = "access_token:user123";
        String refreshTokenKey = "refresh_token:user123";
        String verificationKey = "verification:test@example.com";
        String resetKey = "password_reset:user123";

        String accessToken = "access-jwt-token";
        String refreshToken = "refresh-jwt-token";
        String verificationCode = "123456";
        String resetToken = "reset-token-123";

        given(dataRepository.findByKey(accessTokenKey)).willReturn(accessToken);
        given(dataRepository.findByKey(refreshTokenKey)).willReturn(refreshToken);
        given(dataRepository.findByKey(verificationKey)).willReturn(verificationCode);
        given(dataRepository.findByKey(resetKey)).willReturn(resetToken);

        // when
        String accessResult = tokenQueryService.getValueByKey(accessTokenKey);
        String refreshResult = tokenQueryService.getValueByKey(refreshTokenKey);
        String verificationResult = tokenQueryService.getValueByKey(verificationKey);
        String resetResult = tokenQueryService.getValueByKey(resetKey);

        // then
        assertThat(accessResult).isEqualTo(accessToken);
        assertThat(refreshResult).isEqualTo(refreshToken);
        assertThat(verificationResult).isEqualTo(verificationCode);
        assertThat(resetResult).isEqualTo(resetToken);

        verify(dataRepository).findByKey(accessTokenKey);
        verify(dataRepository).findByKey(refreshTokenKey);
        verify(dataRepository).findByKey(verificationKey);
        verify(dataRepository).findByKey(resetKey);
    }

    @Test
    @DisplayName("긴 토큰 값을 조회할 수 있다")
    void getLongTokenValue() {
        // given
        String key = "long_token:user123";
        String longToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

        given(dataRepository.findByKey(key)).willReturn(longToken);

        // when
        String result = tokenQueryService.getValueByKey(key);

        // then
        assertThat(result).isEqualTo(longToken);
        verify(dataRepository).findByKey(key);
    }

    @Test
    @DisplayName("특수 문자가 포함된 키로 조회할 수 있다")
    void getValueWithSpecialCharactersInKey() {
        // given
        String key = "special:key_with-special@characters.com";
        String expectedValue = "special-value";

        given(dataRepository.findByKey(key)).willReturn(expectedValue);

        // when
        String result = tokenQueryService.getValueByKey(key);

        // then
        assertThat(result).isEqualTo(expectedValue);
        verify(dataRepository).findByKey(key);
    }

    @Test
    @DisplayName("빈 값을 조회할 수 있다")
    void getEmptyValue() {
        // given
        String key = "empty_value:key";
        String emptyValue = "";

        given(dataRepository.findByKey(key)).willReturn(emptyValue);

        // when
        String result = tokenQueryService.getValueByKey(key);

        // then
        assertThat(result).isEqualTo(emptyValue);
        verify(dataRepository).findByKey(key);
    }

    @Test
    @DisplayName("세션 관련 값들을 조회할 수 있다")
    void getSessionRelatedValues() {
        // given
        String sessionKey = "session:user123:device456";
        String loginAttemptKey = "login_attempt:192.168.1.1";
        String rateLimitKey = "rate_limit:api:user123";

        String sessionValue = "session-data-123";
        String attemptValue = "3";
        String limitValue = "10";

        given(dataRepository.findByKey(sessionKey)).willReturn(sessionValue);
        given(dataRepository.findByKey(loginAttemptKey)).willReturn(attemptValue);
        given(dataRepository.findByKey(rateLimitKey)).willReturn(limitValue);

        // when
        String sessionResult = tokenQueryService.getValueByKey(sessionKey);
        String attemptResult = tokenQueryService.getValueByKey(loginAttemptKey);
        String limitResult = tokenQueryService.getValueByKey(rateLimitKey);

        // then
        assertThat(sessionResult).isEqualTo(sessionValue);
        assertThat(attemptResult).isEqualTo(attemptValue);
        assertThat(limitResult).isEqualTo(limitValue);

        verify(dataRepository).findByKey(sessionKey);
        verify(dataRepository).findByKey(loginAttemptKey);
        verify(dataRepository).findByKey(rateLimitKey);
    }

    @Test
    @DisplayName("다양한 사용자의 토큰을 조회할 수 있다")
    void getTokensForDifferentUsers() {
        // given
        String user1TokenKey = "access_token:user1";
        String user2TokenKey = "access_token:user2";
        String user3TokenKey = "access_token:user3";

        String user1Token = "token-for-user1";
        String user2Token = "token-for-user2";
        String user3Token = "token-for-user3";

        given(dataRepository.findByKey(user1TokenKey)).willReturn(user1Token);
        given(dataRepository.findByKey(user2TokenKey)).willReturn(user2Token);
        given(dataRepository.findByKey(user3TokenKey)).willReturn(user3Token);

        // when
        String user1Result = tokenQueryService.getValueByKey(user1TokenKey);
        String user2Result = tokenQueryService.getValueByKey(user2TokenKey);
        String user3Result = tokenQueryService.getValueByKey(user3TokenKey);

        // then
        assertThat(user1Result).isEqualTo(user1Token);
        assertThat(user2Result).isEqualTo(user2Token);
        assertThat(user3Result).isEqualTo(user3Token);

        verify(dataRepository).findByKey(user1TokenKey);
        verify(dataRepository).findByKey(user2TokenKey);
        verify(dataRepository).findByKey(user3TokenKey);
    }

    @Test
    @DisplayName("동일한 키로 여러 번 조회할 수 있다")
    void getValueWithSameKeyMultipleTimes() {
        // given
        String key = "repeated_query:key";
        String expectedValue = "consistent-value";

        given(dataRepository.findByKey(key)).willReturn(expectedValue);

        // when
        String result1 = tokenQueryService.getValueByKey(key);
        String result2 = tokenQueryService.getValueByKey(key);
        String result3 = tokenQueryService.getValueByKey(key);

        // then
        assertThat(result1).isEqualTo(expectedValue);
        assertThat(result2).isEqualTo(expectedValue);
        assertThat(result3).isEqualTo(expectedValue);

        verify(dataRepository, times(3)).findByKey(key);
    }

    @Test
    @DisplayName("만료된 키 조회 시 null을 반환한다")
    void getExpiredKeyReturnsNull() {
        // given
        String expiredKey = "expired_token:user123";

        given(dataRepository.findByKey(expiredKey)).willReturn(null);

        // when
        String result = tokenQueryService.getValueByKey(expiredKey);

        // then
        assertThat(result).isNull();
        verify(dataRepository).findByKey(expiredKey);
    }

    @Test
    @DisplayName("다양한 이메일 주소의 인증 코드를 조회할 수 있다")
    void getVerificationCodesForDifferentEmails() {
        // given
        String[] emails = {
                "user1@example.com",
                "user2@gmail.com",
                "admin@company.co.kr",
                "test@domain.net"
        };

        String[] codes = {"123456", "654321", "111111", "999999"};

        for (int i = 0; i < emails.length; i++) {
            String key = "verification:" + emails[i];
            given(dataRepository.findByKey(key)).willReturn(codes[i]);
        }

        // when & then
        for (int i = 0; i < emails.length; i++) {
            String key = "verification:" + emails[i];
            String result = tokenQueryService.getValueByKey(key);
            assertThat(result).isEqualTo(codes[i]);
        }

        for (String email : emails) {
            String key = "verification:" + email;
            verify(dataRepository).findByKey(key);
        }
    }

    @Test
    @DisplayName("캐시된 사용자 정보를 조회할 수 있다")
    void getCachedUserInfo() {
        // given
        String userInfoKey = "user_info:user123";
        String userInfoJson = "{\"id\":\"user123\",\"email\":\"test@example.com\",\"nickname\":\"테스트유저\"}";

        given(dataRepository.findByKey(userInfoKey)).willReturn(userInfoJson);

        // when
        String result = tokenQueryService.getValueByKey(userInfoKey);

        // then
        assertThat(result).isEqualTo(userInfoJson);
        verify(dataRepository).findByKey(userInfoKey);
    }

    @Test
    @DisplayName("임시 데이터를 조회할 수 있다")
    void getTemporaryData() {
        // given
        String tempKey = "temp_data:operation123";
        String tempValue = "temporary-operation-data";

        given(dataRepository.findByKey(tempKey)).willReturn(tempValue);

        // when
        String result = tokenQueryService.getValueByKey(tempKey);

        // then
        assertThat(result).isEqualTo(tempValue);
        verify(dataRepository).findByKey(tempKey);
    }

    @Test
    @DisplayName("API 키를 조회할 수 있다")
    void getApiKey() {
        // given
        String apiKeyKey = "api_key:service123";
        String apiKeyValue = "sk-1234567890abcdef";

        given(dataRepository.findByKey(apiKeyKey)).willReturn(apiKeyValue);

        // when
        String result = tokenQueryService.getValueByKey(apiKeyKey);

        // then
        assertThat(result).isEqualTo(apiKeyValue);
        verify(dataRepository).findByKey(apiKeyKey);
    }

    @Test
    @DisplayName("복잡한 키 패턴으로 값을 조회할 수 있다")
    void getValueWithComplexKeyPattern() {
        // given
        String complexKey = "namespace:service:feature:user123:session456:temp";
        String complexValue = "complex-nested-value";

        given(dataRepository.findByKey(complexKey)).willReturn(complexValue);

        // when
        String result = tokenQueryService.getValueByKey(complexKey);

        // then
        assertThat(result).isEqualTo(complexValue);
        verify(dataRepository).findByKey(complexKey);
    }
}