package com.backend.immilog.user.application.services.command;

import com.backend.immilog.shared.infrastructure.DataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.BDDMockito.*;

@DisplayName("TokenCommandService 테스트")
class TokenCommandServiceTest {

    private final DataRepository dataRepository = mock(DataRepository.class);
    private TokenCommandService tokenCommandService;

    @BeforeEach
    void setUp() {
        tokenCommandService = new TokenCommandService(dataRepository);
    }

    @Test
    @DisplayName("키와 값을 정상적으로 저장할 수 있다")
    void saveKeyAndValueSuccessfully() {
        // given
        String key = "access_token:user123";
        String value = "jwt-token-value";
        int expireTime = 3600;

        // when
        tokenCommandService.saveKeyAndValue(key, value, expireTime);

        // then
        verify(dataRepository).save(key, value, expireTime);
    }

    @Test
    @DisplayName("리프레시 토큰을 저장할 수 있다")
    void saveRefreshToken() {
        // given
        String key = "refresh_token:user123";
        String value = "refresh-jwt-token-value";
        int expireTime = 86400; // 24시간

        // when
        tokenCommandService.saveKeyAndValue(key, value, expireTime);

        // then
        verify(dataRepository).save(key, value, expireTime);
    }

    @Test
    @DisplayName("인증 코드를 저장할 수 있다")
    void saveVerificationCode() {
        // given
        String key = "verification_code:test@example.com";
        String value = "123456";
        int expireTime = 300; // 5분

        // when
        tokenCommandService.saveKeyAndValue(key, value, expireTime);

        // then
        verify(dataRepository).save(key, value, expireTime);
    }

    @Test
    @DisplayName("비밀번호 재설정 토큰을 저장할 수 있다")
    void savePasswordResetToken() {
        // given
        String key = "password_reset:user123";
        String value = "reset-token-12345";
        int expireTime = 1800; // 30분

        // when
        tokenCommandService.saveKeyAndValue(key, value, expireTime);

        // then
        verify(dataRepository).save(key, value, expireTime);
    }

    @Test
    @DisplayName("다양한 만료 시간으로 토큰을 저장할 수 있다")
    void saveTokensWithVariousExpireTimes() {
        // given
        String shortTermKey = "short_term:key";
        String mediumTermKey = "medium_term:key";
        String longTermKey = "long_term:key";
        String value = "token-value";

        int shortExpire = 60;      // 1분
        int mediumExpire = 3600;   // 1시간
        int longExpire = 86400;    // 24시간

        // when
        tokenCommandService.saveKeyAndValue(shortTermKey, value, shortExpire);
        tokenCommandService.saveKeyAndValue(mediumTermKey, value, mediumExpire);
        tokenCommandService.saveKeyAndValue(longTermKey, value, longExpire);

        // then
        verify(dataRepository).save(shortTermKey, value, shortExpire);
        verify(dataRepository).save(mediumTermKey, value, mediumExpire);
        verify(dataRepository).save(longTermKey, value, longExpire);
    }

    @Test
    @DisplayName("긴 토큰 값을 저장할 수 있다")
    void saveLongTokenValue() {
        // given
        String key = "long_token:user123";
        String longValue = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        int expireTime = 3600;

        // when
        tokenCommandService.saveKeyAndValue(key, longValue, expireTime);

        // then
        verify(dataRepository).save(key, longValue, expireTime);
    }

    @Test
    @DisplayName("특수 문자가 포함된 키로 저장할 수 있다")
    void saveWithSpecialCharactersInKey() {
        // given
        String key = "special:key_with-special@characters.com";
        String value = "special-value";
        int expireTime = 3600;

        // when
        tokenCommandService.saveKeyAndValue(key, value, expireTime);

        // then
        verify(dataRepository).save(key, value, expireTime);
    }

    @Test
    @DisplayName("빈 값으로도 저장할 수 있다")
    void saveWithEmptyValue() {
        // given
        String key = "empty_value:key";
        String value = "";
        int expireTime = 3600;

        // when
        tokenCommandService.saveKeyAndValue(key, value, expireTime);

        // then
        verify(dataRepository).save(key, value, expireTime);
    }

    @Test
    @DisplayName("0초 만료 시간으로 저장할 수 있다")
    void saveWithZeroExpireTime() {
        // given
        String key = "immediate_expire:key";
        String value = "value";
        int expireTime = 0;

        // when
        tokenCommandService.saveKeyAndValue(key, value, expireTime);

        // then
        verify(dataRepository).save(key, value, expireTime);
    }

    @Test
    @DisplayName("키로 값을 정상적으로 삭제할 수 있다")
    void deleteValueByKeySuccessfully() {
        // given
        String key = "delete_test:user123";

        // when
        tokenCommandService.deleteValueByKey(key);

        // then
        verify(dataRepository).deleteByKey(key);
    }

