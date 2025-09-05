package com.backend.immilog.shared.application.query;

/**
 * 페이징 요청 정보를 담는 클래스
 * Spring Data의 Pageable과 유사하지만 도메인에 특화된 구현
 */
public class PageRequest {
    
    private final int pageNumber;
    private final int pageSize;
    private final long offset;
    
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 1000;
    
    private PageRequest(int pageNumber, int pageSize) {
        this.pageNumber = Math.max(0, pageNumber);
        this.pageSize = Math.min(Math.max(1, pageSize), MAX_PAGE_SIZE);
        this.offset = (long) this.pageNumber * this.pageSize;
    }
    
    // Static factory methods
    public static PageRequest of(int pageNumber, int pageSize) {
        return new PageRequest(pageNumber, pageSize);
    }
    
    public static PageRequest of(int pageNumber) {
        return new PageRequest(pageNumber, DEFAULT_PAGE_SIZE);
    }
    
    public static PageRequest first(int size) {
        return new PageRequest(0, size);
    }
    
    public static PageRequest defaultPage() {
        return new PageRequest(0, DEFAULT_PAGE_SIZE);
    }
    
    // Getter methods
    public int getPageNumber() {
        return pageNumber;
    }
    
    public int getPageSize() {
        return pageSize;
    }
    
    public long getOffset() {
        return offset;
    }
    
    // Navigation methods
    public PageRequest next() {
        return new PageRequest(pageNumber + 1, pageSize);
    }
    
    public PageRequest previous() {
        return pageNumber == 0 ? this : new PageRequest(pageNumber - 1, pageSize);
    }
    
    public PageRequest first() {
        return new PageRequest(0, pageSize);
    }
    
    public boolean hasPrevious() {
        return pageNumber > 0;
    }
    
    // Validation methods
    public boolean isValidPage() {
        return pageNumber >= 0 && pageSize > 0 && pageSize <= MAX_PAGE_SIZE;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        PageRequest that = (PageRequest) obj;
        return pageNumber == that.pageNumber && pageSize == that.pageSize;
    }
    
    @Override
    public int hashCode() {
        return 31 * pageNumber + pageSize;
    }
    
    @Override
    public String toString() {
        return String.format("PageRequest{page=%d, size=%d, offset=%d}", pageNumber, pageSize, offset);
    }
}