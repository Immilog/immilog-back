package com.backend.immilog.post.domain.vo;

import com.backend.immilog.post.domain.model.enums.Experience;
import com.backend.immilog.user.domain.model.enums.Industry;
import lombok.*;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class CompanyMetaData {
    @Enumerated(EnumType.STRING)
    private Industry industry;

    @Enumerated(EnumType.STRING)
    private Experience experience;

    private LocalDateTime deadline;
    private String salary;
    private String company;
    private String companyEmail;
    private String companyPhone;
    private String companyAddress;
    private String companyHomepage;
    private String companyLogo;

    public static CompanyMetaData of(
            Industry industry,
            Experience experience,
            LocalDateTime deadline,
            String salary,
            String company,
            String companyEmail,
            String companyPhone,
            String companyAddress,
            String companyHomepage,
            String companyLogo
    ) {
        return CompanyMetaData.builder()
                .industry(industry)
                .experience(experience)
                .deadline(deadline)
                .salary(salary)
                .company(company)
                .companyEmail(companyEmail)
                .companyPhone(companyPhone)
                .companyAddress(companyAddress)
                .companyHomepage(companyHomepage)
                .companyLogo(companyLogo)
                .build();
    }
}
