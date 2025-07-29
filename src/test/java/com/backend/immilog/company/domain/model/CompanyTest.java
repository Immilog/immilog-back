package com.backend.immilog.company.domain.model;

import com.backend.immilog.company.exception.CompanyErrorCode;
import com.backend.immilog.company.exception.CompanyException;
import com.backend.immilog.shared.enums.Country;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Company 도메인 모델 테스트")
class CompanyTest {

    @Test
    @DisplayName("유효한 파라미터로 Company를 생성할 수 있다")
    void shouldCreateCompanyWithValidParameters() {
        // given
        Long seq = 1L;
        CompanyManager manager = CompanyManager.of(Country.SOUTH_KOREA, "서울", 1L);
        CompanyMetaData metaData = CompanyMetaData.of(Industry.IT, "테스트 회사", "test@company.com", "010-1234-5678", "서울시 강남구", "https://company.com", "logo.png");

        // when
        Company company = new Company(seq, manager, metaData);

        // then
        assertThat(company.seq()).isEqualTo(seq);
        assertThat(company.name()).isEqualTo("테스트 회사");
        assertThat(company.email()).isEqualTo("test@company.com");
        assertThat(company.phone()).isEqualTo("010-1234-5678");
        assertThat(company.address()).isEqualTo("서울시 강남구");
        assertThat(company.homepage()).isEqualTo("https://company.com");
        assertThat(company.logo()).isEqualTo("logo.png");
        assertThat(company.industry()).isEqualTo(Industry.IT);
        assertThat(company.country()).isEqualTo(Country.SOUTH_KOREA);
        assertThat(company.region()).isEqualTo("서울");
        assertThat(company.managerUserSeq()).isEqualTo(1L);
    }

    @Test
    @DisplayName("manager가 null이면 예외가 발생한다")
    void shouldThrowExceptionWhenManagerIsNull() {
        // given
        Long seq = 1L;
        CompanyManager manager = null;
        CompanyMetaData metaData = CompanyMetaData.of(Industry.IT, "테스트 회사", "test@company.com", "010-1234-5678", null, null, null);

        // when & then
        assertThatThrownBy(() -> new Company(seq, manager, metaData))
                .isInstanceOf(CompanyException.class)
                .hasMessage("Manager information is required.");
    }

    @Test
    @DisplayName("companyMetaData가 null이면 예외가 발생한다")
    void shouldThrowExceptionWhenCompanyMetaDataIsNull() {
        // given
        Long seq = 1L;
        CompanyManager manager = CompanyManager.of(Country.SOUTH_KOREA, "서울", 1L);
        CompanyMetaData metaData = null;

        // when & then
        assertThatThrownBy(() -> new Company(seq, manager, metaData))
                .isInstanceOf(CompanyException.class)
                .hasMessage("Company metadata is required.");
    }

