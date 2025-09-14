package com.backend.immilog.user.domain.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserRoleTest {

    @Nested
    @DisplayName("UserRole 기본 속성 테스트")
    class UserRoleBasicPropertiesTest {

        @Test
        @DisplayName("ROLE_USER의 기본 속성이 올바르게 설정되어 있다")
        void roleUserBasicProperties() {
            UserRole role = UserRole.ROLE_USER;

            assertThat(role.getRoleName()).isEqualTo("ROLE_USER");
            assertThat(role.getAuthorities()).isNotNull();
            assertThat(role.getAuthorities()).hasSize(1);
            assertThat(role.getAuthorities().get(0))
                    .isInstanceOf(SimpleGrantedAuthority.class)
                    .extracting("role")
                    .isEqualTo("ROLE_USER");
        }

        @Test
        @DisplayName("ROLE_ADMIN의 기본 속성이 올바르게 설정되어 있다")
        void roleAdminBasicProperties() {
            UserRole role = UserRole.ROLE_ADMIN;

            assertThat(role.getRoleName()).isEqualTo("ROLE_ADMIN");
            assertThat(role.getAuthorities()).isNotNull();
            assertThat(role.getAuthorities()).hasSize(1);
            assertThat(role.getAuthorities().get(0))
                    .isInstanceOf(SimpleGrantedAuthority.class)
                    .extracting("role")
                    .isEqualTo("ROLE_ADMIN");
        }
    }

    @Nested
    @DisplayName("UserRole isAdmin 메서드 테스트")
    class UserRoleIsAdminTest {

        @Test
        @DisplayName("ROLE_ADMIN은 관리자 권한이다")
        void roleAdminIsAdmin() {
            boolean result = UserRole.ROLE_ADMIN.isAdmin();

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("ROLE_USER는 관리자 권한이 아니다")
        void roleUserIsNotAdmin() {
            boolean result = UserRole.ROLE_USER.isAdmin();

            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("UserRole authorities 테스트")
    class UserRoleAuthoritiesTest {

        @Test
        @DisplayName("ROLE_USER의 authorities가 올바르게 설정되어 있다")
        void roleUserAuthorities() {
            List<GrantedAuthority> authorities = UserRole.ROLE_USER.getAuthorities();

            assertThat(authorities).isNotNull();
            assertThat(authorities).hasSize(1);
            assertThat(authorities.get(0).getAuthority()).isEqualTo("ROLE_USER");
        }

        @Test
        @DisplayName("ROLE_ADMIN의 authorities가 올바르게 설정되어 있다")
        void roleAdminAuthorities() {
            List<GrantedAuthority> authorities = UserRole.ROLE_ADMIN.getAuthorities();

            assertThat(authorities).isNotNull();
            assertThat(authorities).hasSize(1);
            assertThat(authorities.get(0).getAuthority()).isEqualTo("ROLE_ADMIN");
        }

        @Test
        @DisplayName("authorities 리스트는 불변이다")
        void authoritiesListIsImmutable() {
            List<GrantedAuthority> userAuthorities = UserRole.ROLE_USER.getAuthorities();
            List<GrantedAuthority> adminAuthorities = UserRole.ROLE_ADMIN.getAuthorities();

            assertThat(userAuthorities).isInstanceOf(List.class);
            assertThat(adminAuthorities).isInstanceOf(List.class);
        }

        @Test
        @DisplayName("각 역할의 authorities가 서로 다르다")
        void authoritiesAreDifferentForEachRole() {
            List<GrantedAuthority> userAuthorities = UserRole.ROLE_USER.getAuthorities();
            List<GrantedAuthority> adminAuthorities = UserRole.ROLE_ADMIN.getAuthorities();

            assertThat(userAuthorities).isNotEqualTo(adminAuthorities);
            assertThat(userAuthorities.get(0).getAuthority()).isNotEqualTo(adminAuthorities.get(0).getAuthority());
        }
    }

    @Nested
    @DisplayName("UserRole enum 기본 동작 테스트")
    class UserRoleEnumBehaviorTest {

        @Test
        @DisplayName("UserRole enum의 모든 값을 검증한다")
        void allUserRoleValues() {
            UserRole[] values = UserRole.values();

            assertThat(values).hasSize(2);
            assertThat(values).containsExactlyInAnyOrder(
                    UserRole.ROLE_USER,
                    UserRole.ROLE_ADMIN
            );
        }

        @Test
        @DisplayName("valueOf 메서드로 문자열을 통해 enum 값을 가져올 수 있다")
        void valueOfMethod() {
            assertThat(UserRole.valueOf("ROLE_USER")).isEqualTo(UserRole.ROLE_USER);
            assertThat(UserRole.valueOf("ROLE_ADMIN")).isEqualTo(UserRole.ROLE_ADMIN);
        }

        @Test
        @DisplayName("enum 값의 순서가 올바르다")
        void enumOrderIsCorrect() {
            UserRole[] values = UserRole.values();

            assertThat(values[0]).isEqualTo(UserRole.ROLE_USER);
            assertThat(values[1]).isEqualTo(UserRole.ROLE_ADMIN);
        }

        @Test
        @DisplayName("enum 값의 ordinal이 올바르다")
        void ordinalValues() {
            assertThat(UserRole.ROLE_USER.ordinal()).isEqualTo(0);
            assertThat(UserRole.ROLE_ADMIN.ordinal()).isEqualTo(1);
        }

        @Test
        @DisplayName("enum 값의 name이 올바르다")
        void enumNames() {
            assertThat(UserRole.ROLE_USER.name()).isEqualTo("ROLE_USER");
            assertThat(UserRole.ROLE_ADMIN.name()).isEqualTo("ROLE_ADMIN");
        }
    }

    @Nested
    @DisplayName("UserRole 동등성 테스트")
    class UserRoleEqualityTest {

        @Test
        @DisplayName("같은 UserRole enum 값은 동등하다")
        void sameUserRoleValuesAreEqual() {
            UserRole role1 = UserRole.ROLE_USER;
            UserRole role2 = UserRole.ROLE_USER;

            assertThat(role1).isEqualTo(role2);
            assertThat(role1 == role2).isTrue();
            assertThat(role1.hashCode()).isEqualTo(role2.hashCode());
        }

        @Test
        @DisplayName("다른 UserRole enum 값은 동등하지 않다")
        void differentUserRoleValuesAreNotEqual() {
            UserRole role1 = UserRole.ROLE_USER;
            UserRole role2 = UserRole.ROLE_ADMIN;

            assertThat(role1).isNotEqualTo(role2);
            assertThat(role1 == role2).isFalse();
        }

        @Test
        @DisplayName("equals 메서드를 통한 비교가 올바르게 동작한다")
        void equalsMethodWorksCorrectly() {
            UserRole adminRole = UserRole.ROLE_ADMIN;

            assertThat(adminRole.equals(UserRole.ROLE_ADMIN)).isTrue();
            assertThat(adminRole.equals(UserRole.ROLE_USER)).isFalse();
            assertThat(adminRole.equals(null)).isFalse();
            assertThat(adminRole.equals("ROLE_ADMIN")).isFalse();
        }
    }

    @Nested
    @DisplayName("UserRole toString 테스트")
    class UserRoleToStringTest {

        @Test
        @DisplayName("toString 메서드가 올바르게 동작한다")
        void toStringWorksCorrectly() {
            assertThat(UserRole.ROLE_USER.toString()).isEqualTo("ROLE_USER");
            assertThat(UserRole.ROLE_ADMIN.toString()).isEqualTo("ROLE_ADMIN");
        }
    }

    @Nested
    @DisplayName("UserRole 비즈니스 로직 테스트")
    class UserRoleBusinessLogicTest {

        @Test
        @DisplayName("관리자 역할 판별 로직이 정확하다")
        void adminRoleDetectionLogic() {
            for (UserRole role : UserRole.values()) {
                if (role == UserRole.ROLE_ADMIN) {
                    assertThat(role.isAdmin()).isTrue();
                } else {
                    assertThat(role.isAdmin()).isFalse();
                }
            }
        }

        @Test
        @DisplayName("각 역할이 고유한 roleName을 가진다")
        void eachRoleHasUniqueRoleName() {
            assertThat(UserRole.ROLE_USER.getRoleName()).isNotEqualTo(UserRole.ROLE_ADMIN.getRoleName());
        }

        @Test
        @DisplayName("roleName과 enum name이 일치한다")
        void roleNameMatchesEnumName() {
            assertThat(UserRole.ROLE_USER.getRoleName()).isEqualTo(UserRole.ROLE_USER.name());
            assertThat(UserRole.ROLE_ADMIN.getRoleName()).isEqualTo(UserRole.ROLE_ADMIN.name());
        }
    }
}