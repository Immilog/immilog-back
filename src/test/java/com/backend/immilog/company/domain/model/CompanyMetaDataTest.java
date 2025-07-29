package com.backend.immilog.company.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("CompanyMetaData 도메인 모델 테스트")
class CompanyMetaDataTest {

    @Test
    @DisplayName("유효한 파라미터로 CompanyMetaData를 생성할 수 있다")
    void shouldCreateCompanyMetaDataWithValidParameters() {
        // given
        Industry industry = Industry.IT;
        String name = "테스트 회사";
        String email = "test@company.com";
        String phone = "010-1234-5678";
        String address = "서울시 강남구";
        String homepage = "https://company.com";
        String logo = "logo.png";

        // when
        CompanyMetaData metaData = CompanyMetaData.of(industry, name, email, phone, address, homepage, logo);

        // then
        assertThat(metaData.industry()).isEqualTo(industry);
        assertThat(metaData.name()).isEqualTo(name);
        assertThat(metaData.email()).isEqualTo(email);
        assertThat(metaData.phone()).isEqualTo(phone);
        assertThat(metaData.address()).isEqualTo(address);
        assertThat(metaData.homepage()).isEqualTo(homepage);
        assertThat(metaData.logo()).isEqualTo(logo);
    }

    @Test
    @DisplayName("industry가 null이면 예외가 발생한다")
    void shouldThrowExceptionWhenIndustryIsNull() {
        // given
        Industry industry = null;
        String name = "테스트 회사";
        String email = "test@company.com";
        String phone = "010-1234-5678";

        // when & then
        assertThatThrownBy(() -> CompanyMetaData.of(industry, name, email, phone, null, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Industry cannot be null");
    }

    @Test
    @DisplayName("name이 null이면 예외가 발생한다")
    void shouldThrowExceptionWhenNameIsNull() {
        // given
        Industry industry = Industry.IT;
        String name = null;
        String email = "test@company.com";
        String phone = "010-1234-5678";

        // when & then
        assertThatThrownBy(() -> CompanyMetaData.of(industry, name, email, phone, null, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Company name cannot be null or empty");
    }

    @Test
    @DisplayName("name이 공백이면 예외가 발생한다")
    void shouldThrowExceptionWhenNameIsEmpty() {
        // given
        Industry industry = Industry.IT;
        String name = "   ";
        String email = "test@company.com";
        String phone = "010-1234-5678";

        // when & then
        assertThatThrownBy(() -> CompanyMetaData.of(industry, name, email, phone, null, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Company name cannot be null or empty");
    }

    @Test
    @DisplayName("잘못된 이메일 형식이면 예외가 발생한다")
    void shouldThrowExceptionWhenEmailFormatIsInvalid() {
        // given
        Industry industry = Industry.IT;
        String name = "테스트 회사";
        String email = "invalid-email";
        String phone = "010-1234-5678";

        // when & then
        assertThatThrownBy(() -> CompanyMetaData.of(industry, name, email, phone, null, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid email format");
    }

    @Test
    @DisplayName("email이 null이면 예외가 발생하지 않는다")
    void shouldNotThrowExceptionWhenEmailIsNull() {
        // given
        Industry industry = Industry.IT;
        String name = "테스트 회사";
        String email = null;
        String phone = "010-1234-5678";

        // when
        CompanyMetaData metaData = CompanyMetaData.of(industry, name, email, phone, null, null, null);

        // then
        assertThat(metaData.email()).isNull();
    }

    @Test
    @DisplayName("잘못된 전화번호 형식이면 예외가 발생한다")
    void shouldThrowExceptionWhenPhoneFormatIsInvalid() {
        // given
        Industry industry = Industry.IT;
        String name = "테스트 회사";
        String email = "test@company.com";
        String phone = "invalid-phone";

        // when & then
        assertThatThrownBy(() -> CompanyMetaData.of(industry, name, email, phone, null, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid phone format");
    }

    @Test
    @DisplayName("phone이 null이면 예외가 발생하지 않는다")
    void shouldNotThrowExceptionWhenPhoneIsNull() {
        // given
        Industry industry = Industry.IT;
        String name = "테스트 회사";
        String email = "test@company.com";
        String phone = null;

        // when
        CompanyMetaData metaData = CompanyMetaData.of(industry, name, email, phone, null, null, null);

        // then
        assertThat(metaData.phone()).isNull();
    }

    @Test
    @DisplayName("createEmpty로 빈 CompanyMetaData를 생성할 수 있다")
    void shouldCreateEmptyCompanyMetaData() {
        // when
        CompanyMetaData metaData = CompanyMetaData.createEmpty();

        // then
        assertThat(metaData.industry()).isNull();
        assertThat(metaData.name()).isNull();
        assertThat(metaData.email()).isNull();
        assertThat(metaData.phone()).isNull();
        assertThat(metaData.address()).isNull();
        assertThat(metaData.homepage()).isNull();
        assertThat(metaData.logo()).isNull();
    }

    @Test
    @DisplayName("withIndustry로 새로운 industry를 가진 CompanyMetaData를 생성할 수 있다")
    void shouldCreateCompanyMetaDataWithNewIndustry() {
        // given
        CompanyMetaData original = CompanyMetaData.of(Industry.IT, "회사명", "test@test.com", "010-1234-5678", null, null, null);
        Industry newIndustry = Industry.ETC;

        // when
        CompanyMetaData updated = original.withIndustry(newIndustry);

        // then
        assertThat(updated.industry()).isEqualTo(newIndustry);
        assertThat(updated.name()).isEqualTo(original.name());
        assertThat(updated).isNotSameAs(original);
    }

    @Test
    @DisplayName("withName으로 새로운 name을 가진 CompanyMetaData를 생성할 수 있다")
    void shouldCreateCompanyMetaDataWithNewName() {
        // given
        CompanyMetaData original = CompanyMetaData.of(Industry.IT, "회사명", "test@test.com", "010-1234-5678", null, null, null);
        String newName = "새로운 회사명";

        // when
        CompanyMetaData updated = original.withName(newName);

        // then
        assertThat(updated.name()).isEqualTo(newName);
        assertThat(updated.industry()).isEqualTo(original.industry());
        assertThat(updated).isNotSameAs(original);
    }

    @Test
    @DisplayName("withEmail로 새로운 email을 가진 CompanyMetaData를 생성할 수 있다")
    void shouldCreateCompanyMetaDataWithNewEmail() {
        // given
        CompanyMetaData original = CompanyMetaData.of(Industry.IT, "회사명", "test@test.com", "010-1234-5678", null, null, null);
        String newEmail = "new@test.com";

        // when
        CompanyMetaData updated = original.withEmail(newEmail);

        // then
        assertThat(updated.email()).isEqualTo(newEmail);
        assertThat(updated.name()).isEqualTo(original.name());
        assertThat(updated).isNotSameAs(original);
    }

    @Test
    @DisplayName("withPhone으로 새로운 phone을 가진 CompanyMetaData를 생성할 수 있다")
    void shouldCreateCompanyMetaDataWithNewPhone() {
        // given
        CompanyMetaData original = CompanyMetaData.of(Industry.IT, "회사명", "test@test.com", "010-1234-5678", null, null, null);
        String newPhone = "010-9876-5432";

        // when
        CompanyMetaData updated = original.withPhone(newPhone);

        // then
        assertThat(updated.phone()).isEqualTo(newPhone);
        assertThat(updated.name()).isEqualTo(original.name());
        assertThat(updated).isNotSameAs(original);
    }

    @Test
    @DisplayName("withAddress로 새로운 address를 가진 CompanyMetaData를 생성할 수 있다")
    void shouldCreateCompanyMetaDataWithNewAddress() {
        // given
        CompanyMetaData original = CompanyMetaData.of(Industry.IT, "회사명", "test@test.com", "010-1234-5678", "기존 주소", null, null);
        String newAddress = "새로운 주소";

        // when
        CompanyMetaData updated = original.withAddress(newAddress);

        // then
        assertThat(updated.address()).isEqualTo(newAddress);
        assertThat(updated.name()).isEqualTo(original.name());
        assertThat(updated).isNotSameAs(original);
    }

    @Test
    @DisplayName("withHomepage로 새로운 homepage를 가진 CompanyMetaData를 생성할 수 있다")
    void shouldCreateCompanyMetaDataWithNewHomepage() {
        // given
        CompanyMetaData original = CompanyMetaData.of(Industry.IT, "회사명", "test@test.com", "010-1234-5678", null, "https://old.com", null);
        String newHomepage = "https://new.com";

        // when
        CompanyMetaData updated = original.withHomepage(newHomepage);

        // then
        assertThat(updated.homepage()).isEqualTo(newHomepage);
        assertThat(updated.name()).isEqualTo(original.name());
        assertThat(updated).isNotSameAs(original);
    }

    @Test
    @DisplayName("withLogo로 새로운 logo를 가진 CompanyMetaData를 생성할 수 있다")
    void shouldCreateCompanyMetaDataWithNewLogo() {
        // given
        CompanyMetaData original = CompanyMetaData.of(Industry.IT, "회사명", "test@test.com", "010-1234-5678", null, null, "old-logo.png");
        String newLogo = "new-logo.png";

        // when
        CompanyMetaData updated = original.withLogo(newLogo);

        // then
        assertThat(updated.logo()).isEqualTo(newLogo);
        assertThat(updated.name()).isEqualTo(original.name());
        assertThat(updated).isNotSameAs(original);
    }
}