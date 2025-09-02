package com.backend.immilog.shared.domain.event;

public final class DomainEventTypes {
    
    private DomainEventTypes() {
    }
    
    public static final String USER_REGISTERED = "user.registered";
    public static final String USER_UPDATED = "user.updated";
    public static final String USER_DELETED = "user.deleted";
    public static final String USER_DATA_REQUESTED = "user.data.requested";
    public static final String USER_DATA_RESPONSE = "user.data.response";
    
    public static final String POST_CREATED = "post.created";
    public static final String POST_UPDATED = "post.updated";
    public static final String POST_DELETED = "post.deleted";
    public static final String POST_VIEWED = "post.viewed";
    public static final String POST_LIKED = "post.liked";
    public static final String POST_BOOKMARKED = "post.bookmarked";
    
    public static final String COMMENT_CREATED = "comment.created";
    public static final String COMMENT_UPDATED = "comment.updated";
    public static final String COMMENT_DELETED = "comment.deleted";
    
    public static final String INTERACTION_CREATED = "interaction.created";
    public static final String INTERACTION_UPDATED = "interaction.updated";
    public static final String INTERACTION_DELETED = "interaction.deleted";
    public static final String INTERACTION_DATA_REQUESTED = "interaction.data.requested";
    public static final String INTERACTION_DATA_RESPONSE = "interaction.data.response";
    
    public static final String COMMENT_COUNT_INCREASE_COMPENSATION = "compensation.comment.count.increase";
    public static final String COMMENT_COUNT_DECREASE_COMPENSATION = "compensation.comment.count.decrease";
    public static final String LIKE_COUNT_INCREASE_COMPENSATION = "compensation.like.count.increase";
    public static final String LIKE_COUNT_DECREASE_COMPENSATION = "compensation.like.count.decrease";
    
    public static final String SYSTEM_MAINTENANCE_START = "system.maintenance.start";
    public static final String SYSTEM_MAINTENANCE_END = "system.maintenance.end";
    public static final String BATCH_JOB_STARTED = "batch.job.started";
    public static final String BATCH_JOB_COMPLETED = "batch.job.completed";
    public static final String BATCH_JOB_FAILED = "batch.job.failed";
}