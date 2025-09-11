package com.backend.immilog.post.domain.model.post;

public enum PublicStatus {
    PUBLIC("Y"),
    PRIVATE("N");
    
    private final String value;
    
    PublicStatus(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public boolean isPublic() {
        return this == PUBLIC;
    }
    
    public boolean isPrivate() {
        return this == PRIVATE;
    }
    
    public static PublicStatus fromValue(String value) {
        if (value == null) {
            throw new IllegalArgumentException("PublicStatus value cannot be null");
        }
        
        return switch (value.toUpperCase()) {
            case "Y", "TRUE", "PUBLIC" -> PUBLIC;
            case "N", "FALSE", "PRIVATE" -> PRIVATE;
            default -> throw new IllegalArgumentException("Invalid PublicStatus value: " + value);
        };
    }
    
    public static PublicStatus fromBoolean(Boolean isPublic) {
        if (isPublic == null) {
            throw new IllegalArgumentException("PublicStatus boolean cannot be null");
        }
        return isPublic ? PUBLIC : PRIVATE;
    }
    
    @Override
    public String toString() {
        return name().toLowerCase();
    }
}