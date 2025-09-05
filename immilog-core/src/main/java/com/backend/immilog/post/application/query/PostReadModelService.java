package com.backend.immilog.post.application.query;

import com.backend.immilog.post.application.dto.PostResult;
import com.backend.immilog.shared.application.query.PageRequest;
import com.backend.immilog.shared.application.query.PagedResult;
import com.backend.immilog.shared.application.query.QueryCriteria;
import com.backend.immilog.shared.application.query.ReadModelService;
import com.backend.immilog.post.domain.model.post.Badge;
import com.backend.immilog.post.infrastructure.jdbc.PostJdbcRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Post 도메인의 CQRS Read Model 서비스
 * 복잡한 조회 쿼리와 성능 최적화를 담당
 */
@Slf4j
@Service
public class PostReadModelService implements ReadModelService<PostResult, String> {
    
    private final PostJdbcRepository postJdbcRepository;
    
    public PostReadModelService(PostJdbcRepository postJdbcRepository) {
        this.postJdbcRepository = postJdbcRepository;
    }
    
    @Override
    public Optional<PostResult> findById(String id) {
        try {
            var postOpt = postJdbcRepository.getSinglePost(id);
            return postOpt.map(this::convertToPostResult);
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
                    .map(this::convertToPostResult)
                    .toList();
        } catch (Exception e) {
            log.error("Failed to get all posts", e);
            return List.of();
        }
    }
    
    @Override
    public List<PostResult> findByCriteria(QueryCriteria criteria) {
        log.debug("Finding posts by criteria: {}", criteria);
        
        // QueryCriteria를 기반으로 동적 쿼리 생성
        var posts = buildDynamicQuery(criteria);
        return posts.stream()
                .map(this::convertToPostResult)
                .toList();
    }
    
    @Override
    public PagedResult<PostResult> findPagedByCriteria(QueryCriteria criteria, PageRequest pageRequest) {
        log.debug("Finding paged posts by criteria: {}, page: {}", criteria, pageRequest);
        
        // 전체 개수 조회
        long totalCount = countByCriteria(criteria);
        
        if (totalCount == 0) {
            return PagedResult.empty(pageRequest);
        }
        
        // 페이징된 데이터 조회
        var posts = buildDynamicQueryWithPaging(criteria, pageRequest);
        var postResults = posts.stream()
                .map(this::convertToPostResult)
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
    
    // Private helper methods
    
    private List<com.backend.immilog.post.domain.model.post.Post> buildDynamicQuery(QueryCriteria criteria) {
        try {
            // QueryCriteria를 바탕으로 동적 쿼리 생성
            // 실제 구현에서는 QueryDSL이나 JOOQ 사용 권장
            log.debug("Building dynamic query with criteria: {}", criteria);
            
            // 현재는 기본 getAllPosts를 사용하며, 나중에 criteria에 따른 필터링 추가 예정
            return postJdbcRepository.getAllPosts();
        } catch (Exception e) {
            log.error("Failed to build dynamic query", e);
            return List.of();
        }
    }
    
    private List<com.backend.immilog.post.domain.model.post.Post> buildDynamicQueryWithPaging(QueryCriteria criteria, PageRequest pageRequest) {
        try {
            // 페이징이 포함된 동적 쿼리 생성
            log.debug("Building dynamic query with paging - criteria: {}, pageRequest: {}", criteria, pageRequest);
            
            // 현재는 기본 getAllPosts를 사용하고 메모리에서 페이징 처리
            // 실제로는 DB 레벨에서 LIMIT/OFFSET 처리하는 것이 효율적
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
            // COUNT 쿼리 생성
            log.debug("Building count query with criteria: {}", criteria);
            
            // 현재는 전체 활성 게시물 수를 반환
            // 실제로는 criteria에 따른 필터링된 count 반환 필요
            return postJdbcRepository.getAllPosts().size();
        } catch (Exception e) {
            log.error("Failed to build count query", e);
            return 0L;
        }
    }
    
    /**
     * Post 도메인 모델을 PostResult DTO로 변환
     */
    private PostResult convertToPostResult(com.backend.immilog.post.domain.model.post.Post post) {
        return new PostResult(
                post.id(),
                post.postUserInfo().userId(),
                null, // userProfileUrl - 별도 서비스에서 조회 필요
                null, // userNickname - 별도 서비스에서 조회 필요  
                post.commentCount(),
                post.postInfo().viewCount(),
                0L, // likeCount - 별도 서비스에서 계산 필요
                List.of(), // tags - 별도 구현 필요
                List.of(), // attachments - 별도 구현 필요
                List.of(), // likeUsers - 별도 서비스에서 조회 필요
                List.of(), // bookmarkUsers - 별도 서비스에서 조회 필요
                post.isPublic(),
                post.postInfo().countryId(),
                post.postInfo().region(),
                post.category(),
                post.postInfo().status(),
                post.badge(),
                post.createdAt() != null ? post.createdAt().toString() : null,
                post.updatedAt() != null ? post.updatedAt().toString() : null,
                post.postInfo().title(),
                post.postInfo().content(),
                null // keyword - 검색 시에만 사용
        );
    }
}