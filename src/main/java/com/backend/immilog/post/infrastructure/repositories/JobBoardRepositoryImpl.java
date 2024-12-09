package com.backend.immilog.post.infrastructure.repositories;

import com.backend.immilog.post.application.result.JobBoardResult;
import com.backend.immilog.post.domain.model.JobBoard;
import com.backend.immilog.post.domain.model.enums.Countries;
import com.backend.immilog.post.domain.model.enums.Experience;
import com.backend.immilog.post.domain.model.enums.Industry;
import com.backend.immilog.post.domain.model.enums.SortingMethods;
import com.backend.immilog.post.domain.repositories.JobBoardRepository;
import com.backend.immilog.post.infrastructure.jpa.JobBoardEntity;
import com.backend.immilog.post.infrastructure.jpa.QInteractionUserEntity;
import com.backend.immilog.post.infrastructure.jpa.QJobBoardEntity;
import com.backend.immilog.post.infrastructure.jpa.QPostResourceEntity;
import com.backend.immilog.post.infrastructure.jpa.JobBoardJpaRepository;
import com.backend.immilog.post.infrastructure.result.JobBoardEntityResult;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.backend.immilog.post.domain.model.enums.PostType.JOB_BOARD;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.types.Projections.list;

@Repository
@RequiredArgsConstructor
public class JobBoardRepositoryImpl implements JobBoardRepository {
    private final JobBoardJpaRepository jobBoardJpaRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public void saveEntity(
            JobBoard jobBoard
    ) {
        jobBoardJpaRepository.save(JobBoardEntity.from(jobBoard));
    }

    @Override
    public Page<JobBoardResult> getJobBoards(
            Countries country,
            String sortingMethod,
            Industry industry,
            Experience experience,
            Pageable pageable
    ) {
        QJobBoardEntity jobBoard = QJobBoardEntity.jobBoardEntity;
        QPostResourceEntity resource = QPostResourceEntity.postResourceEntity;
        QInteractionUserEntity interUser = QInteractionUserEntity.interactionUserEntity;

        BooleanBuilder predicateBuilder = new BooleanBuilder();
        com.backend.immilog.user.domain.enums.Industry industryEnum = convertToUserIndustry(industry);

        if (isNotNullAndSame(industry, Industry.ALL)) {
            predicateBuilder.and(jobBoard.companyMetaData.industry.eq(industryEnum));
        }
        if (isNotNullAndSame(country, Countries.ALL)) {
            predicateBuilder.and(jobBoard.postMetaData.country.eq(country));
        }
        if (isNotNullAndSame(experience, Experience.ALL)) {
            predicateBuilder.and(jobBoard.companyMetaData.experience.eq(experience));
        }

        // 정렬 조건 설정
        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(sortingMethod, jobBoard);

        long total = getLength(predicateBuilder);
        List<JobBoardResult> jobBoards = jpaQueryFactory
                .select(jobBoard, list(interUser), list(resource))
                .from(jobBoard)
                .leftJoin(resource)
                .on(resource.postSeq.eq(jobBoard.seq).and(resource.postType.eq(JOB_BOARD)))
                .leftJoin(interUser)
                .on(interUser.postSeq.eq(jobBoard.seq).and(interUser.postType.eq(JOB_BOARD)))
                .where(predicateBuilder)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .transform(
                        groupBy(jobBoard.seq).list(
                                Projections.constructor(
                                        JobBoardEntityResult.class,
                                        jobBoard.seq,
                                        jobBoard.postMetaData.title,
                                        jobBoard.postMetaData.content,
                                        jobBoard.postMetaData.viewCount,
                                        jobBoard.postMetaData.likeCount,
                                        list(resource),
                                        list(interUser),
                                        jobBoard.postMetaData.country,
                                        jobBoard.postMetaData.region,
                                        jobBoard.companyMetaData.companySeq,
                                        jobBoard.companyMetaData.industry,
                                        jobBoard.companyMetaData.deadline,
                                        jobBoard.companyMetaData.experience,
                                        jobBoard.companyMetaData.salary,
                                        jobBoard.companyMetaData.company,
                                        jobBoard.companyMetaData.companyEmail,
                                        jobBoard.companyMetaData.companyPhone,
                                        jobBoard.companyMetaData.companyAddress,
                                        jobBoard.companyMetaData.companyHomepage,
                                        jobBoard.companyMetaData.companyLogo,
                                        jobBoard.postMetaData.status,
                                        jobBoard.createdAt
                                )
                        )
                )
                .stream()
                .map(JobBoardEntityResult::toResult)
                .toList();

        return new PageImpl<>(jobBoards, pageable, total);
    }

