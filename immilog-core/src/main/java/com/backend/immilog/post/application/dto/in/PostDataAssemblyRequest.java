package com.backend.immilog.post.application.dto.in;

import com.backend.immilog.shared.domain.model.CommentData;
import com.backend.immilog.shared.domain.model.InteractionData;
import com.backend.immilog.shared.domain.model.UserData;

import java.util.List;

public record PostDataAssemblyRequest(
        List<UserData> userData,
        List<InteractionData> interactionData,
        List<CommentData> commentData
) {
}