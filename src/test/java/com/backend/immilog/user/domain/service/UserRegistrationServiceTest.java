package com.backend.immilog.user.domain.service;

import com.backend.immilog.shared.enums.Country;
import com.backend.immilog.user.domain.enums.UserRole;
import com.backend.immilog.user.domain.enums.UserStatus;
import com.backend.immilog.user.domain.model.Auth;
import com.backend.immilog.user.domain.model.Location;
import com.backend.immilog.user.domain.model.Profile;
import com.backend.immilog.user.domain.model.User;
import com.backend.immilog.user.domain.repositories.UserRepository;
import com.backend.immilog.user.exception.UserErrorCode;
import com.backend.immilog.user.exception.UserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

@DisplayName("UserRegistrationService 테스트")
@ExtendWith(MockitoExtension.class)
class UserRegistrationServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserRegistrationService userRegistrationService;

    @BeforeEach
    void setUp() {
        userRegistrationService = new UserRegistrationService(userRepository);
    }

    private Auth createValidAuth() {
        return Auth.of("test@example.com", "encodedPassword123");
    }

    private Profile createValidProfile() {
        return Profile.of("테스트유저", "https://example.com/image.jpg", Country.SOUTH_KOREA);
    }

    private Location createValidLocation() {
        return Location.of(Country.SOUTH_KOREA, "서울특별시");
    }

    @Test
    @DisplayName("새로운 사용자를 정상적으로 등록할 수 있다")
    void registerNewUserSuccessfully() {
        // given
        Auth auth = createValidAuth();
        Profile profile = createValidProfile();
        Location location = createValidLocation();

        given(userRepository.existsByEmail(auth.email())).willReturn(false);

        // when
        User registeredUser = userRegistrationService.registerNewUser(auth, profile, location);

        // then
        assertThat(registeredUser).isNotNull();
        assertThat(registeredUser.getUserId()).isNull();
        assertThat(registeredUser.getAuth()).isEqualTo(auth);
        assertThat(registeredUser.getUserRole()).isEqualTo(UserRole.ROLE_USER);
        assertThat(registeredUser.getProfile()).isEqualTo(profile);
        assertThat(registeredUser.getLocation()).isEqualTo(location);
        assertThat(registeredUser.getUserStatus()).isEqualTo(UserStatus.PENDING);
        assertThat(registeredUser.getCreatedAt()).isNotNull();
        assertThat(registeredUser.getUpdatedAt()).isNotNull();

        verify(userRepository).existsByEmail(auth.email());
    }

    @Test
    @DisplayName("이미 존재하는 이메일로 등록 시 예외가 발생한다")
    void registerNewUserWithExistingEmail() {
        // given
        Auth auth = createValidAuth();
        Profile profile = createValidProfile();
        Location location = createValidLocation();

        given(userRepository.existsByEmail(auth.email())).willReturn(true);

        // when & then
        UserException exception = assertThrows(UserException.class,
                () -> userRegistrationService.registerNewUser(auth, profile, location));

        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.EXISTING_USER);
        verify(userRepository).existsByEmail(auth.email());
    }

    @Test
    @DisplayName("여러 다른 이메일로 사용자를 등록할 수 있다")
    void registerMultipleUsersWithDifferentEmails() {
        // given
        Auth auth1 = Auth.of("user1@example.com", "password1");
        Auth auth2 = Auth.of("user2@example.com", "password2");
        Profile profile = createValidProfile();
        Location location = createValidLocation();

        given(userRepository.existsByEmail("user1@example.com")).willReturn(false);
        given(userRepository.existsByEmail("user2@example.com")).willReturn(false);

        // when
        User user1 = userRegistrationService.registerNewUser(auth1, profile, location);
        User user2 = userRegistrationService.registerNewUser(auth2, profile, location);

        // then
        assertThat(user1.getEmail()).isEqualTo("user1@example.com");
        assertThat(user2.getEmail()).isEqualTo("user2@example.com");
        assertThat(user1.getPassword()).isEqualTo("password1");
        assertThat(user2.getPassword()).isEqualTo("password2");

        verify(userRepository).existsByEmail("user1@example.com");
        verify(userRepository).existsByEmail("user2@example.com");
    }

    @Test
    @DisplayName("다양한 프로필과 위치 정보로 사용자를 등록할 수 있다")
    void registerUserWithDifferentProfilesAndLocations() {
        // given
        Auth auth = createValidAuth();
        Profile koreanProfile = Profile.of("한국유저", null, Country.SOUTH_KOREA);
        Profile japanProfile = Profile.of("日本ユーザー", "https://japan.com/img.jpg", Country.JAPAN);
        Location seoulLocation = Location.of(Country.SOUTH_KOREA, "서울특별시");
        Location tokyoLocation = Location.of(Country.JAPAN, "도쿄");

        given(userRepository.existsByEmail(auth.email())).willReturn(false);

        // when
        User koreanUser = userRegistrationService.registerNewUser(auth, koreanProfile, seoulLocation);

        // then
        assertThat(koreanUser.getNickname()).isEqualTo("한국유저");
        assertThat(koreanUser.getImageUrl()).isNull();
        assertThat(koreanUser.getInterestCountry()).isEqualTo(Country.SOUTH_KOREA);
        assertThat(koreanUser.getCountry()).isEqualTo(Country.SOUTH_KOREA);
        assertThat(koreanUser.getRegion()).isEqualTo("서울특별시");
    }

    @Test
    @DisplayName("잘못된 Auth로 등록 시 검증 예외가 전파된다")
    void registerUserWithInvalidAuth() {
        // given
        Profile profile = createValidProfile();
        Location location = createValidLocation();

        // when & then
        // null 이메일로 Auth 생성 시 Auth 생성자에서 예외 발생
        assertThrows(UserException.class,
                () -> userRegistrationService.registerNewUser(
                        Auth.of(null, "password"), profile, location));

        // 잘못된 이메일 형식으로 Auth 생성 시 Auth 생성자에서 예외 발생
        assertThrows(UserException.class,
                () -> userRegistrationService.registerNewUser(
                        Auth.of("invalid-email", "password"), profile, location));

        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("잘못된 Profile로 등록 시 검증 예외가 전파된다")
    void registerUserWithInvalidProfile() {
        // given
        Auth auth = createValidAuth();
        Location location = createValidLocation();

        // when & then
        // null 닉네임으로 Profile 생성 시 Profile 생성자에서 예외 발생
        assertThrows(UserException.class,
                () -> userRegistrationService.registerNewUser(
                        auth, Profile.of(null, "image.jpg", Country.SOUTH_KOREA), location));

        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("잘못된 Location으로 등록 시 검증 예외가 전파된다")
    void registerUserWithInvalidLocation() {
        // given
        Auth auth = createValidAuth();
        Profile profile = createValidProfile();

        // when & then
        // null 국가로 Location 생성 시 Location 생성자에서 예외 발생
        assertThrows(UserException.class,
                () -> userRegistrationService.registerNewUser(
                        auth, profile, Location.of(null, "서울")));

        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("UserRepository가 null을 반환해도 정상 동작한다")
    void handleNullFromRepository() {
        // given
        Auth auth = createValidAuth();
        Profile profile = createValidProfile();
        Location location = createValidLocation();

        given(userRepository.existsByEmail(auth.email())).willReturn(false);

        // when
        User registeredUser = userRegistrationService.registerNewUser(auth, profile, location);

        // then
        assertThat(registeredUser).isNotNull();
        verify(userRepository).existsByEmail(auth.email());
    }
}