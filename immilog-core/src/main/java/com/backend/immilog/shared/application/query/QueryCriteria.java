package com.backend.immilog.shared.application.query;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 동적 쿼리 조건을 표현하는 클래스
 * 다양한 조건들을 조합하여 복잡한 쿼리 구성 가능
 */
public class QueryCriteria {
    
    private final Map<String, Object> conditions = new HashMap<>();
    private final Map<String, SortOrder> sortOrders = new HashMap<>();
    
    public enum SortOrder {
        ASC, DESC
    }
    
    // Static factory methods
    public static QueryCriteria empty() {
        return new QueryCriteria();
    }
    
    public static QueryCriteria create() {
        return new QueryCriteria();
    }
    
    // 기본 조건 메서드들
    public QueryCriteria equals(String field, Object value) {
        conditions.put(field + "_eq", value);
        return this;
    }
    
    public QueryCriteria notEquals(String field, Object value) {
        conditions.put(field + "_ne", value);
        return this;
    }
    
    public QueryCriteria greaterThan(String field, Object value) {
        conditions.put(field + "_gt", value);
        return this;
    }
    
    public QueryCriteria greaterThanOrEqual(String field, Object value) {
        conditions.put(field + "_gte", value);
        return this;
    }
    
    public QueryCriteria lessThan(String field, Object value) {
        conditions.put(field + "_lt", value);
        return this;
    }
    
    public QueryCriteria lessThanOrEqual(String field, Object value) {
        conditions.put(field + "_lte", value);
        return this;
    }
    
    public QueryCriteria like(String field, String value) {
        conditions.put(field + "_like", value);
        return this;
    }
    
    public QueryCriteria in(String field, List<?> values) {
        conditions.put(field + "_in", values);
        return this;
    }
    
    public QueryCriteria notIn(String field, List<?> values) {
        conditions.put(field + "_nin", values);
        return this;
    }
    
    public QueryCriteria isNull(String field) {
        conditions.put(field + "_null", true);
        return this;
    }
    
    public QueryCriteria isNotNull(String field) {
        conditions.put(field + "_notnull", true);
        return this;
    }
    
    public QueryCriteria between(String field, Object start, Object end) {
        conditions.put(field + "_between", List.of(start, end));
        return this;
    }
    
    // 날짜/시간 특화 메서드들
    public QueryCriteria createdAfter(LocalDateTime dateTime) {
        return greaterThan("createdAt", dateTime);
    }
    
    public QueryCriteria createdBefore(LocalDateTime dateTime) {
        return lessThan("createdAt", dateTime);
    }
    
    public QueryCriteria createdBetween(LocalDateTime start, LocalDateTime end) {
        return between("createdAt", start, end);
    }
    
    // 정렬 메서드들
    public QueryCriteria sortBy(String field, SortOrder order) {
        sortOrders.put(field, order);
        return this;
    }
    
    public QueryCriteria sortAsc(String field) {
        return sortBy(field, SortOrder.ASC);
    }
    
    public QueryCriteria sortDesc(String field) {
        return sortBy(field, SortOrder.DESC);
    }
    
    // 일반적인 정렬 패턴들
    public QueryCriteria sortByCreatedAtDesc() {
        return sortDesc("createdAt");
    }
    
    public QueryCriteria sortByUpdatedAtDesc() {
        return sortDesc("updatedAt");
    }
    
    // Getter 메서드들
    public Map<String, Object> getConditions() {
        return new HashMap<>(conditions);
    }
    
    public Map<String, SortOrder> getSortOrders() {
        return new HashMap<>(sortOrders);
    }
    
    public boolean hasConditions() {
        return !conditions.isEmpty();
    }
    
    public boolean hasSortOrders() {
        return !sortOrders.isEmpty();
    }
    
    @Override
    public String toString() {
        return String.format("QueryCriteria{conditions=%s, sortOrders=%s}", conditions, sortOrders);
    }
}