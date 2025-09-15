package com.backend.immilog.comment.application.services;

import com.backend.immilog.comment.application.dto.CommentResult;
import com.backend.immilog.comment.domain.model.ReferenceType;
import com.backend.immilog.interaction.domain.model.InteractionStatus;
import com.backend.immilog.interaction.domain.model.InteractionType;
import com.backend.immilog.interaction.domain.repositories.InteractionUserRepository;
import com.backend.immilog.interaction.domain.model.InteractionUser;
import com.backend.immilog.shared.domain.model.UserData;
import com.backend.immilog.shared.enums.ContentStatus;
import com.backend.immilog.shared.enums.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("CommentHierarchyService")
@ExtendWith(MockitoExtension.class)
class CommentHierarchyServiceTest {

    @Mock
    private InteractionUserRepository interactionUserRepository;

    @InjectMocks
    private CommentHierarchyService commentHierarchyService;

    @Nested
    @DisplayName("댓글 계층 구조 구성")
    class BuildHierarchy {

        @Test
        @DisplayName("단일 댓글 계층 구성 성공")
        void buildHierarchyWithSingleComment() {
            var comment = createCommentResult("comment1", "user1", "post1", null);
            var userData = new UserData("user1", "닉네임1", "profile1.jpg", "KR", "Seoul");
            var comments = List.of(comment);
            var userDataList = List.of(userData);

            when(interactionUserRepository.countByCommentIdAndInteractionTypeAndInteractionStatus(
                    "comment1", InteractionType.LIKE, InteractionStatus.ACTIVE
            )).thenReturn(5L);

            when(interactionUserRepository.findByPostIdListAndContentTypeAndInteractionStatus(
                    List.of("comment1"), ContentType.COMMENT, InteractionStatus.ACTIVE
            )).thenReturn(createInteractionResults());

            var result = commentHierarchyService.buildHierarchy(comments, userDataList);

            assertThat(result).hasSize(1);
            var commentInfo = result.getFirst();
            assertThat(commentInfo.commentId()).isEqualTo("comment1");
            assertThat(commentInfo.nickname()).isEqualTo("닉네임1");
            assertThat(commentInfo.userProfileUrl()).isEqualTo("profile1.jpg");
            assertThat(commentInfo.likeCount()).isEqualTo(5);
            assertThat(commentInfo.replies()).isEmpty();

            verify(interactionUserRepository).countByCommentIdAndInteractionTypeAndInteractionStatus(
                    "comment1", InteractionType.LIKE, InteractionStatus.ACTIVE
            );
            verify(interactionUserRepository).findByPostIdListAndContentTypeAndInteractionStatus(
                    List.of("comment1"), ContentType.COMMENT, InteractionStatus.ACTIVE
            );
        }

        @Test
        @DisplayName("부모-자식 댓글 계층 구조 구성")
        void buildHierarchyWithParentChild() {
            var parentComment = createCommentResult("parent1", "user1", "post1", null);
            var childComment = createCommentResult("child1", "user2", "post1", "parent1");
            var comments = List.of(parentComment, childComment);
            
            var userData1 = new UserData("user1", "부모유저", "parent.jpg", "KR", "Seoul");
            var userData2 = new UserData("user2", "자식유저", "child.jpg", "KR", "Busan");
            var userDataList = List.of(userData1, userData2);

            when(interactionUserRepository.countByCommentIdAndInteractionTypeAndInteractionStatus(
                    anyString(), eq(InteractionType.LIKE), eq(InteractionStatus.ACTIVE)
            )).thenReturn(0L);

            when(interactionUserRepository.findByPostIdListAndContentTypeAndInteractionStatus(
                    anyList(), eq(ContentType.COMMENT), eq(InteractionStatus.ACTIVE)
            )).thenReturn(List.of());

            var result = commentHierarchyService.buildHierarchy(comments, userDataList);

            assertThat(result).hasSize(1);
            var parentInfo = result.getFirst();
            assertThat(parentInfo.commentId()).isEqualTo("parent1");
            assertThat(parentInfo.nickname()).isEqualTo("부모유저");
            assertThat(parentInfo.replies()).hasSize(1);

            var childInfo = parentInfo.replies().getFirst();
            assertThat(childInfo.commentId()).isEqualTo("child1");
            assertThat(childInfo.nickname()).isEqualTo("자식유저");
            assertThat(childInfo.parentId()).isEqualTo("parent1");
        }

        @Test
        @DisplayName("다중 레벨 댓글 계층 구조 구성")
        void buildHierarchyWithMultipleLevel() {
            var rootComment = createCommentResult("root1", "user1", "post1", null);
            var level1Comment = createCommentResult("level1", "user2", "post1", "root1");
            var level2Comment = createCommentResult("level2", "user3", "post1", "level1");
            var comments = List.of(rootComment, level1Comment, level2Comment);

            var userData1 = new UserData("user1", "루트유저", "root.jpg", "KR", "Seoul");
            var userData2 = new UserData("user2", "레벨1유저", "level1.jpg", "US", "NY");
            var userData3 = new UserData("user3", "레벨2유저", "level2.jpg", "JP", "Tokyo");
            var userDataList = List.of(userData1, userData2, userData3);

            when(interactionUserRepository.countByCommentIdAndInteractionTypeAndInteractionStatus(
                    anyString(), eq(InteractionType.LIKE), eq(InteractionStatus.ACTIVE)
            )).thenReturn(0L);

            when(interactionUserRepository.findByPostIdListAndContentTypeAndInteractionStatus(
                    anyList(), eq(ContentType.COMMENT), eq(InteractionStatus.ACTIVE)
            )).thenReturn(List.of());

            var result = commentHierarchyService.buildHierarchy(comments, userDataList);

            assertThat(result).hasSize(1);
            var rootInfo = result.getFirst();
            assertThat(rootInfo.nickname()).isEqualTo("루트유저");
            assertThat(rootInfo.replies()).hasSize(1);

            var level1Info = rootInfo.replies().getFirst();
            assertThat(level1Info.nickname()).isEqualTo("레벨1유저");
            assertThat(level1Info.replies()).hasSize(1);

            var level2Info = level1Info.replies().getFirst();
            assertThat(level2Info.nickname()).isEqualTo("레벨2유저");
            assertThat(level2Info.replies()).isEmpty();
        }

