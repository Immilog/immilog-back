package com.backend.immilog.notice.infrastructure.jpa;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.backend.immilog.notice.domain.enums.NoticeStatus;
import com.backend.immilog.notice.domain.enums.NoticeType;
import com.backend.immilog.notice.domain.model.*;
import com.backend.immilog.shared.enums.Country;
import jakarta.persistence.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.List;

@DynamicUpdate
@Entity
@Table(name = "notice")
public class NoticeJpaEntity {
    @Id
    @Column(name = "notice_id")
    private String id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private NoticeType type;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private NoticeStatus status;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "notice_target_country", joinColumns = @JoinColumn(name = "notice_id"))
    @Column(name = "country")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<Country> targetCountry;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "notice_read_user", joinColumns = @JoinColumn(name = "notice_id"))
    @Column(name = "user_id")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<String> readUsers;

    @CreatedDate
    @Column(name = "created_at")
    private final LocalDateTime createdAt = LocalDateTime.now();

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = NanoIdUtils.randomNanoId();
        }
    }

    protected NoticeJpaEntity() {}

    protected NoticeJpaEntity(
            String id,
            String userId,
            String title,
            String content,
            NoticeType type,
            NoticeStatus status,
            List<Country> targetCountry,
            List<String> readUsers
    ) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.type = type;
        this.status = status;
        this.targetCountry = targetCountry;
        this.readUsers = readUsers;
        this.updatedAt = id == null ? null : LocalDateTime.now();
    }

    public static NoticeJpaEntity from(Notice notice) {
        return new NoticeJpaEntity(
                notice.getIdValue(),
                notice.getAuthorUserId(),
                notice.getTitleValue(),
                notice.getContentValue(),
                notice.getType(),
                notice.getStatus(),
                notice.getTargetCountries(),
                notice.getReadUsers()
        );
    }

    public Notice toDomain() {
        return Notice.restore(
                this.id != null ? NoticeId.of(this.id) : null,
                NoticeAuthor.of(this.userId),
                NoticeTitle.of(this.title),
                NoticeContent.of(this.content),
                this.type,
                this.status,
                NoticeTargeting.of(this.targetCountry),
                NoticeReadStatus.of(this.readUsers),
                this.createdAt,
                this.updatedAt
        );
    }
}