    @Test
    @DisplayName("createEmpty로 빈 Company를 생성할 수 있다")
    void shouldCreateEmptyCompany() {
        // when
        Company company = Company.createEmpty();

        // then
        assertThat(company.seq()).isNull();
        assertThat(company.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("isEmpty는 seq가 null일 때 true를 반환한다")
    void shouldReturnTrueWhenSeqIsNull() {
        // given
        Company company = Company.createEmpty();

        // when
        boolean isEmpty = company.isEmpty();

        // then
        assertThat(isEmpty).isTrue();
    }

    @Test
    @DisplayName("isEmpty는 seq가 있을 때 false를 반환한다")
    void shouldReturnFalseWhenSeqIsNotNull() {
        // given
        CompanyManager manager = CompanyManager.of(Country.SOUTH_KOREA, "서울", 1L);
        CompanyMetaData metaData = CompanyMetaData.of(Industry.IT, "테스트 회사", "test@company.com", "010-1234-5678", null, null, null);
        Company company = new Company(1L, manager, metaData);

        // when
        boolean isEmpty = company.isEmpty();

        // then
        assertThat(isEmpty).isFalse();
    }

    @Test
    @DisplayName("seq 메서드로 새로운 seq를 가진 Company를 생성할 수 있다")
    void shouldCreateCompanyWithNewSeq() {
        // given
        CompanyManager manager = CompanyManager.of(Country.SOUTH_KOREA, "서울", 1L);
        CompanyMetaData metaData = CompanyMetaData.of(Industry.IT, "테스트 회사", "test@company.com", "010-1234-5678", null, null, null);
        Company original = new Company(1L, manager, metaData);
        Long newSeq = 2L;

        // when
        Company updated = original.seq(newSeq);

        // then
        assertThat(updated.seq()).isEqualTo(newSeq);
        assertThat(updated.name()).isEqualTo(original.name());
        assertThat(updated).isNotSameAs(original);
    }

    @Test
    @DisplayName("manager 메서드로 새로운 manager 정보를 가진 Company를 생성할 수 있다")
    void shouldCreateCompanyWithNewManager() {
        // given
        CompanyManager manager = CompanyManager.of(Country.SOUTH_KOREA, "서울", 1L);
        CompanyMetaData metaData = CompanyMetaData.of(Industry.IT, "테스트 회사", "test@company.com", "010-1234-5678", null, null, null);
        Company original = new Company(1L, manager, metaData);

        // when
        Company updated = original.manager(Country.JAPAN, "도쿄", 2L);

        // then
        assertThat(updated.country()).isEqualTo(Country.JAPAN);
        assertThat(updated.region()).isEqualTo("도쿄");
        assertThat(updated.managerUserSeq()).isEqualTo(2L);
        assertThat(updated.seq()).isEqualTo(original.seq());
        assertThat(updated).isNotSameAs(original);
    }

    @Test
    @DisplayName("companyData 메서드로 새로운 회사 정보를 가진 Company를 생성할 수 있다")
    void shouldCreateCompanyWithNewCompanyData() {
        // given
        CompanyManager manager = CompanyManager.of(Country.SOUTH_KOREA, "서울", 1L);
        CompanyMetaData metaData = CompanyMetaData.of(Industry.IT, "테스트 회사", "test@company.com", "010-1234-5678", null, null, null);
        Company original = new Company(1L, manager, metaData);

        // when
        Company updated = original.companyData(Industry.ETC, "새로운 회사", "new@company.com", "010-9876-5432", "부산시", "https://new.com", "new-logo.png");

        // then
        assertThat(updated.industry()).isEqualTo(Industry.ETC);
        assertThat(updated.name()).isEqualTo("새로운 회사");
        assertThat(updated.email()).isEqualTo("new@company.com");
        assertThat(updated.phone()).isEqualTo("010-9876-5432");
        assertThat(updated.address()).isEqualTo("부산시");
        assertThat(updated.homepage()).isEqualTo("https://new.com");
        assertThat(updated.logo()).isEqualTo("new-logo.png");
        assertThat(updated.seq()).isEqualTo(original.seq());
        assertThat(updated).isNotSameAs(original);
    }

    @Test
    @DisplayName("updateName으로 회사명을 변경할 수 있다")
    void shouldUpdateCompanyName() {
        // given
        CompanyManager manager = CompanyManager.of(Country.SOUTH_KOREA, "서울", 1L);
        CompanyMetaData metaData = CompanyMetaData.of(Industry.IT, "테스트 회사", "test@company.com", "010-1234-5678", null, null, null);
        Company original = new Company(1L, manager, metaData);
        String newName = "새로운 회사명";

        // when
        Company updated = original.updateName(newName);

        // then
        assertThat(updated.name()).isEqualTo(newName);
        assertThat(updated.seq()).isEqualTo(original.seq());
        assertThat(updated).isNotSameAs(original);
    }

    @Test
    @DisplayName("updateName에서 빈 문자열로 변경하면 예외가 발생한다")
    void shouldThrowExceptionWhenUpdateNameWithEmptyString() {
        // given
        CompanyManager manager = CompanyManager.of(Country.SOUTH_KOREA, "서울", 1L);
        CompanyMetaData metaData = CompanyMetaData.of(Industry.IT, "테스트 회사", "test@company.com", "010-1234-5678", null, null, null);
        Company company = new Company(1L, manager, metaData);
        String emptyName = "   ";

        // when & then
        assertThatThrownBy(() -> company.updateName(emptyName))
                .isInstanceOf(CompanyException.class)
                .hasMessage("Company name cannot be empty.");
    }

    @Test
    @DisplayName("updateName에서 같은 이름으로 변경하면 원본 객체를 반환한다")
    void shouldReturnOriginalWhenUpdateNameWithSameName() {
        // given
        CompanyManager manager = CompanyManager.of(Country.SOUTH_KOREA, "서울", 1L);
        CompanyMetaData metaData = CompanyMetaData.of(Industry.IT, "테스트 회사", "test@company.com", "010-1234-5678", null, null, null);
        Company original = new Company(1L, manager, metaData);
        String sameName = "테스트 회사";

        // when
        Company updated = original.updateName(sameName);

        // then
        assertThat(updated).isSameAs(original);
    }

    @Test
    @DisplayName("updateName에서 null로 변경하면 원본 객체를 반환한다")
    void shouldReturnOriginalWhenUpdateNameWithNull() {
        // given
        CompanyManager manager = CompanyManager.of(Country.SOUTH_KOREA, "서울", 1L);
        CompanyMetaData metaData = CompanyMetaData.of(Industry.IT, "테스트 회사", "test@company.com", "010-1234-5678", null, null, null);
        Company original = new Company(1L, manager, metaData);

        // when
        Company updated = original.updateName(null);

        // then
        assertThat(updated).isSameAs(original);
    }

    @Test
    @DisplayName("updatePhone으로 전화번호를 변경할 수 있다")
    void shouldUpdateCompanyPhone() {
        // given
        CompanyManager manager = CompanyManager.of(Country.SOUTH_KOREA, "서울", 1L);
        CompanyMetaData metaData = CompanyMetaData.of(Industry.IT, "테스트 회사", "test@company.com", "010-1234-5678", null, null, null);
        Company original = new Company(1L, manager, metaData);
        String newPhone = "010-9876-5432";

        // when
        Company updated = original.updatePhone(newPhone);

        // then
        assertThat(updated.phone()).isEqualTo(newPhone);
        assertThat(updated.name()).isEqualTo(original.name());
        assertThat(updated).isNotSameAs(original);
    }

    @Test
    @DisplayName("updatePhone에서 같은 전화번호로 변경하면 원본 객체를 반환한다")
    void shouldReturnOriginalWhenUpdatePhoneWithSamePhone() {
        // given
        CompanyManager manager = CompanyManager.of(Country.SOUTH_KOREA, "서울", 1L);
        CompanyMetaData metaData = CompanyMetaData.of(Industry.IT, "테스트 회사", "test@company.com", "010-1234-5678", null, null, null);
        Company original = new Company(1L, manager, metaData);
        String samePhone = "010-1234-5678";

        // when
        Company updated = original.updatePhone(samePhone);

        // then
        assertThat(updated).isSameAs(original);
    }

    @Test
    @DisplayName("updateLogo로 로고를 변경할 수 있다")
    void shouldUpdateCompanyLogo() {
        // given
        CompanyManager manager = CompanyManager.of(Country.SOUTH_KOREA, "서울", 1L);
        CompanyMetaData metaData = CompanyMetaData.of(Industry.IT, "테스트 회사", "test@company.com", "010-1234-5678", null, null, "old-logo.png");
        Company original = new Company(1L, manager, metaData);
        String newLogo = "new-logo.png";

        // when
        Company updated = original.updateLogo(newLogo);

        // then
        assertThat(updated.logo()).isEqualTo(newLogo);
        assertThat(updated.name()).isEqualTo(original.name());
        assertThat(updated).isNotSameAs(original);
    }

    @Test
    @DisplayName("updateHomepage로 홈페이지를 변경할 수 있다")
    void shouldUpdateCompanyHomepage() {
        // given
        CompanyManager manager = CompanyManager.of(Country.SOUTH_KOREA, "서울", 1L);
        CompanyMetaData metaData = CompanyMetaData.of(Industry.IT, "테스트 회사", "test@company.com", "010-1234-5678", null, "https://old.com", null);
        Company original = new Company(1L, manager, metaData);
        String newHomepage = "https://new.com";

        // when
        Company updated = original.updateHomepage(newHomepage);

        // then
        assertThat(updated.homepage()).isEqualTo(newHomepage);
        assertThat(updated.name()).isEqualTo(original.name());
        assertThat(updated).isNotSameAs(original);
    }

    @Test
    @DisplayName("updateEmail로 이메일을 변경할 수 있다")
    void shouldUpdateCompanyEmail() {
        // given
        CompanyManager manager = CompanyManager.of(Country.SOUTH_KOREA, "서울", 1L);
        CompanyMetaData metaData = CompanyMetaData.of(Industry.IT, "테스트 회사", "test@company.com", "010-1234-5678", null, null, null);
        Company original = new Company(1L, manager, metaData);
        String newEmail = "new@company.com";

        // when
        Company updated = original.updateEmail(newEmail);

        // then
        assertThat(updated.email()).isEqualTo(newEmail);
        assertThat(updated.name()).isEqualTo(original.name());
        assertThat(updated).isNotSameAs(original);
    }

    @Test
    @DisplayName("updateCountry로 국가를 변경할 수 있다")
    void shouldUpdateCompanyCountry() {
        // given
        CompanyManager manager = CompanyManager.of(Country.SOUTH_KOREA, "서울", 1L);
        CompanyMetaData metaData = CompanyMetaData.of(Industry.IT, "테스트 회사", "test@company.com", "010-1234-5678", null, null, null);
        Company original = new Company(1L, manager, metaData);
        Country newCountry = Country.JAPAN;

        // when
        Company updated = original.updateCountry(newCountry);

        // then
        assertThat(updated.country()).isEqualTo(newCountry);
        assertThat(updated.region()).isEqualTo(original.region());
        assertThat(updated.name()).isEqualTo(original.name());
        assertThat(updated).isNotSameAs(original);
    }

    @Test
    @DisplayName("updateAddress로 주소를 변경할 수 있다")
    void shouldUpdateCompanyAddress() {
        // given
        CompanyManager manager = CompanyManager.of(Country.SOUTH_KOREA, "서울", 1L);
        CompanyMetaData metaData = CompanyMetaData.of(Industry.IT, "테스트 회사", "test@company.com", "010-1234-5678", "기존 주소", null, null);
        Company original = new Company(1L, manager, metaData);
        String newAddress = "새로운 주소";

        // when
        Company updated = original.updateAddress(newAddress);

        // then
        assertThat(updated.address()).isEqualTo(newAddress);
        assertThat(updated.name()).isEqualTo(original.name());
        assertThat(updated).isNotSameAs(original);
    }

    @Test
    @DisplayName("updateRegion으로 지역을 변경할 수 있다")
    void shouldUpdateCompanyRegion() {
        // given
        CompanyManager manager = CompanyManager.of(Country.SOUTH_KOREA, "서울", 1L);
        CompanyMetaData metaData = CompanyMetaData.of(Industry.IT, "테스트 회사", "test@company.com", "010-1234-5678", null, null, null);
        Company original = new Company(1L, manager, metaData);
        String newRegion = "부산";

        // when
        Company updated = original.updateRegion(newRegion);

        // then
        assertThat(updated.region()).isEqualTo(newRegion);
        assertThat(updated.country()).isEqualTo(original.country());
        assertThat(updated.name()).isEqualTo(original.name());
        assertThat(updated).isNotSameAs(original);
    }

    @Test
    @DisplayName("updateIndustry로 업종을 변경할 수 있다")
    void shouldUpdateCompanyIndustry() {
        // given
        CompanyManager manager = CompanyManager.of(Country.SOUTH_KOREA, "서울", 1L);
        CompanyMetaData metaData = CompanyMetaData.of(Industry.IT, "테스트 회사", "test@company.com", "010-1234-5678", null, null, null);
        Company original = new Company(1L, manager, metaData);
        Industry newIndustry = Industry.ETC;

        // when
        Company updated = original.updateIndustry(newIndustry);

        // then
        assertThat(updated.industry()).isEqualTo(newIndustry);
        assertThat(updated.name()).isEqualTo(original.name());
        assertThat(updated).isNotSameAs(original);
    }

    @Test
    @DisplayName("updateIndustry에서 같은 업종으로 변경하면 원본 객체를 반환한다")
    void shouldReturnOriginalWhenUpdateIndustryWithSameIndustry() {
        // given
        CompanyManager manager = CompanyManager.of(Country.SOUTH_KOREA, "서울", 1L);
        CompanyMetaData metaData = CompanyMetaData.of(Industry.IT, "테스트 회사", "test@company.com", "010-1234-5678", null, null, null);
        Company original = new Company(1L, manager, metaData);
        Industry sameIndustry = Industry.IT;

        // when
        Company updated = original.updateIndustry(sameIndustry);

        // then
        assertThat(updated).isSameAs(original);
    }

    @Test
    @DisplayName("모든 getter 메서드가 올바른 값을 반환한다")
    void shouldReturnCorrectValuesFromGetters() {
        // given
        Long seq = 1L;
        CompanyManager manager = CompanyManager.of(Country.SOUTH_KOREA, "서울", 1L);
        CompanyMetaData metaData = CompanyMetaData.of(Industry.IT, "테스트 회사", "test@company.com", "010-1234-5678", "서울시 강남구", "https://company.com", "logo.png");
        Company company = new Company(seq, manager, metaData);

        // when & then
        assertThat(company.seq()).isEqualTo(seq);
        assertThat(company.industry()).isEqualTo(Industry.IT);
        assertThat(company.name()).isEqualTo("테스트 회사");
        assertThat(company.email()).isEqualTo("test@company.com");
        assertThat(company.phone()).isEqualTo("010-1234-5678");
        assertThat(company.address()).isEqualTo("서울시 강남구");
        assertThat(company.homepage()).isEqualTo("https://company.com");
        assertThat(company.country()).isEqualTo(Country.SOUTH_KOREA);
        assertThat(company.region()).isEqualTo("서울");
        assertThat(company.logo()).isEqualTo("logo.png");
        assertThat(company.managerUserSeq()).isEqualTo(1L);
    }
}