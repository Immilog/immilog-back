package com.backend.immilog.notice.domain.model;

import com.backend.immilog.notice.domain.enums.NoticeStatus;
import com.backend.immilog.notice.domain.enums.NoticeType;
import com.backend.immilog.notice.exception.NoticeErrorCode;
import com.backend.immilog.notice.exception.NoticeException;

import java.time.LocalDateTime;
import java.util.List;

public class Notice {
    private final NoticeId id;
    private final NoticeAuthor author;
    private NoticeTitle title;
    private NoticeContent content;
    private final NoticeTargeting targeting;
    private NoticeReadStatus readStatus;
    private NoticeMetadata metadata;

    private Notice(
            NoticeId id,
            NoticeAuthor author,
            NoticeTitle title,
            NoticeContent content,
            NoticeTargeting targeting,
            NoticeReadStatus readStatus,
            NoticeMetadata metadata
    ) {
        this.id = id;
        this.author = author;
        this.title = title;
        this.content = content;
        this.targeting = targeting;
        this.readStatus = readStatus;
        this.metadata = metadata;
    }

    public static Notice create(
            NoticeAuthor author,
            NoticeTitle title,
            NoticeContent content,
            NoticeType type,
            NoticeTargeting targeting
    ) {
        validateCreationInputs(author, title, content, type, targeting);

        return new Notice(
                null,
                author,
                title,
                content,
                targeting,
                NoticeReadStatus.empty(),
                NoticeMetadata.of(type, NoticeStatus.NORMAL)
        );
    }

    public static Notice restore(
            NoticeId id,
            NoticeAuthor author,
            NoticeTitle title,
            NoticeContent content,
            NoticeType type,
            NoticeStatus status,
            NoticeTargeting targeting,
            NoticeReadStatus readStatus,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return new Notice(
                id,
                author,
                title,
                content,
                targeting,
                readStatus,
                NoticeMetadata.restore(type, status, createdAt, updatedAt)
        );
    }

    public Notice updateTitle(NoticeTitle newTitle) {
        validateNotDeleted();
        if (newTitle == null || newTitle.equals(this.title)) {
            return this;
        }
        this.title = newTitle;
        this.metadata = metadata.touch();
        return this;
    }

    public Notice updateContent(NoticeContent newContent) {
        validateNotDeleted();
        if (newContent == null || newContent.equals(this.content)) {
            return this;
        }
        this.content = newContent;
        this.metadata = metadata.touch();
        return this;
    }

    public Notice updateType(NoticeType newType) {
        validateNotDeleted();
        this.metadata = metadata.updateType(newType);
        return this;
    }

    public Notice markAsRead(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new NoticeException(NoticeErrorCode.INVALID_USER_SEQ);
        }
        this.readStatus = readStatus.markAsRead(userId);
        return this;
    }

    public Notice delete() {
        if (isDeleted()) {
            throw new NoticeException(NoticeErrorCode.NOTICE_ALREADY_DELETED);
        }
        this.metadata = metadata.updateStatus(NoticeStatus.DELETED);
        return this;
    }

    public Notice activate() {
        if (isActive()) {
            return this;
        }
        this.metadata = metadata.updateStatus(NoticeStatus.NORMAL);
        return this;
    }

    public boolean isTargetedTo(String countryId) {
        return targeting.isTargetedTo(countryId);
    }

    public boolean isReadBy(String userId) {
        return readStatus.isReadBy(userId);
    }

    public boolean isActive() {
        return metadata.isActive();
    }

    public boolean isDeleted() {
        return metadata.isDeleted();
    }

    public boolean isAuthor(String userId) {
        return author.userId().equals(userId);
    }

    private static void validateCreationInputs(
            NoticeAuthor author,
            NoticeTitle title,
            NoticeContent content,
            NoticeType type,
            NoticeTargeting targeting
    ) {
        if (author == null) {
            throw new NoticeException(NoticeErrorCode.INVALID_NOTICE_AUTHOR);
        }
        if (title == null) {
            throw new NoticeException(NoticeErrorCode.INVALID_NOTICE_TITLE);
        }
        if (content == null) {
            throw new NoticeException(NoticeErrorCode.INVALID_NOTICE_CONTENT);
        }
        if (type == null) {
            throw new NoticeException(NoticeErrorCode.INVALID_NOTICE_TYPE);
        }
        if (targeting == null) {
            throw new NoticeException(NoticeErrorCode.INVALID_NOTICE_TARGET_COUNTRIES);
        }
    }

    private void validateNotDeleted() {
        if (isDeleted()) {
            throw new NoticeException(NoticeErrorCode.NOTICE_ALREADY_DELETED);
        }
    }

    public NoticeId getId() {return id;}

    public NoticeAuthor getAuthor() {return author;}

    public NoticeTitle getTitle() {return title;}

    public NoticeContent getContent() {return content;}

    public NoticeTargeting getTargeting() {return targeting;}

    public NoticeReadStatus getReadStatus() {return readStatus;}

    public NoticeMetadata getMetadata() {return metadata;}

    public String getIdValue() {return id != null ? id.value() : null;}

    public String getAuthorUserId() {return author.userId();}

    public String getTitleValue() {return title.value();}

    public String getContentValue() {return content.value();}

    public NoticeType getType() {return metadata.type();}

    public NoticeStatus getStatus() {return metadata.status();}

    public List<String> getTargetCountries() {return targeting.targetCountryIds();}

    public List<String> getReadUsers() {return readStatus.getReadUsersList();}

    public LocalDateTime getCreatedAt() {return metadata.createdAt();}

    public LocalDateTime getUpdatedAt() {return metadata.updatedAt();}

    public int getReadCount() {return readStatus.getReadCount();}
}