    @Test
    @DisplayName("여러 키를 연속으로 삭제할 수 있다")
    void deleteMultipleKeysSequentially() {
        // given
        String[] keys = {
                "access_token:user1",
                "refresh_token:user1",
                "verification_code:user1@example.com",
                "password_reset:user1"
        };

        // when
        for (String key : keys) {
            tokenCommandService.deleteValueByKey(key);
        }

        // then
        for (String key : keys) {
            verify(dataRepository).deleteByKey(key);
        }
    }

    @Test
    @DisplayName("존재하지 않는 키도 삭제 시도할 수 있다")
    void deleteNonExistentKey() {
        // given
        String nonExistentKey = "non_existent:key123";

        // when
        tokenCommandService.deleteValueByKey(nonExistentKey);

        // then
        verify(dataRepository).deleteByKey(nonExistentKey);
    }

    @Test
    @DisplayName("특수 문자가 포함된 키를 삭제할 수 있다")
    void deleteKeyWithSpecialCharacters() {
        // given
        String specialKey = "special:key_with-special@characters.com";

        // when
        tokenCommandService.deleteValueByKey(specialKey);

        // then
        verify(dataRepository).deleteByKey(specialKey);
    }

    @Test
    @DisplayName("세션 관련 키들을 삭제할 수 있다")
    void deleteSessionRelatedKeys() {
        // given
        String sessionKey = "session:user123:device456";
        String loginAttemptKey = "login_attempt:192.168.1.1";
        String rateLimitKey = "rate_limit:api:user123";

        // when
        tokenCommandService.deleteValueByKey(sessionKey);
        tokenCommandService.deleteValueByKey(loginAttemptKey);
        tokenCommandService.deleteValueByKey(rateLimitKey);

        // then
        verify(dataRepository).deleteByKey(sessionKey);
        verify(dataRepository).deleteByKey(loginAttemptKey);
        verify(dataRepository).deleteByKey(rateLimitKey);
    }

    @Test
    @DisplayName("저장과 삭제를 연속으로 수행할 수 있다")
    void performSaveAndDeleteSequentially() {
        // given
        String key1 = "temp_token:user123";
        String key2 = "temp_token:user456";
        String value = "temporary-value";
        int expireTime = 300;

        // when
        tokenCommandService.saveKeyAndValue(key1, value, expireTime);
        tokenCommandService.saveKeyAndValue(key2, value, expireTime);
        tokenCommandService.deleteValueByKey(key1);
        tokenCommandService.deleteValueByKey(key2);

        // then
        verify(dataRepository).save(key1, value, expireTime);
        verify(dataRepository).save(key2, value, expireTime);
        verify(dataRepository).deleteByKey(key1);
        verify(dataRepository).deleteByKey(key2);
    }

    @Test
    @DisplayName("동일한 키로 여러 번 저장할 수 있다")
    void saveSameKeyMultipleTimes() {
        // given
        String key = "overwrite_test:user123";
        String value1 = "first-value";
        String value2 = "second-value";
        String value3 = "third-value";
        int expireTime = 3600;

        // when
        tokenCommandService.saveKeyAndValue(key, value1, expireTime);
        tokenCommandService.saveKeyAndValue(key, value2, expireTime);
        tokenCommandService.saveKeyAndValue(key, value3, expireTime);

        // then
        verify(dataRepository, times(3)).save(eq(key), anyString(), eq(expireTime));
        verify(dataRepository).save(key, value1, expireTime);
        verify(dataRepository).save(key, value2, expireTime);
        verify(dataRepository).save(key, value3, expireTime);
    }

    @Test
    @DisplayName("다양한 토큰 타입을 한 번에 저장할 수 있다")
    void saveDifferentTokenTypes() {
        // given
        String userId = "user123";
        String email = "test@example.com";

        String accessTokenKey = "access_token:" + userId;
        String refreshTokenKey = "refresh_token:" + userId;
        String verificationKey = "verification:" + email;
        String resetKey = "password_reset:" + userId;

        String accessToken = "access-jwt-token";
        String refreshToken = "refresh-jwt-token";
        String verificationCode = "123456";
        String resetToken = "reset-token-123";

        // when
        tokenCommandService.saveKeyAndValue(accessTokenKey, accessToken, 3600);
        tokenCommandService.saveKeyAndValue(refreshTokenKey, refreshToken, 86400);
        tokenCommandService.saveKeyAndValue(verificationKey, verificationCode, 300);
        tokenCommandService.saveKeyAndValue(resetKey, resetToken, 1800);

        // then
        verify(dataRepository).save(accessTokenKey, accessToken, 3600);
        verify(dataRepository).save(refreshTokenKey, refreshToken, 86400);
        verify(dataRepository).save(verificationKey, verificationCode, 300);
        verify(dataRepository).save(resetKey, resetToken, 1800);
    }
}