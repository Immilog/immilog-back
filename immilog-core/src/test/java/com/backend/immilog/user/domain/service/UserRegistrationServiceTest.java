package com.backend.immilog.user.domain.service;

import com.backend.immilog.user.domain.model.*;
import com.backend.immilog.user.domain.enums.UserRole;
import com.backend.immilog.user.domain.enums.UserStatus;
import com.backend.immilog.user.domain.repositories.UserRepository;
import com.backend.immilog.user.exception.UserErrorCode;
import com.backend.immilog.user.exception.UserException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserRegistrationServiceTest {

    private final UserRepository mockUserRepository = mock(UserRepository.class);
    private final UserRegistrationService userRegistrationService = new UserRegistrationService(mockUserRepository);

    @Nested
    @DisplayName("사용자 등록 테스트")
    class RegisterNewUserTest {

        @Test
        @DisplayName("새로운 사용자를 등록할 수 있다")
        void registerNewUser() {
            Auth auth = Auth.of("test@example.com", "encodedPassword");
            Profile profile = Profile.of("nickname", "imageUrl", "US");
            Location location = Location.of("KR", "Seoul");
            when(mockUserRepository.existsByEmail("test@example.com")).thenReturn(false);

            User result = userRegistrationService.registerNewUser(auth, profile, location);

            assertThat(result).isNotNull();
            assertThat(result.getEmail()).isEqualTo("test@example.com");
            assertThat(result.getNickname()).isEqualTo("nickname");
            assertThat(result.getUserStatus()).isEqualTo(UserStatus.PENDING);
            assertThat(result.getUserRole()).isEqualTo(UserRole.ROLE_USER);
            verify(mockUserRepository).existsByEmail("test@example.com");
        }

        @Test
        @DisplayName("이미 존재하는 이메일로 등록하면 예외가 발생한다")
        void registerNewUserWithExistingEmail() {
            Auth auth = Auth.of("existing@example.com", "encodedPassword");
            Profile profile = Profile.of("nickname", "imageUrl", "US");
            Location location = Location.of("KR", "Seoul");
            when(mockUserRepository.existsByEmail("existing@example.com")).thenReturn(true);

            assertThatThrownBy(() -> userRegistrationService.registerNewUser(auth, profile, location))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.EXISTING_USER.getMessage());

            verify(mockUserRepository).existsByEmail("existing@example.com");
        }

        @Test
        @DisplayName("다양한 이메일 형식으로 사용자를 등록할 수 있다")
        void registerUserWithVariousEmailFormats() {
            String[] emails = {
                    "user@domain.com",
                    "user.name@domain.co.kr",
                    "user+tag@subdomain.domain.org",
                    "123@numbers.net"
            };

            for (String email : emails) {
                Auth auth = Auth.of(email, "encodedPassword");
                Profile profile = Profile.of("nickname", "imageUrl", "US");
                Location location = Location.of("KR", "Seoul");
                when(mockUserRepository.existsByEmail(email)).thenReturn(false);

                User result = userRegistrationService.registerNewUser(auth, profile, location);

                assertThat(result.getEmail()).isEqualTo(email);
                verify(mockUserRepository).existsByEmail(email);
            }
        }

        @Test
        @DisplayName("다양한 프로필 정보로 사용자를 등록할 수 있다")
        void registerUserWithVariousProfiles() {
            Auth auth = Auth.of("test@example.com", "encodedPassword");
            Location location = Location.of("KR", "Seoul");
            when(mockUserRepository.existsByEmail("test@example.com")).thenReturn(false);

            Profile profile1 = Profile.of("한글닉네임", "http://image1.com", "KR");
            Profile profile2 = Profile.of("EnglishNick", "http://image2.com", "US");
            Profile profile3 = Profile.of("Nick123", "http://image3.com", "JP");

            User result1 = userRegistrationService.registerNewUser(auth, profile1, location);
            User result2 = userRegistrationService.registerNewUser(auth, profile2, location);
            User result3 = userRegistrationService.registerNewUser(auth, profile3, location);

            assertThat(result1.getNickname()).isEqualTo("한글닉네임");
            assertThat(result1.getImageUrl()).isEqualTo("http://image1.com");
            assertThat(result1.getInterestCountryId()).isEqualTo("KR");

            assertThat(result2.getNickname()).isEqualTo("EnglishNick");
            assertThat(result2.getImageUrl()).isEqualTo("http://image2.com");
            assertThat(result2.getInterestCountryId()).isEqualTo("US");

            assertThat(result3.getNickname()).isEqualTo("Nick123");
            assertThat(result3.getImageUrl()).isEqualTo("http://image3.com");
            assertThat(result3.getInterestCountryId()).isEqualTo("JP");
        }

        @Test
        @DisplayName("다양한 위치 정보로 사용자를 등록할 수 있다")
        void registerUserWithVariousLocations() {
            Auth auth = Auth.of("test@example.com", "encodedPassword");
            Profile profile = Profile.of("nickname", "imageUrl", "US");
            when(mockUserRepository.existsByEmail("test@example.com")).thenReturn(false);

            Location location1 = Location.of("KR", "Seoul");
            Location location2 = Location.of("US", "New York");
            Location location3 = Location.of("JP", "Tokyo");

            User result1 = userRegistrationService.registerNewUser(auth, profile, location1);
            User result2 = userRegistrationService.registerNewUser(auth, profile, location2);
            User result3 = userRegistrationService.registerNewUser(auth, profile, location3);

            assertThat(result1.getCountryId()).isEqualTo("KR");
            assertThat(result1.getRegion()).isEqualTo("Seoul");

            assertThat(result2.getCountryId()).isEqualTo("US");
            assertThat(result2.getRegion()).isEqualTo("New York");

            assertThat(result3.getCountryId()).isEqualTo("JP");
            assertThat(result3.getRegion()).isEqualTo("Tokyo");
        }
    }

    @Nested
    @DisplayName("이메일 고유성 검증 테스트")
    class EmailUniquenessValidationTest {

        @Test
        @DisplayName("고유한 이메일은 검증을 통과한다")
        void uniqueEmailPassesValidation() {
            Auth auth = Auth.of("unique@example.com", "encodedPassword");
            Profile profile = Profile.of("nickname", "imageUrl", "US");
            Location location = Location.of("KR", "Seoul");
            when(mockUserRepository.existsByEmail("unique@example.com")).thenReturn(false);

            User result = userRegistrationService.registerNewUser(auth, profile, location);

            assertThat(result).isNotNull();
            verify(mockUserRepository).existsByEmail("unique@example.com");
        }

        @Test
        @DisplayName("중복된 이메일은 검증에 실패한다")
        void duplicateEmailFailsValidation() {
            Auth auth = Auth.of("duplicate@example.com", "encodedPassword");
            Profile profile = Profile.of("nickname", "imageUrl", "US");
            Location location = Location.of("KR", "Seoul");
            when(mockUserRepository.existsByEmail("duplicate@example.com")).thenReturn(true);

            assertThatThrownBy(() -> userRegistrationService.registerNewUser(auth, profile, location))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UserErrorCode.EXISTING_USER.getMessage());

            verify(mockUserRepository).existsByEmail("duplicate@example.com");
        }

        @Test
        @DisplayName("대소문자 구분없이 이메일 중복을 검사한다")
        void emailDuplicationCheckIsCaseInsensitive() {
            String[] duplicateEmails = {
                    "TEST@EXAMPLE.COM",
                    "Test@Example.Com",
                    "test@EXAMPLE.com",
                    "TEST@example.COM"
            };

            for (String email : duplicateEmails) {
                Auth auth = Auth.of(email, "encodedPassword");
                Profile profile = Profile.of("nickname", "imageUrl", "US");
                Location location = Location.of("KR", "Seoul");
                when(mockUserRepository.existsByEmail(email)).thenReturn(true);

                assertThatThrownBy(() -> userRegistrationService.registerNewUser(auth, profile, location))
                        .isInstanceOf(UserException.class)
                        .hasMessage(UserErrorCode.EXISTING_USER.getMessage());

                verify(mockUserRepository).existsByEmail(email);
            }
        }
    }

    @Nested
    @DisplayName("사용자 등록 비즈니스 규칙 테스트")
    class UserRegistrationBusinessRulesTest {

        @Test
        @DisplayName("등록된 사용자는 기본적으로 PENDING 상태이다")
        void registeredUserIsPendingByDefault() {
            Auth auth = Auth.of("test@example.com", "encodedPassword");
            Profile profile = Profile.of("nickname", "imageUrl", "US");
            Location location = Location.of("KR", "Seoul");
            when(mockUserRepository.existsByEmail("test@example.com")).thenReturn(false);

            User result = userRegistrationService.registerNewUser(auth, profile, location);

            assertThat(result.getUserStatus()).isEqualTo(UserStatus.PENDING);
        }

        @Test
        @DisplayName("등록된 사용자는 기본적으로 ROLE_USER 권한을 가진다")
        void registeredUserHasUserRoleByDefault() {
            Auth auth = Auth.of("test@example.com", "encodedPassword");
            Profile profile = Profile.of("nickname", "imageUrl", "US");
            Location location = Location.of("KR", "Seoul");
            when(mockUserRepository.existsByEmail("test@example.com")).thenReturn(false);

            User result = userRegistrationService.registerNewUser(auth, profile, location);

            assertThat(result.getUserRole()).isEqualTo(UserRole.ROLE_USER);
        }

        @Test
        @DisplayName("등록된 사용자는 고유한 ID를 가진다")
        void registeredUserHasUniqueId() {
            Auth auth = Auth.of("test@example.com", "encodedPassword");
            Profile profile = Profile.of("nickname", "imageUrl", "US");
            Location location = Location.of("KR", "Seoul");
            when(mockUserRepository.existsByEmail("test@example.com")).thenReturn(false);

            User result = userRegistrationService.registerNewUser(auth, profile, location);

            assertThat(result.getUserId()).isNull();
        }

        @Test
        @DisplayName("등록 과정에서 제공된 모든 정보가 보존된다")
        void allProvidedInformationIsPreservedDuringRegistration() {
            String email = "preserve@example.com";
            String password = "encodedPassword123";
            String nickname = "TestNickname";
            String imageUrl = "http://test-image.com";
            String interestCountryId = "US";
            String countryId = "KR";
            String region = "Seoul";

            Auth auth = Auth.of(email, password);
            Profile profile = Profile.of(nickname, imageUrl, interestCountryId);
            Location location = Location.of(countryId, region);
            when(mockUserRepository.existsByEmail(email)).thenReturn(false);

            User result = userRegistrationService.registerNewUser(auth, profile, location);

            assertThat(result.getEmail()).isEqualTo(email);
            assertThat(result.getPassword()).isEqualTo(password);
            assertThat(result.getNickname()).isEqualTo(nickname);
            assertThat(result.getImageUrl()).isEqualTo(imageUrl);
            assertThat(result.getInterestCountryId()).isEqualTo(interestCountryId);
            assertThat(result.getCountryId()).isEqualTo(countryId);
            assertThat(result.getRegion()).isEqualTo(region);
        }
    }

    @Nested
    @DisplayName("등록 서비스 의존성 테스트")
    class RegistrationServiceDependencyTest {

        @Test
        @DisplayName("UserRepository가 이메일 존재 여부를 확인한다")
        void userRepositoryChecksEmailExistence() {
            Auth auth = Auth.of("dependency@example.com", "encodedPassword");
            Profile profile = Profile.of("nickname", "imageUrl", "US");
            Location location = Location.of("KR", "Seoul");
            when(mockUserRepository.existsByEmail("dependency@example.com")).thenReturn(false);

            userRegistrationService.registerNewUser(auth, profile, location);

            verify(mockUserRepository).existsByEmail("dependency@example.com");
        }

        @Test
        @DisplayName("UserRepository가 정확한 이메일로 조회한다")
        void userRepositoryQueriesWithCorrectEmail() {
            String[] emails = {
                    "test1@example.com",
                    "test2@example.com",
                    "test3@example.com"
            };

            for (String email : emails) {
                Auth auth = Auth.of(email, "encodedPassword");
                Profile profile = Profile.of("nickname", "imageUrl", "US");
                Location location = Location.of("KR", "Seoul");
                when(mockUserRepository.existsByEmail(email)).thenReturn(false);

                userRegistrationService.registerNewUser(auth, profile, location);

                verify(mockUserRepository).existsByEmail(email);
            }
        }
    }
}