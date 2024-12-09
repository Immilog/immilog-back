package com.backend.immilog.post.infrastructure.repositories;

import com.backend.immilog.post.application.result.PostResult;
import com.backend.immilog.post.domain.model.enums.Categories;
import com.backend.immilog.post.domain.model.enums.Countries;
import com.backend.immilog.post.domain.model.enums.SortingMethods;
import com.backend.immilog.post.domain.model.Post;
import com.backend.immilog.post.domain.repositories.PostRepository;
import com.backend.immilog.post.infrastructure.jpa.PostEntity;
import com.backend.immilog.post.infrastructure.jpa.QInteractionUserEntity;
import com.backend.immilog.post.infrastructure.jpa.QPostEntity;
import com.backend.immilog.post.infrastructure.jpa.QPostResourceEntity;
import com.backend.immilog.post.infrastructure.jpa.PostJpaRepository;
import com.backend.immilog.post.infrastructure.result.PostEntityResult;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.backend.immilog.post.domain.model.enums.PostStatus.DELETED;
import static com.backend.immilog.post.domain.model.enums.PostType.POST;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.types.Projections.list;

@RequiredArgsConstructor
@Repository
public class PostRepositoryImpl implements PostRepository {
    private final JPAQueryFactory queryFactory;
    private final PostJpaRepository postJpaRepository;

    @Override
    public Page<PostResult> getPosts(
            Countries country,
            SortingMethods sortingMethod,
            String isPublic,
            Categories category,
            Pageable pageable
    ) {
        QPostEntity post = QPostEntity.postEntity;
        QPostResourceEntity resource = QPostResourceEntity.postResourceEntity;
        QInteractionUserEntity interUser = QInteractionUserEntity.interactionUserEntity;

        Predicate predicate = generateCriteria(country, isPublic, category, post);
        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(sortingMethod, post);

        List<PostResult> postResults = queryFactory
                .select(post, list(interUser), list(resource))
                .from(post)
                .leftJoin(resource)
                .on(resource.postSeq.eq(post.seq).and(resource.postType.eq(POST)))
                .leftJoin(interUser)
                .on(interUser.postSeq.eq(post.seq).and(interUser.postType.eq(POST)))
                .where(predicate)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .transform(
                        groupBy(post.seq).list(
                                Projections.constructor(
                                        PostEntityResult.class,
                                        post,
                                        list(interUser),
                                        list(resource)
                                )
                        )
                ).stream()
                .map(PostEntityResult::toPostResult)
                .toList();

        long total = getSize(post, predicate);
        return new PageImpl<>(postResults, pageable, total);
    }

    @Override
    public Optional<PostResult> getPost(
            Long postSeq
    ) {
        QPostEntity post = QPostEntity.postEntity;
        QPostResourceEntity resource = QPostResourceEntity.postResourceEntity;
        QInteractionUserEntity interUser = QInteractionUserEntity.interactionUserEntity;
        BooleanExpression criteria = post.seq.eq(postSeq);

        return queryFactory
                .select(post, list(interUser), list(resource))
                .from(post)
                .leftJoin(resource)
                .on(resource.postSeq.eq(post.seq).and(resource.postType.eq(POST)))
                .leftJoin(interUser)
                .on(interUser.postSeq.eq(post.seq).and(interUser.postType.eq(POST)))
                .where(criteria)
                .transform(
                        groupBy(post.seq)
                                .list(
                                        Projections.constructor(
                                                PostEntityResult.class,
                                                post,
                                                list(interUser),
                                                list(resource)
                                        )
                                )
                )
                .stream()
                .map(PostEntityResult::toPostResult)
                .findFirst();
    }

    @Override
    public Page<PostResult> getPostsByKeyword(
            String keyword,
            Pageable pageable
    ) {
        QPostEntity post = QPostEntity.postEntity;
        QPostResourceEntity resource = QPostResourceEntity.postResourceEntity;
        QInteractionUserEntity interUser = QInteractionUserEntity.interactionUserEntity;
        BooleanExpression predicate = generateKeywordCriteria(keyword, post, resource);

        List<PostResult> postResults = queryFactory
                .select(post, list(interUser), list(resource))
                .from(post)
                .leftJoin(resource)
                .on(resource.postSeq.eq(post.seq).and(resource.postType.eq(POST)))
                .leftJoin(interUser)
                .on(interUser.postSeq.eq(post.seq).and(interUser.postType.eq(POST)))
                .where(predicate)
                .transform(
                        groupBy(post.seq)
                                .list(
                                        Projections.constructor(
                                                PostEntityResult.class,
                                                post,
                                                list(interUser),
                                                list(resource)
                                        )
                                )
                )
                .stream()
                .map(PostEntityResult::toPostResult)
                .toList();

        long total = getSize(post, resource, interUser, predicate);
        return new PageImpl<>(postResults, pageable, total);
    }

