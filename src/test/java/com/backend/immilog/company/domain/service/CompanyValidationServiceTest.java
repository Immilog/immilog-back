package com.backend.immilog.company.domain.service;

import com.backend.immilog.company.domain.model.Company;
import com.backend.immilog.company.domain.model.CompanyManager;
import com.backend.immilog.company.domain.model.CompanyMetaData;
import com.backend.immilog.company.domain.model.Industry;
import com.backend.immilog.company.exception.CompanyException;
import com.backend.immilog.shared.enums.Country;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("CompanyValidationService 도메인 서비스 테스트")
class CompanyValidationServiceTest {

    private final CompanyValidationService companyValidationService = new CompanyValidationService();

    @Test
    @DisplayName("유효한 Company가 존재할 때 예외가 발생하지 않는다")
    void shouldNotThrowExceptionWhenCompanyExists() {
        // given
        CompanyManager manager = CompanyManager.of(Country.SOUTH_KOREA, "서울", "1");
        CompanyMetaData metaData = CompanyMetaData.of(Industry.IT, "테스트 회사", "test@company.com", "010-1234-5678", null, null, null);
        Company company = new Company("1", manager, metaData);

        // when & then
        assertThatCode(() -> companyValidationService.validateCompanyExists(company))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Company가 null이면 예외가 발생한다")
    void shouldThrowExceptionWhenCompanyIsNull() {
        // given
        Company company = null;

        // when & then
        assertThatThrownBy(() -> companyValidationService.validateCompanyExists(company))
                .isInstanceOf(CompanyException.class)
                .hasMessage("Company not found.");
    }

    @Test
    @DisplayName("Company가 비어있으면 예외가 발생한다")
    void shouldThrowExceptionWhenCompanyIsEmpty() {
        // given
        Company company = Company.createEmpty();

        // when & then
        assertThatThrownBy(() -> companyValidationService.validateCompanyExists(company))
                .isInstanceOf(CompanyException.class)
                .hasMessage("Company not found.");
    }

    @Test
    @DisplayName("유효한 회사명일 때 예외가 발생하지 않는다")
    void shouldNotThrowExceptionWhenCompanyNameIsValid() {
        // given
        String validName = "테스트 회사";

        // when & then
        assertThatCode(() -> companyValidationService.validateCompanyName(validName))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("회사명이 null이면 예외가 발생한다")
    void shouldThrowExceptionWhenCompanyNameIsNull() {
        // given
        String name = null;

        // when & then
        assertThatThrownBy(() -> companyValidationService.validateCompanyName(name))
                .isInstanceOf(CompanyException.class)
                .hasMessage("Company name cannot be empty.");
    }

    @Test
    @DisplayName("회사명이 공백이면 예외가 발생한다")
    void shouldThrowExceptionWhenCompanyNameIsEmpty() {
        // given
        String name = "   ";

        // when & then
        assertThatThrownBy(() -> companyValidationService.validateCompanyName(name))
                .isInstanceOf(CompanyException.class)
                .hasMessage("Company name cannot be empty.");
    }

    @Test
    @DisplayName("회사명이 100자를 초과하면 예외가 발생한다")
    void shouldThrowExceptionWhenCompanyNameIsTooString() {
        // given
        String longName = "a".repeat(101);

        // when & then
        assertThatThrownBy(() -> companyValidationService.validateCompanyName(longName))
                .isInstanceOf(CompanyException.class)
                .hasMessage("Company name is too long.");
    }

    @Test
    @DisplayName("회사명이 100자일 때 예외가 발생하지 않는다")
    void shouldNotThrowExceptionWhenCompanyNameIs100Characters() {
        // given
        String name = "a".repeat(100);

        // when & then
        assertThatCode(() -> companyValidationService.validateCompanyName(name))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("유효한 이메일일 때 예외가 발생하지 않는다")
    void shouldNotThrowExceptionWhenEmailIsValid() {
        // given
        String validEmail = "test@company.com";

        // when & then
        assertThatCode(() -> companyValidationService.validateCompanyEmail(validEmail))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("이메일이 null이면 예외가 발생한다")
    void shouldThrowExceptionWhenEmailIsNull() {
        // given
        String email = null;

        // when & then
        assertThatThrownBy(() -> companyValidationService.validateCompanyEmail(email))
                .isInstanceOf(CompanyException.class)
                .hasMessage("Company email is invalid.");
    }

    @Test
    @DisplayName("이메일이 공백이면 예외가 발생한다")
    void shouldThrowExceptionWhenEmailIsEmpty() {
        // given
        String email = "   ";

        // when & then
        assertThatThrownBy(() -> companyValidationService.validateCompanyEmail(email))
                .isInstanceOf(CompanyException.class)
                .hasMessage("Company email is invalid.");
    }

    @Test
    @DisplayName("잘못된 이메일 형식이면 예외가 발생한다")
    void shouldThrowExceptionWhenEmailFormatIsInvalid() {
        // given
        String invalidEmail = "invalid-email";

        // when & then
        assertThatThrownBy(() -> companyValidationService.validateCompanyEmail(invalidEmail))
                .isInstanceOf(CompanyException.class)
                .hasMessage("Company email format is invalid.");
    }

    @Test
    @DisplayName("도메인이 없는 이메일 형식이면 예외가 발생한다")
    void shouldThrowExceptionWhenEmailHasNoDomain() {
        // given
        String invalidEmail = "test@";

        // when & then
        assertThatThrownBy(() -> companyValidationService.validateCompanyEmail(invalidEmail))
                .isInstanceOf(CompanyException.class)
                .hasMessage("Company email format is invalid.");
    }

    @Test
    @DisplayName("유효한 전화번호일 때 예외가 발생하지 않는다")
    void shouldNotThrowExceptionWhenPhoneIsValid() {
        // given
        String validPhone = "010-1234-5678";

        // when & then
        assertThatCode(() -> companyValidationService.validateCompanyPhone(validPhone))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("국제 전화번호 형식일 때 예외가 발생하지 않는다")
    void shouldNotThrowExceptionWhenPhoneIsInternationalFormat() {
        // given
        String validPhone = "+82-10-1234-5678";

        // when & then
        assertThatCode(() -> companyValidationService.validateCompanyPhone(validPhone))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("전화번호가 null이면 예외가 발생한다")
    void shouldThrowExceptionWhenPhoneIsNull() {
        // given
        String phone = null;

        // when & then
        assertThatThrownBy(() -> companyValidationService.validateCompanyPhone(phone))
                .isInstanceOf(CompanyException.class)
                .hasMessage("Company phone is invalid.");
    }

    @Test
    @DisplayName("전화번호가 공백이면 예외가 발생한다")
    void shouldThrowExceptionWhenPhoneIsEmpty() {
        // given
        String phone = "   ";

        // when & then
        assertThatThrownBy(() -> companyValidationService.validateCompanyPhone(phone))
                .isInstanceOf(CompanyException.class)
                .hasMessage("Company phone is invalid.");
    }

    @Test
    @DisplayName("잘못된 전화번호 형식이면 예외가 발생한다")
    void shouldThrowExceptionWhenPhoneFormatIsInvalid() {
        // given
        String invalidPhone = "abc-def-ghij";

        // when & then
        assertThatThrownBy(() -> companyValidationService.validateCompanyPhone(invalidPhone))
                .isInstanceOf(CompanyException.class)
                .hasMessage("Company phone format is invalid.");
    }

    @Test
    @DisplayName("유효한 주소일 때 예외가 발생하지 않는다")
    void shouldNotThrowExceptionWhenAddressIsValid() {
        // given
        String validAddress = "서울시 강남구 테헤란로 123";

        // when & then
        assertThatCode(() -> companyValidationService.validateCompanyAddress(validAddress))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("주소가 null이면 예외가 발생한다")
    void shouldThrowExceptionWhenAddressIsNull() {
        // given
        String address = null;

        // when & then
        assertThatThrownBy(() -> companyValidationService.validateCompanyAddress(address))
                .isInstanceOf(CompanyException.class)
                .hasMessage("Company address is invalid.");
    }

    @Test
    @DisplayName("주소가 공백이면 예외가 발생한다")
    void shouldThrowExceptionWhenAddressIsEmpty() {
        // given
        String address = "   ";

        // when & then
        assertThatThrownBy(() -> companyValidationService.validateCompanyAddress(address))
                .isInstanceOf(CompanyException.class)
                .hasMessage("Company address is invalid.");
    }

    @Test
    @DisplayName("주소가 500자를 초과하면 예외가 발생한다")
    void shouldThrowExceptionWhenAddressIsTooString() {
        // given
        String longAddress = "a".repeat(501);

        // when & then
        assertThatThrownBy(() -> companyValidationService.validateCompanyAddress(longAddress))
                .isInstanceOf(CompanyException.class)
                .hasMessage("Company address is too long.");
    }

    @Test
    @DisplayName("주소가 500자일 때 예외가 발생하지 않는다")
    void shouldNotThrowExceptionWhenAddressIs500Characters() {
        // given
        String address = "a".repeat(500);

        // when & then
        assertThatCode(() -> companyValidationService.validateCompanyAddress(address))
                .doesNotThrowAnyException();
    }
}