package com.backend.immilog.user.application.services.command;


import com.backend.immilog.user.domain.enums.UserRole;
import com.backend.immilog.user.domain.enums.UserStatus;
import com.backend.immilog.user.domain.model.*;
import com.backend.immilog.user.domain.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@DisplayName("UserCommandService 테스트")
class UserCommandServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private UserCommandService userCommandService;

    @BeforeEach
    void setUp() {
        userCommandService = new UserCommandService(userRepository);
    }

    private User createMockUser() {
        return User.restore(
                UserId.of("user123"),
                Auth.of("test@example.com", "encodedPassword"),
                UserRole.ROLE_USER,
                Profile.of("테스트유저", "https://example.com/image.jpg", "KR"),
                Location.of("KR", "서울특별시"),
                UserStatus.ACTIVE,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now()
        );
    }

    private User createNewUser() {
        return User.create(
                Auth.of("new@example.com", "password"),
                Profile.of("신규유저", null, "KR"),
                Location.of("KR", "부산광역시")
        );
    }

    @Test
    @DisplayName("사용자를 정상적으로 저장할 수 있다")
    void saveUserSuccessfully() {
        // given
        User user = createMockUser();
        User savedUser = createMockUser();

        given(userRepository.save(user)).willReturn(savedUser);

        // when
        User result = userCommandService.save(user);

        // then
        assertThat(result).isEqualTo(savedUser);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("신규 사용자를 저장할 수 있다")
    void saveNewUser() {
        // given
        User newUser = createNewUser();
        User savedUser = User.restore(
                UserId.of("new123"),
                Auth.of("new@example.com", "password"),
                UserRole.ROLE_USER,
                Profile.of("신규유저", null, "KR"),
                Location.of("KR", "부산광역시"),
                UserStatus.PENDING,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        given(userRepository.save(newUser)).willReturn(savedUser);

        // when
        User result = userCommandService.save(newUser);

        // then
        assertThat(result).isEqualTo(savedUser);
        assertThat(result.getUserId()).isNotNull();
        verify(userRepository).save(newUser);
    }

    @Test
    @DisplayName("다양한 상태의 사용자를 저장할 수 있다")
    void saveUsersWithDifferentStatuses() {
        // given
        User pendingUser = User.restore(
                UserId.of("pending123"),
                Auth.of("pending@example.com", "password"),
                UserRole.ROLE_USER,
                Profile.of("대기유저", null, "KR"),
                Location.of("KR", "서울"),
                UserStatus.PENDING,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        User activeUser = User.restore(
                UserId.of("active123"),
                Auth.of("active@example.com", "password"),
                UserRole.ROLE_USER,
                Profile.of("활성유저", null, "KR"),
                Location.of("KR", "서울"),
                UserStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        User blockedUser = User.restore(
                UserId.of("blocked123"),
                Auth.of("blocked@example.com", "password"),
                UserRole.ROLE_USER,
                Profile.of("차단유저", null, "KR"),
                Location.of("KR", "서울"),
                UserStatus.BLOCKED,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        given(userRepository.save(pendingUser)).willReturn(pendingUser);
        given(userRepository.save(activeUser)).willReturn(activeUser);
        given(userRepository.save(blockedUser)).willReturn(blockedUser);

        // when
        User pendingResult = userCommandService.save(pendingUser);
        User activeResult = userCommandService.save(activeUser);
        User blockedResult = userCommandService.save(blockedUser);

        // then
        assertThat(pendingResult.getUserStatus()).isEqualTo(UserStatus.PENDING);
        assertThat(activeResult.getUserStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(blockedResult.getUserStatus()).isEqualTo(UserStatus.BLOCKED);

        verify(userRepository).save(pendingUser);
        verify(userRepository).save(activeUser);
        verify(userRepository).save(blockedUser);
    }

    @Test
    @DisplayName("다양한 권한의 사용자를 저장할 수 있다")
    void saveUsersWithDifferentRoles() {
        // given
        User regularUser = User.restore(
                UserId.of("regular123"),
                Auth.of("regular@example.com", "password"),
                UserRole.ROLE_USER,
                Profile.of("일반유저", null, "KR"),
                Location.of("KR", "서울"),
                UserStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        User adminUser = User.restore(
                UserId.of("admin123"),
                Auth.of("admin@example.com", "password"),
                UserRole.ROLE_ADMIN,
                Profile.of("관리자", null, "KR"),
                Location.of("KR", "서울"),
                UserStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        given(userRepository.save(regularUser)).willReturn(regularUser);
        given(userRepository.save(adminUser)).willReturn(adminUser);

        // when
        User regularResult = userCommandService.save(regularUser);
        User adminResult = userCommandService.save(adminUser);

        // then
        assertThat(regularResult.getUserRole()).isEqualTo(UserRole.ROLE_USER);
        assertThat(adminResult.getUserRole()).isEqualTo(UserRole.ROLE_ADMIN);

        verify(userRepository).save(regularUser);
        verify(userRepository).save(adminUser);
    }

    @Test
    @DisplayName("다양한 국가의 사용자를 저장할 수 있다")
    void saveUsersFromDifferentCountries() {
        // given
        User koreanUser = User.restore(
                UserId.of("korean123"),
                Auth.of("korean@example.com", "password"),
                UserRole.ROLE_USER,
                Profile.of("한국유저", null, "KR"),
                Location.of("KR", "서울"),
                UserStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        User japaneseUser = User.restore(
                UserId.of("japanese123"),
                Auth.of("japanese@example.com", "password"),
                UserRole.ROLE_USER,
                Profile.of("일본유저", null, "JP"),
                Location.of("JP", "도쿄"),
                UserStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        given(userRepository.save(koreanUser)).willReturn(koreanUser);
        given(userRepository.save(japaneseUser)).willReturn(japaneseUser);

        // when
        User koreanResult = userCommandService.save(koreanUser);
        User japaneseResult = userCommandService.save(japaneseUser);

        // then
        assertThat(koreanResult.getCountryId()).isEqualTo("KR");
        assertThat(japaneseResult.getCountryId()).isEqualTo("JP");

        verify(userRepository).save(koreanUser);
        verify(userRepository).save(japaneseUser);
    }

    @Test
    @DisplayName("프로필이 업데이트된 사용자를 저장할 수 있다")
    void saveUserWithUpdatedProfile() {
        // given
        User user = createMockUser();
        Profile newProfile = Profile.of("업데이트된유저", "https://new.example.com/image.jpg", "JP");
        user.updateProfile(newProfile);

        given(userRepository.save(user)).willReturn(user);

        // when
        User result = userCommandService.save(user);

        // then
        assertThat(result.getNickname()).isEqualTo("업데이트된유저");
        assertThat(result.getImageUrl()).isEqualTo("https://new.example.com/image.jpg");
        assertThat(result.getInterestCountryId()).isEqualTo("JP");
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("위치가 업데이트된 사용자를 저장할 수 있다")
    void saveUserWithUpdatedLocation() {
        // given
        User user = createMockUser();
        Location newLocation = Location.of("JP", "오사카");
        user.updateLocation(newLocation);

        given(userRepository.save(user)).willReturn(user);

        // when
        User result = userCommandService.save(user);

        // then
        assertThat(result.getCountryId()).isEqualTo("JP");
        assertThat(result.getRegion()).isEqualTo("오사카");
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("비밀번호가 변경된 사용자를 저장할 수 있다")
    void saveUserWithChangedPassword() {
        // given
        User user = createMockUser();
        String newPassword = "newEncodedPassword123";
        user.changePassword(newPassword);

        given(userRepository.save(user)).willReturn(user);

        // when
        User result = userCommandService.save(user);

        // then
        assertThat(result.getPassword()).isEqualTo(newPassword);
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("여러 번 연속으로 사용자를 저장할 수 있다")
    void saveMultipleUsersSequentially() {
        // given
        User user1 = createMockUser();
        User user2 = createNewUser();
        User user3 = User.restore(
                UserId.of("user3"),
                Auth.of("user3@example.com", "password"),
                UserRole.ROLE_ADMIN,
                Profile.of("유저3", null, "JP"),
                Location.of("JP", "교토"),
                UserStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        given(userRepository.save(user1)).willReturn(user1);
        given(userRepository.save(user2)).willReturn(user2);
        given(userRepository.save(user3)).willReturn(user3);

        // when
        User result1 = userCommandService.save(user1);
        User result2 = userCommandService.save(user2);
        User result3 = userCommandService.save(user3);

        // then
        assertThat(result1).isEqualTo(user1);
        assertThat(result2).isEqualTo(user2);
        assertThat(result3).isEqualTo(user3);

        verify(userRepository, times(3)).save(any(User.class));
    }

    @Test
    @DisplayName("트랜잭션이 적용되어 저장이 수행된다")
    void saveUserWithTransaction() {
        // given
        User user = createMockUser();

        given(userRepository.save(user)).willReturn(user);

        // when
        User result = userCommandService.save(user);

        // then
        assertThat(result).isEqualTo(user);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("동일한 사용자를 여러 번 저장할 수 있다")
    void saveSameUserMultipleTimes() {
        // given
        User user = createMockUser();

        given(userRepository.save(user)).willReturn(user);

        // when
        User result1 = userCommandService.save(user);
        User result2 = userCommandService.save(user);
        User result3 = userCommandService.save(user);

        // then
        assertThat(result1).isEqualTo(user);
        assertThat(result2).isEqualTo(user);
        assertThat(result3).isEqualTo(user);

        verify(userRepository, times(3)).save(user);
    }

    @Test
    @DisplayName("복합적인 변경사항이 있는 사용자를 저장할 수 있다")
    void saveUserWithMultipleChanges() {
        // given
        User user = createMockUser();

        // 여러 변경사항 적용
        user.changePassword("newPassword123");
        user.updateProfile(Profile.of("새닉네임", "https://new.com/image.jpg", "JP"));
        user.updateLocation(Location.of("JP", "나고야"));
        user.changeStatus(UserStatus.BLOCKED);

        given(userRepository.save(user)).willReturn(user);

        // when
        User result = userCommandService.save(user);

        // then
        assertThat(result.getPassword()).isEqualTo("newPassword123");
        assertThat(result.getNickname()).isEqualTo("새닉네임");
        assertThat(result.getImageUrl()).isEqualTo("https://new.com/image.jpg");
        assertThat(result.getInterestCountryId()).isEqualTo("JP");
        assertThat(result.getCountryId()).isEqualTo("JP");
        assertThat(result.getRegion()).isEqualTo("나고야");
        assertThat(result.getUserStatus()).isEqualTo(UserStatus.BLOCKED);

        verify(userRepository).save(user);
    }
}