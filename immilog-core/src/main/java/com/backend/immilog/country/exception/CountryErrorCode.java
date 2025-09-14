package com.backend.immilog.country.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CountryErrorCode {
    INVALID_COUNTRY_ID("C001", "유효하지 않은 국가 ID입니다"),
    COUNTRY_NOT_FOUND("C002", "국가를 찾을 수 없습니다"),
    COUNTRY_ALREADY_EXISTS("C003", "이미 존재하는 국가입니다"),
    INVALID_COUNTRY_NAME("C004", "유효하지 않은 국가명입니다"),
    INVALID_CONTINENT("C005", "유효하지 않은 대륙명입니다"),
    COUNTRY_DEACTIVATION_NOT_ALLOWED("C006", "국가 비활성화가 허용되지 않습니다"),
    COUNTRY_ACTIVATION_NOT_ALLOWED("C007", "국가 활성화가 허용되지 않습니다");

    private final String code;
    private final String message;
}