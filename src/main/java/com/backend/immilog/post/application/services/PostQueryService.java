package com.backend.immilog.post.application.services;

import com.backend.immilog.interaction.application.services.InteractionUserQueryService;
import com.backend.immilog.post.application.mapper.PostResultAssembler;
import com.backend.immilog.post.application.result.PostResult;
import com.backend.immilog.post.domain.model.post.Categories;
import com.backend.immilog.post.domain.model.post.Post;
import com.backend.immilog.post.domain.model.post.PostType;
import com.backend.immilog.post.domain.model.post.SortingMethods;
import com.backend.immilog.post.domain.repositories.PostDomainRepository;
import com.backend.immilog.post.exception.PostErrorCode;
import com.backend.immilog.post.exception.PostException;
import com.backend.immilog.shared.aop.annotation.PerformanceMonitor;
import com.backend.immilog.shared.domain.model.Resource;
import com.backend.immilog.shared.enums.Country;
import com.backend.immilog.shared.infrastructure.DataRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
public class PostQueryService {
    private final ObjectMapper objectMapper;
    private final PostDomainRepository postDomainRepository;
    private final DataRepository redisDataRepository;
    private final InteractionUserQueryService interactionUserQueryService;
    private final PostResourceQueryService postResourceQueryService;
    private final PostResultAssembler postResultAssembler;

    public PostQueryService(
            ObjectMapper objectMapper,
            PostDomainRepository postDomainRepository,
            DataRepository redisDataRepository,
            InteractionUserQueryService interactionUserQueryService,
            PostResourceQueryService postResourceQueryService,
            PostResultAssembler postResultAssembler
    ) {
        this.objectMapper = objectMapper;
        this.postDomainRepository = postDomainRepository;
        this.redisDataRepository = redisDataRepository;
        this.interactionUserQueryService = interactionUserQueryService;
        this.postResourceQueryService = postResourceQueryService;
        this.postResultAssembler = postResultAssembler;
    }

    @Transactional(readOnly = true)
    public Post getPostById(String postId) {
        return postDomainRepository.findById(postId)
                .orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_FOUND));
    }

    @PerformanceMonitor
    @Transactional(readOnly = true)
    public Page<PostResult> getPosts(
            Country country,
            SortingMethods sortingMethod,
            String isPublic,
            Categories category,
            Pageable pageable
    ) {
        var posts = postDomainRepository.findPosts(
                country,
                sortingMethod,
                isPublic,
                category,
                pageable
        );
        var postIdList = posts.stream().map(Post::id).toList();
        var postResults = posts.map(this::convertToPostResult);
        return this.assemblePostResult(postIdList, postResults);
    }

    @Transactional(readOnly = true)
    public Page<PostResult> getPostsByKeyword(
            String keyword,
            Pageable pageable
    ) {
        var posts = postDomainRepository.findPostsByKeyword(keyword, pageable);
        var postIdList = posts.stream().map(Post::id).toList();
        var postResults = posts.map(this::convertToPostResult);
        var updatedPostResultsPage = new PageImpl<>(
                postResults.getContent().stream()
                        .map(post -> postResultAssembler.assembleKeywords(post, keyword))
                        .toList(),
                pageable,
                postResults.getTotalElements()
        );
        return this.assemblePostResult(postIdList, updatedPostResultsPage);
    }

    @Transactional(readOnly = true)
    public PostResult getPostDetail(String postId) {
        var post = postDomainRepository.findById(postId)
                .orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_FOUND));
        var posts = new PageImpl<>(List.of(post));
        var postResult = posts.map(this::convertToPostResult);
        return this.assemblePostResult(List.of(postId), postResult).getContent().getFirst();
    }

    @Transactional(readOnly = true)
    public Page<PostResult> getPostsByUserId(
            String userId,
            Pageable pageable
    ) {
        var posts = postDomainRepository.findPostsByUserId(userId, pageable);
        var postResults = posts.map(this::convertToPostResult);
        return this.assemblePostResult(
                posts.stream().map(Post::id).toList(),
                postResults
        );
    }

    public List<PostResult> getPostsFromRedis(String key) {
        var jsonData = redisDataRepository.findByKey(key);
        if (jsonData == null) {
            log.info("No data found with key {}", key);
            return List.of();
        }
        try {
            return objectMapper.readValue(jsonData, new TypeReference<List<PostResult>>() {});
        } catch (JsonProcessingException e) {
            log.error("Failed to parse json data", e);
        } catch (Exception e) {
            log.error("Failed to get popular posts with key {}", key, e);
        }
        return List.of();
    }

    public List<PostResult> getPostsByPostIdList(List<String> postIdList) {
        var postResults = postDomainRepository.findPostsByIdList(postIdList)
                .stream()
                .map(this::convertToPostResult)
                .toList();
        return this.assemblePostResult(postIdList, new PageImpl<>(postResults)).toList();
    }

    private Page<PostResult> assemblePostResult(
            List<String> resultIdList,
            Page<PostResult> postResults
    ) {
        var orderMap = IntStream.range(0, resultIdList.size())
                .boxed()
                .collect(Collectors.toMap(resultIdList::get, i -> i));

        var interactionUsers = interactionUserQueryService.getInteractionUsersByPostIdList(resultIdList, PostType.POST);
        var postResources = postResourceQueryService.getResourcesByPostIdList(resultIdList, PostType.POST);

        return postResults.map(postResult -> {
            var resources = postResources.stream()
                    .filter(postResource -> postResource.postId().equals(postResult.id()))
                    .map(pr -> new Resource(
                            pr.id(),
                            pr.postId(),
                            pr.postType(),
                            com.backend.immilog.shared.domain.model.ResourceType.valueOf(pr.resourceType().name()),
                            pr.content()
                    ))
                    .sorted(Comparator.comparingInt(pr -> orderMap.getOrDefault(pr.entityId(), Integer.MAX_VALUE)))
                    .toList();

            var interactionUserList = interactionUsers.stream()
                    .filter(interactionUser -> interactionUser.postId().equals(postResult.id()))
                    .sorted(Comparator.comparingInt(iu -> orderMap.getOrDefault(iu.postId(), Integer.MAX_VALUE)))
                    .toList();

            var postResultWithNewInteractionUsers = postResultAssembler.assembleInteractionUsers(
                    postResult,
                    interactionUserList
            );
            var postResultWithNewResources = postResultAssembler.assembleResources(
                    postResultWithNewInteractionUsers,
                    resources
            );
            return postResultAssembler.assembleLikeCount(
                    postResultWithNewResources,
                    interactionUserList.size()
            );
        });
    }

    private PostResult convertToPostResult(Post post) {
        return new PostResult(
                post.id(),
                post.userId(),
                post.profileImage(),
                post.nickname(),
                null,
                post.commentCount(),
                post.viewCount(),
                post.likeCount(),
                null,
                null,
                null,
                null,
                post.isPublic(),
                post.countryName(),
                post.region(),
                post.category(),
                post.status(),
                post.createdAt().toString(),
                post.updatedAt().toString(),
                post.title(),
                post.content(),
                null
        );
    }
}
