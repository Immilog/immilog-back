package com.backend.immilog.post.domain.model.post;

public record CommentCount(Long value) {
    
    public CommentCount {
        if (value == null) {
            throw new IllegalArgumentException("Comment count cannot be null");
        }
        if (value < 0) {
            throw new IllegalArgumentException("Comment count cannot be negative");
        }
    }
    
    public static CommentCount zero() {
        return new CommentCount(0L);
    }
    
    public static CommentCount of(Long value) {
        return new CommentCount(value);
    }
    
    public CommentCount increment() {
        return new CommentCount(value + 1);
    }
    
    public CommentCount decrement() {
        return value > 0 ? new CommentCount(value - 1) : this;
    }
    
    public boolean isEmpty() {
        return value == 0;
    }
    
    public boolean hasComments() {
        return value > 0;
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
}