    @Override
    public Page<PostResult> getPostsByUserSeq(
            Long userSeq,
            Pageable pageable
    ) {
        QPostEntity post = QPostEntity.postEntity;
        QPostResourceEntity resource = QPostResourceEntity.postResourceEntity;
        QInteractionUserEntity interUser = QInteractionUserEntity.interactionUserEntity;
        BooleanExpression predicate = generateUserPostCriteria(userSeq, post);

        List<PostResult> postResults = queryFactory
                .select(post, list(interUser), list(resource))
                .from(post)
                .leftJoin(resource)
                .on(resource.postSeq.eq(post.seq).and(resource.postType.eq(POST)))
                .leftJoin(interUser)
                .on(interUser.postSeq.eq(post.seq).and(interUser.postType.eq(POST)))
                .where(predicate)
                .transform(
                        groupBy(post.seq)
                                .list(
                                        Projections.constructor(
                                                PostEntityResult.class,
                                                post,
                                                list(interUser),
                                                list(resource)
                                        )
                                )
                )
                .stream()
                .map(PostEntityResult::toPostResult)
                .toList();
        long total = getSize(post, resource, interUser, predicate);
        return new PageImpl<>(postResults, pageable, total);
    }

    @Override
    public Optional<Post> getById(
            Long postSeq
    ) {
        return postJpaRepository.findById(postSeq).map(PostEntity::toDomain);
    }

    @Override
    public Post save(
            Post postEntity
    ) {
        return postJpaRepository.save(PostEntity.from(postEntity)).toDomain();
    }

    private BooleanExpression generateUserPostCriteria(
            Long userSeq,
            QPostEntity post
    ) {
        return post.postUserData.userSeq.eq(userSeq)
                .and(post.postMetaData.status.ne(DELETED));
    }

    private static BooleanExpression generateKeywordCriteria(
            String keyword,
            QPostEntity post,
            QPostResourceEntity resource
    ) {
        BooleanExpression postCondition = post.postMetaData.content.contains(keyword)
                .or(post.postMetaData.title.contains(keyword));

        BooleanExpression resourceCondition = resource.content.contains(keyword);

        return postCondition.or(resourceCondition);
    }

    private static Predicate generateCriteria(
            Countries country,
            String isPublic,
            Categories category,
            QPostEntity post
    ) {
        BooleanBuilder predicateBuilder = new BooleanBuilder();
        if (!category.equals(Categories.ALL)) {
            predicateBuilder.and(post.category.eq(category));
        }
        if (!country.equals(Countries.ALL)) {
            predicateBuilder.and(post.postMetaData.country.eq(country));
        }
        predicateBuilder
                .and(post.isPublic.eq(isPublic))
                .and(post.postMetaData.status.ne(DELETED));
        return predicateBuilder.getValue();
    }

    private OrderSpecifier<?> getOrderSpecifier(
            SortingMethods sortingMethod,
            QPostEntity post
    ) {
        return switch (sortingMethod) {
            case CREATED_DATE -> post.createdAt.desc();
            case VIEW_COUNT -> post.postMetaData.viewCount.desc();
            case LIKE_COUNT -> post.postMetaData.likeCount.desc();
            default -> post.createdAt.desc();
        };
    }

    private int getSize(
            QPostEntity post,
            Predicate predicate
    ) {
        return queryFactory.selectFrom(post)
                .where(predicate)
                .fetch()
                .size();
    }

    private int getSize(
            QPostEntity post,
            QPostResourceEntity postResource,
            QInteractionUserEntity interactionUser,
            Predicate predicate
    ) {
        return queryFactory.selectFrom(post)
                .leftJoin(postResource)
                .on(postResource.postSeq.eq(post.seq).and(postResource.postType.eq(POST)))
                .leftJoin(interactionUser)
                .on(interactionUser.postSeq.eq(post.seq).and(interactionUser.postType.eq(POST)))
                .where(predicate)
                .fetch()
                .size();
    }

}
