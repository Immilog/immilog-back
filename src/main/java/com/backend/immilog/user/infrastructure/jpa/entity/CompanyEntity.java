package com.backend.immilog.user.infrastructure.jpa.entity;

import com.backend.immilog.user.domain.model.Company;
import com.backend.immilog.user.domain.enums.Industry;
import com.backend.immilog.user.domain.enums.UserCountry;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@Builder
@Entity
public class CompanyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @Setter
    @Enumerated(EnumType.STRING)
    private Industry industry;

    @Setter
    private String companyName;

    @Setter
    private String companyEmail;

    @Setter
    private String companyPhone;

    @Setter
    private String companyAddress;

    @Setter
    private String companyHomepage;

    @Setter
    private UserCountry companyCountry;

    @Setter
    private String companyRegion;

    @Setter
    private String companyLogo;

    @Setter
    private Long companyManagerUserSeq;

    public static CompanyEntity from(
            Company company
    ) {
        return CompanyEntity.builder()
                .industry(company.industry())
                .companyName(company.companyName())
                .companyEmail(company.companyEmail())
                .companyPhone(company.companyPhone())
                .companyAddress(company.companyAddress())
                .companyHomepage(company.companyHomepage())
                .companyCountry(company.companyCountry())
                .companyRegion(company.companyRegion())
                .companyLogo(company.companyLogo())
                .companyManagerUserSeq(company.companyManagerUserSeq())
                .build();
    }

    public Company toDomain() {
        return Company.builder()
                .seq(seq)
                .industry(industry)
                .companyName(companyName)
                .companyEmail(companyEmail)
                .companyPhone(companyPhone)
                .companyAddress(companyAddress)
                .companyHomepage(companyHomepage)
                .companyCountry(companyCountry)
                .companyRegion(companyRegion)
                .companyLogo(companyLogo)
                .companyManagerUserSeq(companyManagerUserSeq)
                .build();
    }
}