    @Override
    public Optional<JobBoardResult> getJobBoardBySeq(
            Long jobBoardSeq
    ) {
        QJobBoardEntity jobBoard = QJobBoardEntity.jobBoardEntity;
        QPostResourceEntity resource = QPostResourceEntity.postResourceEntity;
        QInteractionUserEntity interUser = QInteractionUserEntity.interactionUserEntity;

        return jpaQueryFactory
                .select(jobBoard, list(interUser), list(resource))
                .from(jobBoard)
                .leftJoin(resource)
                .on(resource.postSeq.eq(jobBoard.seq).and(resource.postType.eq(JOB_BOARD)))
                .leftJoin(interUser)
                .on(interUser.postSeq.eq(jobBoard.seq).and(interUser.postType.eq(JOB_BOARD)))
                .where(jobBoard.seq.eq(jobBoardSeq))
                .transform(
                        groupBy(jobBoard.seq).list(
                                Projections.constructor(
                                        JobBoardEntityResult.class,
                                        jobBoard.seq,
                                        jobBoard.postMetaData.title,
                                        jobBoard.postMetaData.content,
                                        jobBoard.postMetaData.viewCount,
                                        jobBoard.postMetaData.likeCount,
                                        list(resource),
                                        list(interUser),
                                        jobBoard.postMetaData.country,
                                        jobBoard.postMetaData.region,
                                        jobBoard.companyMetaData.companySeq,
                                        jobBoard.companyMetaData.industry,
                                        jobBoard.companyMetaData.deadline,
                                        jobBoard.companyMetaData.experience,
                                        jobBoard.companyMetaData.salary,
                                        jobBoard.companyMetaData.company,
                                        jobBoard.companyMetaData.companyEmail,
                                        jobBoard.companyMetaData.companyPhone,
                                        jobBoard.companyMetaData.companyAddress,
                                        jobBoard.companyMetaData.companyHomepage,
                                        jobBoard.companyMetaData.companyLogo,
                                        jobBoard.postMetaData.status,
                                        jobBoard.createdAt
                                )
                        )
                )
                .stream()
                .findFirst()
                .map(JobBoardEntityResult::toResult);
    }

    private Long getLength(
            Predicate predicate
    ) {
        QJobBoardEntity jobBoard = QJobBoardEntity.jobBoardEntity;
        return (long) jpaQueryFactory.selectFrom(jobBoard)
                .where(predicate)
                .fetch()
                .size();
    }

    private OrderSpecifier<?> getOrderSpecifier(
            String sortingMethod,
            QJobBoardEntity jobBoard
    ) {
        return switch (SortingMethods.valueOf(sortingMethod)) {
            case VIEW_COUNT -> jobBoard.postMetaData.viewCount.desc();
            case LIKE_COUNT -> jobBoard.postMetaData.likeCount.desc();
            default -> jobBoard.createdAt.desc();
        };
    }


    private static com.backend.immilog.user.domain.enums.Industry convertToUserIndustry(Industry industry) {
        return com.backend.immilog.user.domain.enums.Industry.valueOf(industry.name());
    }

    private <T, E extends Enum<E>> boolean isNotNullAndSame(
            E value,
            T compare
    ) {
        return Objects.equals(value, compare);
    }

}