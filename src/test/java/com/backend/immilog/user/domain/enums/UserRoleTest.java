package com.backend.immilog.user.domain.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("UserRole enum 테스트")
class UserRoleTest {

    @Test
    @DisplayName("ROLE_USER의 속성이 올바르게 설정되어 있다")
    void roleUserProperties() {
        // given
        UserRole roleUser = UserRole.ROLE_USER;

        // when & then
        assertThat(roleUser.getRoleName()).isEqualTo("ROLE_USER");
        assertThat(roleUser.getAuthorities()).hasSize(1);
        assertThat(roleUser.getAuthorities().get(0))
                .isEqualTo(new SimpleGrantedAuthority("ROLE_USER"));
        assertThat(roleUser.isAdmin()).isFalse();
    }

    @Test
    @DisplayName("ROLE_ADMIN의 속성이 올바르게 설정되어 있다")
    void roleAdminProperties() {
        // given
        UserRole roleAdmin = UserRole.ROLE_ADMIN;

        // when & then
        assertThat(roleAdmin.getRoleName()).isEqualTo("ROLE_ADMIN");
        assertThat(roleAdmin.getAuthorities()).hasSize(1);
        assertThat(roleAdmin.getAuthorities().get(0))
                .isEqualTo(new SimpleGrantedAuthority("ROLE_ADMIN"));
        assertThat(roleAdmin.isAdmin()).isTrue();
    }

    @Test
    @DisplayName("모든 UserRole 값이 존재한다")
    void allUserRoleValues() {
        // given
        UserRole[] roles = UserRole.values();

        // when & then
        assertThat(roles).hasSize(2);
        assertThat(roles).contains(UserRole.ROLE_USER, UserRole.ROLE_ADMIN);
    }

    @Test
    @DisplayName("UserRole의 authorities는 immutable하다")
    void authoritiesAreImmutable() {
        // given
        UserRole roleUser = UserRole.ROLE_USER;

        // when & then
        assertThatThrownBy(() -> roleUser.getAuthorities().add(new SimpleGrantedAuthority("NEW_ROLE")))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("각 UserRole은 고유한 GrantedAuthority를 가진다")
    void uniqueGrantedAuthorities() {
        // given
        UserRole roleUser = UserRole.ROLE_USER;
        UserRole roleAdmin = UserRole.ROLE_ADMIN;

        // when
        GrantedAuthority userAuthority = roleUser.getAuthorities().get(0);
        GrantedAuthority adminAuthority = roleAdmin.getAuthorities().get(0);

        // then
        assertThat(userAuthority).isNotEqualTo(adminAuthority);
        assertThat(userAuthority.getAuthority()).isEqualTo("ROLE_USER");
        assertThat(adminAuthority.getAuthority()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    @DisplayName("UserRole enum의 동등성이 정상 작동한다")
    void userRoleEquality() {
        // given
        UserRole role1 = UserRole.ROLE_USER;
        UserRole role2 = UserRole.ROLE_USER;
        UserRole role3 = UserRole.ROLE_ADMIN;

        // when & then
        assertThat(role1).isEqualTo(role2);
        assertThat(role1).isNotEqualTo(role3);
        assertThat(role1.hashCode()).isEqualTo(role2.hashCode());
    }

    @Test
    @DisplayName("UserRole의 toString이 정상 작동한다")
    void userRoleToString() {
        // given
        UserRole roleUser = UserRole.ROLE_USER;
        UserRole roleAdmin = UserRole.ROLE_ADMIN;

        // when
        String userString = roleUser.toString();
        String adminString = roleAdmin.toString();

        // then
        assertThat(userString).isEqualTo("ROLE_USER");
        assertThat(adminString).isEqualTo("ROLE_ADMIN");
    }
}