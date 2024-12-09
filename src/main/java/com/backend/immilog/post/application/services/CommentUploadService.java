package com.backend.immilog.post.application.services;

import com.backend.immilog.post.exception.PostException;
import com.backend.immilog.post.domain.model.Comment;
import com.backend.immilog.post.domain.model.Post;
import com.backend.immilog.post.domain.model.enums.ReferenceType;
import com.backend.immilog.post.domain.repositories.CommentRepository;
import com.backend.immilog.post.domain.repositories.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.backend.immilog.post.exception.PostErrorCode.POST_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentUploadService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Transactional
    public void uploadComment(
            Long userId,
            Long postSeq,
            String referenceType,
            String content
    ) {
        Post post = getPost(postSeq);
        post = post.copyWithNewCommentCount(post.commentCount() + 1);
        ReferenceType reference = ReferenceType.getByString(referenceType);
        Comment comment = Comment.of(userId, postSeq, content, reference);
        postRepository.save(post);
        commentRepository.saveEntity(comment);
    }

    private Post getPost(
            Long postSeq
    ) {
        return postRepository
                .getById(postSeq)
                .orElseThrow(() -> new PostException(POST_NOT_FOUND));
    }
}
