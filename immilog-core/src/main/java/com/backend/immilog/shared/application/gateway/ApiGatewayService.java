package com.backend.immilog.shared.application.gateway;

import com.backend.immilog.shared.application.query.PagedResult;
import com.backend.immilog.shared.domain.service.InteractionDataProvider;
import com.backend.immilog.shared.domain.service.UserDataProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Service
public class ApiGatewayService {

    private final UserDataProvider userDataProvider;
    private final InteractionDataProvider interactionDataProvider;
    private final Executor taskExecutor;

    public ApiGatewayService(
            UserDataProvider userDataProvider,
            InteractionDataProvider interactionDataProvider,
            @Qualifier("applicationTaskExecutor") Executor taskExecutor
    ) {
        this.userDataProvider = userDataProvider;
        this.interactionDataProvider = interactionDataProvider;
        this.taskExecutor = taskExecutor;
    }

    public <T> CompletableFuture<EnrichedResult<T>> enrichDataAsync(
            T baseData,
            DataEnrichmentRequest request
    ) {

        log.debug("Starting data enrichment for: {}", baseData.getClass().getSimpleName());

        return CompletableFuture.supplyAsync(() -> {
            try {
                var builder = EnrichedResult.<T>builder()
                        .baseData(baseData);

                if (request.includeUserData() && request.getUserId() != null) {
                    var userData = userDataProvider.getUserData(request.getUserId());
                    builder.userData(userData);
                }

                if (request.includeInteractionData() && request.getContentId() != null) {
                    var interactionData = interactionDataProvider.getInteractionData(
                            request.getContentId(),
                            request.getContentType()
                    );
                    builder.interactionData(interactionData);
                }

                if (request.includeUserInteraction() &&
                        request.getUserId() != null &&
                        request.getContentId() != null) {
                    var userInteraction = interactionDataProvider.getUserInteractionData(
                            request.getUserId(),
                            request.getContentId(),
                            request.getContentType()
                    );
                    builder.userInteractionData(userInteraction);
                }

                var result = builder.build();
                log.debug("Data enrichment completed for: {}", baseData.getClass().getSimpleName());
                return result;

            } catch (Exception e) {
                log.error("Error during data enrichment", e);
                return EnrichedResult.<T>builder()
                        .baseData(baseData)
                        .error(e.getMessage())
                        .build();
            }
        }, taskExecutor);
    }

    public <T> CompletableFuture<List<EnrichedResult<T>>> enrichDataListAsync(
            List<T> baseDataList,
            DataEnrichmentRequest request
    ) {

        log.debug("Starting batch data enrichment for {} items", baseDataList.size());

        var futures = baseDataList.stream()
                .map(item -> enrichDataAsync(item, request))
                .toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .toList());
    }

    public <T> CompletableFuture<PagedResult<EnrichedResult<T>>> enrichPagedDataAsync(
            PagedResult<T> pagedData,
            DataEnrichmentRequest request
    ) {

        log.debug("Starting paged data enrichment for page: {}", pagedData.getPageNumber());

        return enrichDataListAsync(pagedData.getContent(), request)
                .thenApply(enrichedList -> pagedData.map(item -> {
                    return enrichedList.stream()
                            .filter(enriched -> enriched.getBaseData().equals(item))
                            .findFirst()
                            .orElse(EnrichedResult.<T>builder()
                                    .baseData(item)
                                    .error("Enrichment failed")
                                    .build());
                }));
    }

    /**
     * 캐시를 활용한 최적화된 데이터 조합
     */
    public <T> CompletableFuture<EnrichedResult<T>> enrichDataWithCache(
            T baseData,
            DataEnrichmentRequest request,
            String cacheKey
    ) {

        // TODO: Redis 캐시 구현
        // 현재는 직접 조합으로 처리
        return enrichDataAsync(baseData, request);
    }

    /**
     * 에러 복구가 포함된 데이터 조합
     */
    public <T> EnrichedResult<T> enrichDataWithFallback(
            T baseData,
            DataEnrichmentRequest request,
            long timeoutMillis
    ) {

        try {
            return enrichDataAsync(baseData, request)
                    .get(timeoutMillis, java.util.concurrent.TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.warn("Data enrichment timeout or error, returning base data only", e);
            return EnrichedResult.<T>builder()
                    .baseData(baseData)
                    .error("Enrichment failed: " + e.getMessage())
                    .fallbackUsed(true)
                    .build();
        }
    }
}