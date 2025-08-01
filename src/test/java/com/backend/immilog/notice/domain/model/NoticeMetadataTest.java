package com.backend.immilog.notice.domain.model;

import com.backend.immilog.notice.domain.enums.NoticeStatus;
import com.backend.immilog.notice.domain.enums.NoticeType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class NoticeMetadataTest {

    @Test
    @DisplayName("NoticeMetadata 생성 - 정상 케이스")
    void createNoticeMetadataSuccessfully() {
        //given
        NoticeType type = NoticeType.NOTICE;
        NoticeStatus status = NoticeStatus.NORMAL;

        //when
        NoticeMetadata metadata = NoticeMetadata.of(type, status);

        //then
        assertThat(metadata.type()).isEqualTo(type);
        assertThat(metadata.status()).isEqualTo(status);
        assertThat(metadata.createdAt()).isNotNull();
        assertThat(metadata.updatedAt()).isNotNull();
        assertThat(metadata.createdAt()).isEqualToIgnoringNanos(metadata.updatedAt());
    }

    @Test
    @DisplayName("NoticeMetadata 복원 - 정상 케이스")
    void restoreNoticeMetadataSuccessfully() {
        //given
        NoticeType type = NoticeType.EVENT;
        NoticeStatus status = NoticeStatus.DELETED;
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now().minusHours(1);

        //when
        NoticeMetadata metadata = NoticeMetadata.restore(type, status, createdAt, updatedAt);

        //then
        assertThat(metadata.type()).isEqualTo(type);
        assertThat(metadata.status()).isEqualTo(status);
        assertThat(metadata.createdAt()).isEqualTo(createdAt);
        assertThat(metadata.updatedAt()).isEqualTo(updatedAt);
    }

    @Test
    @DisplayName("타입 업데이트 - 정상 케이스")
    void updateTypeSuccessfully() {
        //given
        NoticeMetadata metadata = NoticeMetadata.of(NoticeType.NOTICE, NoticeStatus.NORMAL);
        NoticeType newType = NoticeType.NOTICE;

        //when
        NoticeMetadata updatedMetadata = metadata.updateType(newType);

        //then
        assertThat(updatedMetadata.type()).isEqualTo(newType);
        assertThat(updatedMetadata.status()).isEqualTo(metadata.status());
    }

    @Test
    @DisplayName("타입 업데이트 - 동일한 타입")
    void updateTypeWithSameType() {
        //given
        NoticeMetadata metadata = NoticeMetadata.of(NoticeType.NOTICE, NoticeStatus.NORMAL);
        NoticeType sameType = NoticeType.NOTICE;

        //when
        NoticeMetadata updatedMetadata = metadata.updateType(sameType);

        //then
        assertThat(updatedMetadata).isEqualTo(metadata);
        assertThat(updatedMetadata.updatedAt()).isEqualTo(metadata.updatedAt());
    }

    @Test
    @DisplayName("타입 업데이트 - null 타입")
    void updateTypeWithNullType() {
        //given
        NoticeMetadata metadata = NoticeMetadata.of(NoticeType.NOTICE, NoticeStatus.NORMAL);

        //when
        NoticeMetadata updatedMetadata = metadata.updateType(null);

        //then
        assertThat(updatedMetadata).isEqualTo(metadata);
        assertThat(updatedMetadata.updatedAt()).isEqualTo(metadata.updatedAt());
    }

    @Test
    @DisplayName("상태 업데이트 - 정상 케이스")
    void updateStatusSuccessfully() {
        //given
        NoticeMetadata metadata = NoticeMetadata.of(NoticeType.NOTICE, NoticeStatus.NORMAL);
        NoticeStatus newStatus = NoticeStatus.DELETED;

        //when
        NoticeMetadata updatedMetadata = metadata.updateStatus(newStatus);

        //then
        assertThat(updatedMetadata.status()).isEqualTo(newStatus);
        assertThat(updatedMetadata.type()).isEqualTo(metadata.type());
        assertThat(updatedMetadata.createdAt()).isEqualTo(metadata.createdAt());
        assertThat(updatedMetadata.updatedAt()).isAfter(metadata.updatedAt());
    }

    @Test
    @DisplayName("상태 업데이트 - 동일한 상태")
    void updateStatusWithSameStatus() {
        //given
        NoticeMetadata metadata = NoticeMetadata.of(NoticeType.NOTICE, NoticeStatus.NORMAL);
        NoticeStatus sameStatus = NoticeStatus.NORMAL;

        //when
        NoticeMetadata updatedMetadata = metadata.updateStatus(sameStatus);

        //then
        assertThat(updatedMetadata).isEqualTo(metadata);
        assertThat(updatedMetadata.updatedAt()).isEqualTo(metadata.updatedAt());
    }

    @Test
    @DisplayName("상태 업데이트 - null 상태")
    void updateStatusWithNullStatus() {
        //given
        NoticeMetadata metadata = NoticeMetadata.of(NoticeType.NOTICE, NoticeStatus.NORMAL);

        //when
        NoticeMetadata updatedMetadata = metadata.updateStatus(null);

        //then
        assertThat(updatedMetadata).isEqualTo(metadata);
        assertThat(updatedMetadata.updatedAt()).isEqualTo(metadata.updatedAt());
    }

    @Test
    @DisplayName("터치 - 업데이트 시간 갱신")
    void touchUpdatesTimestamp() {
        //given
        NoticeMetadata metadata = NoticeMetadata.of(NoticeType.NOTICE, NoticeStatus.NORMAL);

        //when
        NoticeMetadata touchedMetadata = metadata.touch();

        //then
        assertThat(touchedMetadata.type()).isEqualTo(metadata.type());
        assertThat(touchedMetadata.status()).isEqualTo(metadata.status());
        assertThat(touchedMetadata.createdAt()).isEqualTo(metadata.createdAt());
        assertThat(touchedMetadata.updatedAt()).isAfter(metadata.updatedAt());
    }

    @Test
    @DisplayName("활성 상태 확인 - NORMAL 상태")
    void isActiveWhenStatusIsNormal() {
        //given
        NoticeMetadata metadata = NoticeMetadata.of(NoticeType.NOTICE, NoticeStatus.NORMAL);

        //when & then
        assertThat(metadata.isActive()).isTrue();
        assertThat(metadata.isDeleted()).isFalse();
    }

    @Test
    @DisplayName("활성 상태 확인 - DELETED 상태")
    void isActiveWhenStatusIsDeleted() {
        //given
        NoticeMetadata metadata = NoticeMetadata.of(NoticeType.NOTICE, NoticeStatus.DELETED);

        //when & then
        assertThat(metadata.isActive()).isFalse();
        assertThat(metadata.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("삭제 상태 확인 - DELETED 상태")
    void isDeletedWhenStatusIsDeleted() {
        //given
        NoticeMetadata metadata = NoticeMetadata.of(NoticeType.NOTICE, NoticeStatus.DELETED);

        //when & then
        assertThat(metadata.isDeleted()).isTrue();
        assertThat(metadata.isActive()).isFalse();
    }

    @Test
    @DisplayName("삭제 상태 확인 - NORMAL 상태")
    void isDeletedWhenStatusIsNormal() {
        //given
        NoticeMetadata metadata = NoticeMetadata.of(NoticeType.NOTICE, NoticeStatus.NORMAL);

        //when & then
        assertThat(metadata.isDeleted()).isFalse();
        assertThat(metadata.isActive()).isTrue();
    }

    @Test
    @DisplayName("모든 NoticeType으로 메타데이터 생성")
    void createMetadataWithAllNoticeTypes() {
        //given & when & then
        for (NoticeType type : NoticeType.values()) {
            NoticeMetadata metadata = NoticeMetadata.of(type, NoticeStatus.NORMAL);
            assertThat(metadata.type()).isEqualTo(type);
            assertThat(metadata.status()).isEqualTo(NoticeStatus.NORMAL);
            assertThat(metadata.isActive()).isTrue();
        }
    }

    @Test
    @DisplayName("모든 NoticeStatus로 메타데이터 생성")
    void createMetadataWithAllNoticeStatuses() {
        //given & when & then
        for (NoticeStatus status : NoticeStatus.values()) {
            NoticeMetadata metadata = NoticeMetadata.of(NoticeType.NOTICE, status);
            assertThat(metadata.type()).isEqualTo(NoticeType.NOTICE);
            assertThat(metadata.status()).isEqualTo(status);
        }
    }

    @Test
    @DisplayName("NoticeMetadata 동등성 테스트")
    void equalityTest() {
        //given
        LocalDateTime now = LocalDateTime.now();
        NoticeMetadata metadata1 = NoticeMetadata.restore(NoticeType.NOTICE, NoticeStatus.NORMAL, now, now);
        NoticeMetadata metadata2 = NoticeMetadata.restore(NoticeType.NOTICE, NoticeStatus.NORMAL, now, now);
        NoticeMetadata metadata3 = NoticeMetadata.restore(NoticeType.EVENT, NoticeStatus.NORMAL, now, now);

        //when & then
        assertThat(metadata1).isEqualTo(metadata2);
        assertThat(metadata1).isNotEqualTo(metadata3);
        assertThat(metadata1.hashCode()).isEqualTo(metadata2.hashCode());
    }

    @Test
    @DisplayName("타입과 상태 연속 업데이트")
    void updateTypeAndStatusSequentially() {
        //given
        NoticeMetadata originalMetadata = NoticeMetadata.of(NoticeType.NOTICE, NoticeStatus.NORMAL);

        //when
        NoticeMetadata updatedMetadata = originalMetadata
                .updateType(NoticeType.EVENT)
                .updateStatus(NoticeStatus.DELETED);

        //then
        assertThat(updatedMetadata.type()).isEqualTo(NoticeType.EVENT);
        assertThat(updatedMetadata.status()).isEqualTo(NoticeStatus.DELETED);
        assertThat(updatedMetadata.createdAt()).isEqualTo(originalMetadata.createdAt());
        assertThat(updatedMetadata.updatedAt()).isAfter(originalMetadata.updatedAt());
        assertThat(updatedMetadata.isActive()).isFalse();
        assertThat(updatedMetadata.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("메타데이터 불변성 확인")
    void metadataImmutability() {
        //given
        NoticeMetadata originalMetadata = NoticeMetadata.of(NoticeType.NOTICE, NoticeStatus.NORMAL);

        //when
        NoticeMetadata updatedMetadata = originalMetadata.updateType(NoticeType.EVENT);

        //then
        assertThat(originalMetadata.type()).isEqualTo(NoticeType.NOTICE);
        assertThat(updatedMetadata.type()).isEqualTo(NoticeType.EVENT);
        assertThat(originalMetadata).isNotEqualTo(updatedMetadata);
    }

    @Test
    @DisplayName("생성 시간과 업데이트 시간의 순서 확인")
    void timestampOrderValidation() {
        //given
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now();

        //when
        NoticeMetadata metadata = NoticeMetadata.restore(NoticeType.NOTICE, NoticeStatus.NORMAL, createdAt, updatedAt);

        //then
        assertThat(metadata.createdAt()).isBefore(metadata.updatedAt());
    }
}