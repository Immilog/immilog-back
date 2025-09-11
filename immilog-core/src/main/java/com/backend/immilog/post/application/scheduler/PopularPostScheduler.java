package com.backend.immilog.post.application.scheduler;

import com.backend.immilog.post.application.services.PopularPostAggregationService;
import com.backend.immilog.post.application.services.PostBadgeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class PopularPostScheduler {
    private final PopularPostAggregationService popularPostAggregationService;
    private final PostBadgeService postBadgeService;

    @Scheduled(cron = "59 * * * * *")
    public void aggregatePopularPosts() {
        log.info("[POPULAR POST AGGREGATION] Started aggregating popular posts...");
        popularPostAggregationService.aggregatePopularPosts();
        log.info("[POPULAR POST AGGREGATION] Finished aggregating popular posts.");
    }
    
    @Scheduled(cron = "0 0 1 * * MON") // 매주 월요일 01:00 실행 (새벽 시간대)
    public void updateWeeklyBestBadges() {
        log.info("[WEEKLY BEST BADGE UPDATE] Started updating weekly best badges...");
        postBadgeService.updateWeeklyBestBadges();
        log.info("[WEEKLY BEST BADGE UPDATE] Finished updating weekly best badges.");
    }
}