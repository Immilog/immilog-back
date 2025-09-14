package com.backend.immilog.interaction.domain.service;

import com.backend.immilog.interaction.domain.model.InteractionType;
import com.backend.immilog.interaction.domain.model.InteractionUser;
import com.backend.immilog.interaction.exception.InteractionErrorCode;
import com.backend.immilog.interaction.exception.InteractionException;
import com.backend.immilog.shared.enums.ContentType;
import org.springframework.stereotype.Service;

public interface InteractionDomainService {
    
    void validateInteractionRules(InteractionUser interaction);
    
    boolean canUserInteract(String userId, String postId, InteractionType interactionType, ContentType contentType);
    
    void validateUserPermissions(String userId, String postId);
    
    void validatePostExists(String postId, ContentType contentType);
    
    void validateInteractionLimits(String userId, InteractionType interactionType);

    @Service
    class InteractionDomainServiceImpl implements InteractionDomainService {

        @Override
        public void validateInteractionRules(InteractionUser interaction) {
            if (interaction == null) {
                throw new InteractionException(InteractionErrorCode.INTERACTION_CREATE_FAILED);
            }

            if (interaction.userId() == null || interaction.userId().trim().isEmpty()) {
                throw new InteractionException(InteractionErrorCode.INTERACTION_CREATE_FAILED);
            }

            if (interaction.postId() == null || interaction.postId().trim().isEmpty()) {
                throw new InteractionException(InteractionErrorCode.INTERACTION_CREATE_FAILED);
            }

            if (interaction.contentType() == null) {
                throw new InteractionException(InteractionErrorCode.INTERACTION_CREATE_FAILED);
            }

            if (interaction.interactionType() == null) {
                throw new InteractionException(InteractionErrorCode.INTERACTION_CREATE_FAILED);
            }
        }

        @Override
        public boolean canUserInteract(String userId, String postId, InteractionType interactionType, ContentType contentType) {
            if (userId == null || userId.trim().isEmpty()) {
                return false;
            }

            if (postId == null || postId.trim().isEmpty()) {
                return false;
            }

            if (interactionType == null || contentType == null) {
                return false;
            }

            return true;
        }

        @Override
        public void validateUserPermissions(String userId, String postId) {
            if (userId == null || userId.trim().isEmpty()) {
                throw new InteractionException(InteractionErrorCode.INTERACTION_CREATE_FAILED);
            }
        }

        @Override
        public void validatePostExists(String postId, ContentType contentType) {
            if (postId == null || postId.trim().isEmpty()) {
                throw new InteractionException(InteractionErrorCode.INTERACTION_CREATE_FAILED);
            }

            if (contentType == null) {
                throw new InteractionException(InteractionErrorCode.INTERACTION_CREATE_FAILED);
            }
        }

        @Override
        public void validateInteractionLimits(String userId, InteractionType interactionType) {
            if (userId == null || userId.trim().isEmpty()) {
                throw new InteractionException(InteractionErrorCode.INTERACTION_CREATE_FAILED);
            }

            if (interactionType == null) {
                throw new InteractionException(InteractionErrorCode.INTERACTION_CREATE_FAILED);
            }
        }
    }
}