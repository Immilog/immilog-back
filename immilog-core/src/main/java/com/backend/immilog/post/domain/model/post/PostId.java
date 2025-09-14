package com.backend.immilog.post.domain.model.post;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;

import java.util.Objects;

public record PostId(String value) {
    
    public PostId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("PostId cannot be null or blank");
        }
        if (value.length() > 255) {
            throw new IllegalArgumentException("PostId cannot exceed 255 characters");
        }
    }
    
    public static PostId generate() {
        return new PostId(NanoIdUtils.randomNanoId());
    }
    
    public static PostId of(String value) {
        return new PostId(value);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PostId postId = (PostId) obj;
        return Objects.equals(value, postId.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    @Override
    public String toString() {
        return value;
    }
}