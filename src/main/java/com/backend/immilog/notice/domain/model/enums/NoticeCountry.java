package com.backend.immilog.notice.domain.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NoticeCountry {
    ALL("ALL", "전체"),
    MALAYSIA("MY", "말레이시아"),
    SINGAPORE("SG", "싱가포르"),
    INDONESIA("ID", "인도네시아"),
    VIETNAM("VN", "베트남"),
    PHILIPPINES("PH", "필리핀"),
    THAILAND("TH", "태국"),
    MYANMAR("MM", "미얀마"),
    CAMBODIA("KH", "캄보디아"),
    LAOS("LA", "라오스"),
    BRUNEI("BN", "브루나이"),
    EAST_TIMOR("TL", "동티모르"),
    CHINA("CN", "중국"),
    JAPAN("JP", "일본"),
    SOUTH_KOREA("KR", "대한민국"),
    AUSTRALIA("AU", "오스트레일리아"),
    NEW_ZEALAND("NZ", "뉴질랜드"),
    GUAM("GU", "괌"),
    SAI_PAN("MP", "사이판"),
    ECT("ETC", "기타");

    private final String countryCode;
    private final String countryKoreanName;
}
