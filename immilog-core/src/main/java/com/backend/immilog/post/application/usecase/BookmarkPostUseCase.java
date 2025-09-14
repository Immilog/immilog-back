package com.backend.immilog.post.application.usecase;

import com.backend.immilog.interaction.domain.model.InteractionStatus;
import com.backend.immilog.interaction.domain.model.InteractionType;
import com.backend.immilog.interaction.domain.model.InteractionUser;
import com.backend.immilog.interaction.domain.repositories.InteractionUserRepository;
import com.backend.immilog.post.application.services.InteractionToggleService;
import com.backend.immilog.post.domain.repositories.PostDomainRepository;
import com.backend.immilog.post.exception.PostErrorCode;
import com.backend.immilog.post.exception.PostException;
import com.backend.immilog.shared.enums.ContentType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface BookmarkPostUseCase {
    boolean toggleBookmark(String userId, String postId);
    
    @Slf4j
    @Service
    @RequiredArgsConstructor
    class BookmarkPostUseCaseImpl implements BookmarkPostUseCase {

        private final InteractionToggleService interactionToggleService;
        private final InteractionUserRepository interactionUserRepository;
        private final PostDomainRepository postDomainRepository;

        @Override
        @Transactional
        public boolean toggleBookmark(String userId, String postId) {
            validateInputs(userId, postId);
            validatePostExists(postId);
            
            var existingBookmark = findExistingBookmark(userId, postId);

            return existingBookmark.map(this::handleExistingBookmark).orElseGet(() -> createNewBookmark(userId, postId));
        }
        
        private void validateInputs(String userId, String postId) {
            if (userId == null || userId.isBlank()) {
                throw new PostException(PostErrorCode.INVALID_USER);
            }
            if (postId == null || postId.isBlank()) {
                throw new PostException(PostErrorCode.INVALID_POST_DATA);
            }
        }
        
        private void validatePostExists(String postId) {
            var post = postDomainRepository.findById(postId)
                    .orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_FOUND));
            
            // 삭제된 게시물은 북마크할 수 없음
            if (post.status() == com.backend.immilog.shared.enums.ContentStatus.DELETED) {
                throw new PostException(PostErrorCode.POST_ALREADY_DELETED);
            }
        }
        
        private Optional<InteractionUser> findExistingBookmark(String userId, String postId) {
            try {
                return interactionUserRepository.findByUserIdAndPostIdAndContentTypeAndInteractionType(
                        userId,
                        postId,
                        ContentType.POST,
                        InteractionType.BOOKMARK
                );
            } catch (Exception e) {
                log.warn("Failed to find existing bookmark for user {} and post {}: {}", userId, postId, e.getMessage());
                return java.util.Optional.empty();
            }
        }
        
        private boolean handleExistingBookmark(InteractionUser existingBookmark) {
            try {
                if (existingBookmark.interactionStatus() == InteractionStatus.ACTIVE) {
                    // 북마크가 활성화된 경우 → 비활성화
                    interactionToggleService.deactivateInteraction(existingBookmark.id());
                    log.info("Bookmark deactivated for user {} and post {}", existingBookmark.userId(), existingBookmark.postId());
                    return false;
                } else {
                    // 북마크가 비활성화된 경우 → 활성화
                    interactionToggleService.activateInteraction(existingBookmark.id());
                    log.info("Bookmark reactivated for user {} and post {}", existingBookmark.userId(), existingBookmark.postId());
                    return true;
                }
            } catch (Exception e) {
                log.error("Failed to toggle bookmark status: {}", e.getMessage());
                throw new PostException(PostErrorCode.FAILED_TO_SAVE_POST);
            }
        }
        
        private boolean createNewBookmark(String userId, String postId) {
            try {
                var newBookmark = InteractionUser.createBookmark(userId, postId, ContentType.POST);
                interactionUserRepository.save(newBookmark);
                log.info("New bookmark created for user {} and post {}", userId, postId);
                return true;
            } catch (Exception e) {
                log.error("Failed to create new bookmark: {}", e.getMessage());
                throw new PostException(PostErrorCode.FAILED_TO_SAVE_POST);
            }
        }
    }
}