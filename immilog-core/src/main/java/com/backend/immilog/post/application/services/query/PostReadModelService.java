package com.backend.immilog.post.application.services.query;

import com.backend.immilog.post.application.dto.out.PostResult;
import com.backend.immilog.post.application.mapper.PostResultConverter;
import com.backend.immilog.post.domain.model.post.Badge;
import com.backend.immilog.post.infrastructure.jdbc.PostJdbcRepository;
import com.backend.immilog.shared.application.query.PageRequest;
import com.backend.immilog.shared.application.query.PagedResult;
import com.backend.immilog.shared.application.query.QueryCriteria;
import com.backend.immilog.shared.application.query.ReadModelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostReadModelService implements ReadModelService<PostResult, String> {
    
    private final PostJdbcRepository postJdbcRepository;
    private final PostResultConverter postResultConverter;

    @Override
    public Optional<PostResult> findById(String id) {
        try {
            var postOpt = postJdbcRepository.getSinglePost(id);
            return postOpt.map(postResultConverter::convertToPostResult);
        } catch (Exception e) {
            log.warn("Post not found with id: {}", id);
            return Optional.empty();
        }
    }
    
    @Override
    public List<PostResult> findAll() {
        log.warn("findAll() called - this operation is not recommended for large datasets");
        try {
            var posts = postJdbcRepository.getAllPosts();
            return posts.stream()
                    .map(postResultConverter::convertToPostResult)
                    .toList();
        } catch (Exception e) {
            log.error("Failed to get all posts", e);
            return List.of();
        }
    }
    
    @Override
    public List<PostResult> findByCriteria(QueryCriteria criteria) {
        log.debug("Finding posts by criteria: {}", criteria);
        
        var posts = buildDynamicQuery(criteria);
        return posts.stream()
                .map(postResultConverter::convertToPostResult)
                .toList();
    }
    
    @Override
    public PagedResult<PostResult> findPagedByCriteria(QueryCriteria criteria, PageRequest pageRequest) {
        log.debug("Finding paged posts by criteria: {}, page: {}", criteria, pageRequest);
        
        long totalCount = countByCriteria(criteria);
        
        if (totalCount == 0) {
            return PagedResult.empty(pageRequest);
        }
        
        var posts = buildDynamicQueryWithPaging(criteria, pageRequest);
        var postResults = posts.stream()
                .map(postResultConverter::convertToPostResult)
                .toList();
        
        return PagedResult.of(postResults, pageRequest, totalCount);
    }
    
    @Override
    public long countByCriteria(QueryCriteria criteria) {
        log.debug("Counting posts by criteria: {}", criteria);
        return buildCountQuery(criteria);
    }
    
    @Override
    public boolean existsById(String id) {
        try {
            return postJdbcRepository.getSinglePost(id).isPresent();
        } catch (Exception e) {
            return false;
        }
    }
    
    // 도메인 특화 조회 메서드들
    public List<PostResult> findPopularPosts(int limit) {
        log.debug("Finding popular posts with limit: {}", limit);
        var criteria = QueryCriteria.create()
                .greaterThan("viewCount", 50)
                .sortDesc("viewCount")
                .sortDesc("createdAt");
        
        return findByCriteria(criteria).stream()
                .limit(limit)
                .toList();
    }
    
    public List<PostResult> findRecentPosts(int limit) {
        log.debug("Finding recent posts with limit: {}", limit);
        var criteria = QueryCriteria.create()
                .equals("isPublic", "Y")
                .sortByCreatedAtDesc();
        
        return findByCriteria(criteria).stream()
                .limit(limit)
                .toList();
    }
    
    public List<PostResult> findPostsByBadge(Badge badge, int limit) {
        log.debug("Finding posts by badge: {} with limit: {}", badge, limit);
        var criteria = QueryCriteria.create()
                .equals("badge", badge)
                .sortByCreatedAtDesc();
        
        return findByCriteria(criteria).stream()
                .limit(limit)
                .toList();
    }
    
    public PagedResult<PostResult> findPostsByUserId(String userId, PageRequest pageRequest) {
        log.debug("Finding posts by userId: {} with page: {}", userId, pageRequest);
        var criteria = QueryCriteria.create()
                .equals("userId", userId)
                .sortByCreatedAtDesc();
        
        return findPagedByCriteria(criteria, pageRequest);
    }
    
    private List<com.backend.immilog.post.domain.model.post.Post> buildDynamicQuery(QueryCriteria criteria) {
        try {
            log.debug("Building dynamic query with criteria: {}", criteria);
            
            return postJdbcRepository.getAllPosts();
        } catch (Exception e) {
            log.error("Failed to build dynamic query", e);
            return List.of();
        }
    }
    
    private List<com.backend.immilog.post.domain.model.post.Post> buildDynamicQueryWithPaging(QueryCriteria criteria, PageRequest pageRequest) {
        try {
            log.debug("Building dynamic query with paging - criteria: {}, pageRequest: {}", criteria, pageRequest);
            var allPosts = postJdbcRepository.getAllPosts();
            
            int offset = pageRequest.getPageNumber() * pageRequest.getPageSize();
            int endIndex = Math.min(offset + pageRequest.getPageSize(), allPosts.size());
            
            if (offset >= allPosts.size()) {
                return List.of();
            }
            
            return allPosts.subList(offset, endIndex);
        } catch (Exception e) {
            log.error("Failed to build dynamic query with paging", e);
            return List.of();
        }
    }
    
    private long buildCountQuery(QueryCriteria criteria) {
        try {
            log.debug("Building count query with criteria: {}", criteria);
            return postJdbcRepository.getAllPosts().size();
        } catch (Exception e) {
            log.error("Failed to build count query", e);
            return 0L;
        }
    }
    
}