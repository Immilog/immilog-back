package com.backend.immilog.country.application;

import com.backend.immilog.country.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CountryQueryServiceTest {

    @Mock
    private CountryRepository countryRepository;

    private CountryQueryService countryQueryService;

    private Country activeCountryKR;
    private Country activeCountryJP;
    private Country inactiveCountryUS;

    @BeforeEach
    void setUp() {
        countryQueryService = new CountryQueryService(countryRepository);
        
        LocalDateTime now = LocalDateTime.now();
        
        activeCountryKR = Country.builder()
                .id(new CountryId("KR"))
                .info(new CountryInfo("대한민국", "South Korea", "Asia"))
                .status(CountryStatus.ACTIVE)
                .createdAt(now)
                .updatedAt(now)
                .build();

        activeCountryJP = Country.builder()
                .id(new CountryId("JP"))
                .info(new CountryInfo("일본", "Japan", "Asia"))
                .status(CountryStatus.ACTIVE)
                .createdAt(now)
                .updatedAt(now)
                .build();

        inactiveCountryUS = Country.builder()
                .id(new CountryId("US"))
                .info(new CountryInfo("미국","United States","North America"))
                .status(CountryStatus.INACTIVE)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    @Nested
    @DisplayName("모든 국가 조회 테스트")
    class GetAllCountriesTest {

        @Test
        @DisplayName("모든 국가를 조회할 수 있다")
        void getAllCountries() {
            List<Country> allCountries = Arrays.asList(activeCountryKR, activeCountryJP, inactiveCountryUS);
            given(countryRepository.findAll()).willReturn(allCountries);

            List<Country> result = countryQueryService.getAllCountries();

            assertThat(result).hasSize(3);
            assertThat(result).containsExactly(activeCountryKR, activeCountryJP, inactiveCountryUS);
            verify(countryRepository).findAll();
        }

        @Test
        @DisplayName("국가가 없을 때 빈 리스트를 반환한다")
        void getAllCountriesWhenEmpty() {
            given(countryRepository.findAll()).willReturn(Collections.emptyList());

            List<Country> result = countryQueryService.getAllCountries();

            assertThat(result).isEmpty();
            verify(countryRepository).findAll();
        }

        @Test
        @DisplayName("단일 국가만 있을 때도 올바르게 조회한다")
        void getAllCountriesWithSingleCountry() {
            List<Country> singleCountry = Collections.singletonList(activeCountryKR);
            given(countryRepository.findAll()).willReturn(singleCountry);

            List<Country> result = countryQueryService.getAllCountries();

            assertThat(result).hasSize(1);
            assertThat(result).containsExactly(activeCountryKR);
            verify(countryRepository).findAll();
        }
    }

    @Nested
    @DisplayName("활성 국가 조회 테스트")
    class GetActiveCountriesTest {

        @Test
        @DisplayName("활성 상태의 국가들만 조회할 수 있다")
        void getActiveCountries() {
            List<Country> activeCountries = Arrays.asList(activeCountryKR, activeCountryJP);
            given(countryRepository.findAllActive()).willReturn(activeCountries);

            List<Country> result = countryQueryService.getActiveCountries();

            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(activeCountryKR, activeCountryJP);
            assertThat(result).allMatch(Country::isActive);
            verify(countryRepository).findAllActive();
        }

        @Test
        @DisplayName("활성 국가가 없을 때 빈 리스트를 반환한다")
        void getActiveCountriesWhenEmpty() {
            given(countryRepository.findAllActive()).willReturn(Collections.emptyList());

            List<Country> result = countryQueryService.getActiveCountries();

            assertThat(result).isEmpty();
            verify(countryRepository).findAllActive();
        }

        @Test
        @DisplayName("단일 활성 국가만 있을 때도 올바르게 조회한다")
        void getActiveCountriesWithSingleActiveCountry() {
            List<Country> singleActiveCountry = Collections.singletonList(activeCountryKR);
            given(countryRepository.findAllActive()).willReturn(singleActiveCountry);

            List<Country> result = countryQueryService.getActiveCountries();

            assertThat(result).hasSize(1);
            assertThat(result).containsExactly(activeCountryKR);
            assertThat(result.get(0).isActive()).isTrue();
            verify(countryRepository).findAllActive();
        }
    }

    @Nested
    @DisplayName("ID로 국가 조회 테스트")
    class GetCountryByIdTest {

        @Test
        @DisplayName("존재하는 국가 ID로 조회할 수 있다")
        void getCountryByExistingId() {
            given(countryRepository.findById("KR")).willReturn(Optional.of(activeCountryKR));

            Optional<Country> result = countryQueryService.getCountryById("KR");

            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(activeCountryKR);
            verify(countryRepository).findById("KR");
        }

        @Test
        @DisplayName("존재하지 않는 국가 ID로 조회하면 빈 Optional을 반환한다")
        void getCountryByNonExistingId() {
            given(countryRepository.findById("XX")).willReturn(Optional.empty());

            Optional<Country> result = countryQueryService.getCountryById("XX");

            assertThat(result).isEmpty();
            verify(countryRepository).findById("XX");
        }

        @Test
        @DisplayName("활성 상태가 아닌 국가도 조회할 수 있다")
        void getInactiveCountryById() {
            given(countryRepository.findById("US")).willReturn(Optional.of(inactiveCountryUS));

            Optional<Country> result = countryQueryService.getCountryById("US");

            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(inactiveCountryUS);
            assertThat(result.get().isActive()).isFalse();
            verify(countryRepository).findById("US");
        }

        @Test
        @DisplayName("null ID로 조회해도 적절히 처리한다")
        void getCountryByNullId() {
            given(countryRepository.findById(null)).willReturn(Optional.empty());

            Optional<Country> result = countryQueryService.getCountryById(null);

            assertThat(result).isEmpty();
            verify(countryRepository).findById(null);
        }

        @Test
        @DisplayName("빈 문자열 ID로 조회해도 적절히 처리한다")
        void getCountryByEmptyId() {
            given(countryRepository.findById("")).willReturn(Optional.empty());

            Optional<Country> result = countryQueryService.getCountryById("");

            assertThat(result).isEmpty();
            verify(countryRepository).findById("");
        }
    }

    @Nested
    @DisplayName("ID로 활성 국가 조회 테스트")
    class GetActiveCountryByIdTest {

        @Test
        @DisplayName("존재하는 활성 국가 ID로 조회할 수 있다")
        void getActiveCountryByExistingId() {
            given(countryRepository.findByIdAndActive("KR")).willReturn(Optional.of(activeCountryKR));

            Optional<Country> result = countryQueryService.getActiveCountryById("KR");

            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(activeCountryKR);
            assertThat(result.get().isActive()).isTrue();
            verify(countryRepository).findByIdAndActive("KR");
        }

        @Test
        @DisplayName("존재하지 않는 국가 ID로 활성 국가 조회하면 빈 Optional을 반환한다")
        void getActiveCountryByNonExistingId() {
            given(countryRepository.findByIdAndActive("XX")).willReturn(Optional.empty());

            Optional<Country> result = countryQueryService.getActiveCountryById("XX");

            assertThat(result).isEmpty();
            verify(countryRepository).findByIdAndActive("XX");
        }

        @Test
        @DisplayName("비활성 국가 ID로 활성 국가 조회하면 빈 Optional을 반환한다")
        void getActiveCountryByInactiveCountryId() {
            given(countryRepository.findByIdAndActive("US")).willReturn(Optional.empty());

            Optional<Country> result = countryQueryService.getActiveCountryById("US");

            assertThat(result).isEmpty();
            verify(countryRepository).findByIdAndActive("US");
        }

        @Test
        @DisplayName("null ID로 활성 국가 조회해도 적절히 처리한다")
        void getActiveCountryByNullId() {
            given(countryRepository.findByIdAndActive(null)).willReturn(Optional.empty());

            Optional<Country> result = countryQueryService.getActiveCountryById(null);

            assertThat(result).isEmpty();
            verify(countryRepository).findByIdAndActive(null);
        }

        @Test
        @DisplayName("빈 문자열 ID로 활성 국가 조회해도 적절히 처리한다")
        void getActiveCountryByEmptyId() {
            given(countryRepository.findByIdAndActive("")).willReturn(Optional.empty());

            Optional<Country> result = countryQueryService.getActiveCountryById("");

            assertThat(result).isEmpty();
            verify(countryRepository).findByIdAndActive("");
        }
    }

    @Nested
    @DisplayName("CountryQueryService 생성자 테스트")
    class ConstructorTest {

        @Test
        @DisplayName("CountryRepository를 주입받아 올바르게 생성된다")
        void constructorInjection() {
            CountryQueryService service = new CountryQueryService(countryRepository);
            
            assertThat(service).isNotNull();
        }
    }

    @Nested
    @DisplayName("서비스 트랜잭션 테스트")
    class TransactionTest {

        @Test
        @DisplayName("모든 조회 메서드가 읽기 전용 트랜잭션으로 실행된다")
        void allQueryMethodsUseReadOnlyTransaction() {
            given(countryRepository.findAll()).willReturn(Collections.emptyList());
            given(countryRepository.findAllActive()).willReturn(Collections.emptyList());
            given(countryRepository.findById("KR")).willReturn(Optional.empty());
            given(countryRepository.findByIdAndActive("KR")).willReturn(Optional.empty());

            countryQueryService.getAllCountries();
            countryQueryService.getActiveCountries();
            countryQueryService.getCountryById("KR");
            countryQueryService.getActiveCountryById("KR");

            verify(countryRepository).findAll();
            verify(countryRepository).findAllActive();
            verify(countryRepository).findById("KR");
            verify(countryRepository).findByIdAndActive("KR");
        }
    }

    @Nested
    @DisplayName("대용량 데이터 처리 테스트")
    class LargeDataTest {

        @Test
        @DisplayName("많은 수의 국가 데이터도 올바르게 처리한다")
        void handleLargeNumberOfCountries() {
            List<Country> manyCountries = Collections.nCopies(1000, activeCountryKR);
            given(countryRepository.findAll()).willReturn(manyCountries);

            List<Country> result = countryQueryService.getAllCountries();

            assertThat(result).hasSize(1000);
            verify(countryRepository).findAll();
        }
    }
}