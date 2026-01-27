package com.animalfarm.mlf.domain.retry;

import org.springframework.http.HttpMethod;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApiType {

	// [Order - 주문 관련]
	ORDER_DETAIL("주문 상세", "/api/order/%s", HttpMethod.GET),
	ORDER_CANCEL("주문 취소", "/api/order/cancel/%s/%s", HttpMethod.POST),
	ORDER_BALANCE_ALL("전체 잔고 조회", "/api/order/balance", HttpMethod.GET),
	ORDER_BALANCE_TOKEN("토큰별 잔고 조회", "/api/order/balance/%s", HttpMethod.GET),

	// [Market - 마켓 관련]
	MARKET_LIST("마켓 목록", "/api/market", HttpMethod.GET),
	MARKET_SEARCH("마켓 검색", "/api/market/search?content=%s", HttpMethod.GET),
	MARKET_DETAIL("마켓 상세", "/api/market/%s?unit=%s", HttpMethod.GET),
	MARKET_QUOTE("시세 조회", "/api/market/quote/%s", HttpMethod.GET),
	MARKET_TRADE("체결 내역", "/api/market/trade/%s", HttpMethod.GET),
	MARKET_PENDING("미체결 내역", "/api/market/%s/pending", HttpMethod.GET),

	// [My - 계좌/지갑 관련]
	MY_ACCOUNT("내 계좌 정보", "/api/my/account", HttpMethod.GET),
	MY_WALLET_DETAIL("지갑 상세", "/api/my/wallet/%s", HttpMethod.GET),
	MY_TOKEN_LIST("보유 토큰 목록", "/api/my/token/%s?page=%s", HttpMethod.GET),
	MY_TRANSACTION("거래 내역", "/api/my/transaction", HttpMethod.GET),
	MY_WALLET_LIST("지갑 목록", "/api/my/wallet", HttpMethod.GET),
	MY_DEPOSIT("입금", "/api/my/deposit", HttpMethod.POST),
	MY_WITHDRAWAL("출금", "/api/my/withdrawal", HttpMethod.POST),

	// [Project/Carbon - 프로젝트 및 탄소배출권]
	CARBON_DATA("탄소 데이터 조회", "/api/carbon/%s", HttpMethod.GET),
	PROJECT_RESULT("프로젝트 결과", "/api/project/result/%s", HttpMethod.GET),
	PROJECT_CANCEL("프로젝트 취소", "/api/project/cancel/%s", HttpMethod.POST),
	PROJECT_APPLY("프로젝트 신청", "/api/project/application/%s", HttpMethod.POST),
	PROJECT_DIVIDEND("배당금 지급", "/api/project/dividend/%s", HttpMethod.POST),
	PROJECT_CLOSE("프로젝트 종료", "/api/project/close/%s", HttpMethod.POST),

	// [SUB - 청약]
	SUB_CANCEL("청약 취소", "/api/subscription/cancel/%s", HttpMethod.POST);

	private final String description;
	private final String uriTemplate;
	private final HttpMethod method;

	/**
	 * 가변 인자를 받아 최종 URL을 완성하는 유틸리티 메소드
	 */
	public String getFullUri(Object... args) {
		return String.format(this.uriTemplate, args);
	}
}