        @Test
        @DisplayName("사용자 데이터가 없는 경우 기본값 사용")
        void buildHierarchyWithMissingUserData() {
            var comment = createCommentResult("comment1", "user999", "post1", null);
            var comments = List.of(comment);
            var userDataList = List.<UserData>of();

            when(interactionUserRepository.countByCommentIdAndInteractionTypeAndInteractionStatus(
                    "comment1", InteractionType.LIKE, InteractionStatus.ACTIVE
            )).thenReturn(0L);

            when(interactionUserRepository.findByPostIdListAndContentTypeAndInteractionStatus(
                    List.of("comment1"), ContentType.COMMENT, InteractionStatus.ACTIVE
            )).thenReturn(List.of());

            var result = commentHierarchyService.buildHierarchy(comments, userDataList);

            assertThat(result).hasSize(1);
            var commentInfo = result.getFirst();
            assertThat(commentInfo.nickname()).isEqualTo("기본닉네임");
            assertThat(commentInfo.userProfileUrl()).isEqualTo("default.jpg");
        }

        @Test
        @DisplayName("빈 댓글 목록으로 빈 계층 반환")
        void buildHierarchyWithEmptyComments() {
            var result = commentHierarchyService.buildHierarchy(List.of(), List.of());

            assertThat(result).isEmpty();
            verifyNoInteractions(interactionUserRepository);
        }

        @Test
        @DisplayName("좋아요 및 북마크 사용자 정보 포함")
        void buildHierarchyWithLikeAndBookmarkUsers() {
            var comment = createCommentResult("comment1", "user1", "post1", null);
            var userData = new UserData("user1", "테스트유저", "test.jpg", "KR", "Seoul");
            var comments = List.of(comment);
            var userDataList = List.of(userData);

            when(interactionUserRepository.countByCommentIdAndInteractionTypeAndInteractionStatus(
                    "comment1", InteractionType.LIKE, InteractionStatus.ACTIVE
            )).thenReturn(3L);

            var interactions = List.of(
                    InteractionUser.builder().id("int1").userId("user2").postId("comment1").contentType(ContentType.COMMENT).interactionType(InteractionType.LIKE).interactionStatus(InteractionStatus.ACTIVE).createdAt(LocalDateTime.now()).build(),
                    InteractionUser.builder().id("int2").userId("user3").postId("comment1").contentType(ContentType.COMMENT).interactionType(InteractionType.LIKE).interactionStatus(InteractionStatus.ACTIVE).createdAt(LocalDateTime.now()).build(),
                    InteractionUser.builder().id("int3").userId("user4").postId("comment1").contentType(ContentType.COMMENT).interactionType(InteractionType.BOOKMARK).interactionStatus(InteractionStatus.ACTIVE).createdAt(LocalDateTime.now()).build()
            );

            when(interactionUserRepository.findByPostIdListAndContentTypeAndInteractionStatus(
                    List.of("comment1"), ContentType.COMMENT, InteractionStatus.ACTIVE
            )).thenReturn(interactions);

            var result = commentHierarchyService.buildHierarchy(comments, userDataList);

            assertThat(result).hasSize(1);
            var commentInfo = result.get(0);
            assertThat(commentInfo.likeCount()).isEqualTo(3);
            assertThat(commentInfo.likeUsers()).containsExactly("user2", "user3");
            assertThat(commentInfo.bookmarkUsers()).containsExactly("user4");
        }
    }

    private CommentResult createCommentResult(String id, String userId, String postId, String parentId) {
        return new CommentResult(
                id,
                userId,
                "기본닉네임",
                "default.jpg",
                "KR",
                "Seoul",
                "댓글 내용",
                postId,
                parentId,
                ReferenceType.POST,
                0,
                0,
                ContentStatus.NORMAL,
                LocalDateTime.now(),
                null
        );
    }

    private List<InteractionUser> createInteractionResults() {
        return List.of(
                InteractionUser.builder().id("int1").userId("user2").postId("comment1").contentType(ContentType.COMMENT).interactionType(InteractionType.LIKE).interactionStatus(InteractionStatus.ACTIVE).createdAt(LocalDateTime.now()).build(),
                InteractionUser.builder().id("int2").userId("user3").postId("comment1").contentType(ContentType.COMMENT).interactionType(InteractionType.BOOKMARK).interactionStatus(InteractionStatus.ACTIVE).createdAt(LocalDateTime.now()).build()
        );
    }
}