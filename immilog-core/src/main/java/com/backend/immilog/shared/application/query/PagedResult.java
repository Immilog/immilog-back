package com.backend.immilog.shared.application.query;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 페이징된 결과를 담는 클래스
 * Spring Data의 Page와 유사하지만 도메인에 특화된 구현
 */
public class PagedResult<T> {
    
    private final List<T> content;
    private final PageRequest pageRequest;
    private final long totalElements;
    private final int totalPages;
    private final boolean hasNext;
    private final boolean hasPrevious;
    private final boolean isFirst;
    private final boolean isLast;
    
    public PagedResult(List<T> content, PageRequest pageRequest, long totalElements) {
        this.content = List.copyOf(content != null ? content : List.of());
        this.pageRequest = pageRequest;
        this.totalElements = Math.max(0, totalElements);
        this.totalPages = pageRequest.getPageSize() == 0 ? 1 : (int) Math.ceil((double) this.totalElements / pageRequest.getPageSize());
        this.hasNext = pageRequest.getPageNumber() + 1 < totalPages;
        this.hasPrevious = pageRequest.getPageNumber() > 0;
        this.isFirst = pageRequest.getPageNumber() == 0;
        this.isLast = pageRequest.getPageNumber() + 1 >= totalPages;
    }
    
    // Static factory methods
    public static <T> PagedResult<T> empty(PageRequest pageRequest) {
        return new PagedResult<>(List.of(), pageRequest, 0);
    }
    
    public static <T> PagedResult<T> of(List<T> content, PageRequest pageRequest, long totalElements) {
        return new PagedResult<>(content, pageRequest, totalElements);
    }
    
    // Content access
    public List<T> getContent() {
        return content;
    }
    
    public int getNumberOfElements() {
        return content.size();
    }
    
    public boolean isEmpty() {
        return content.isEmpty();
    }
    
    public boolean hasContent() {
        return !content.isEmpty();
    }
    
    // Page information
    public PageRequest getPageRequest() {
        return pageRequest;
    }
    
    public int getPageNumber() {
        return pageRequest.getPageNumber();
    }
    
    public int getPageSize() {
        return pageRequest.getPageSize();
    }
    
    public long getTotalElements() {
        return totalElements;
    }
    
    public int getTotalPages() {
        return totalPages;
    }
    
    // Navigation information
    public boolean hasNext() {
        return hasNext;
    }
    
    public boolean hasPrevious() {
        return hasPrevious;
    }
    
    public boolean isFirst() {
        return isFirst;
    }
    
    public boolean isLast() {
        return isLast;
    }
    
    // Navigation methods
    public PageRequest nextPageable() {
        return hasNext ? pageRequest.next() : pageRequest;
    }
    
    public PageRequest previousPageable() {
        return hasPrevious ? pageRequest.previous() : pageRequest;
    }
    
    // Transformation methods
    public <U> PagedResult<U> map(Function<? super T, ? extends U> mapper) {
        List<U> mappedContent = content.stream()
                .map(mapper)
                .collect(Collectors.toList());
        return new PagedResult<>(mappedContent, pageRequest, totalElements);
    }
    
    // Utility methods
    public boolean isValidResult() {
        return pageRequest.isValidPage() && totalElements >= 0;
    }
    
    @Override
    public String toString() {
        return String.format("PagedResult{content=%d items, page=%d, size=%d, totalElements=%d, totalPages=%d}", 
                content.size(), getPageNumber(), getPageSize(), totalElements, totalPages);
    }
}