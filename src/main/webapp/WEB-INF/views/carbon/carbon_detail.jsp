<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<link rel="stylesheet" href="/resources/css/carbon_detail.css">

<div class="content-wrapper">
    <div class="container detail-main-container">
        <div class="row">
            <div class="col-8">
                <div class="project-banner" id="projectBanner">
                    <p class="banner-placeholder">[전주 스마트팜 바이오차 프로젝트 메인 이미지]</p>
                </div>

                <div class="project-header-info">
                    <div class="category-tag">
                        <span id="cpType">감축형 프로젝트</span> | <span id="vintageYear">2025 빈티지</span>
                    </div>
                    <h1 id="projectName" class="project-title">로딩 중...</h1>
                    <p id="projectLoc" class="project-location">위치 정보 로딩 중...</p>
                </div>

                <div class="info-section">
                    <h2 class="section-title">주요 정보 및 기대 효과</h2>
                    <div class="info-grid">
                        <div class="info-card">
                            <span class="label">발급 주체</span>
                            <span class="value" id="productCertificate">-</span>
                        </div>
                        <div class="info-card">
                            <span class="label">인증 표준</span>
                            <span class="value">VER (Voluntary Emission Reduction)</span>
                        </div>
                        <div class="info-card">
                            <span class="label">프로젝트 유형</span>
                            <span class="value" id="cpDetail">-</span>
                        </div>
                        <div class="info-card">
                            <span class="label">발급 수량</span>
                            <span class="value" id="initAmount">0 tCO2e</span>
                        </div>
                        <div class="info-card">
                            <span class="label">재고 수량</span>
                            <span class="value" id="cpAmount">0 tCO2e</span>
                        </div>
                        <div class="info-card">
                            <span class="label">최소 주문 단위</span>
                            <span class="value">0.0001 tCO2e</span>
                        </div>
                    </div>
                    <div class="benefit-card">
                        <span class="label">주요 기대 효과</span>
                        <p class="value-text">토양 비옥도 향상 및 비료 사용량 절감, 지역 농업 용수 수질 개선, 농가 부가 소득 증대 및 탄소 중립 실현 기여</p>
                    </div>
                </div>

                <div class="bottom-action">
                    <button class="btn-outline" onclick="location.href='/carbon/list'">프로젝트 보러가기</button>
                </div>
            </div>

            <div class="col-4">
                <div class="price-sidebar">
                    <p class="price-label">현재 단가 (1 tCO2e)</p>
                    <div class="price-info">
                        <span id="originalPrice" class="old-price">0 KRW</span>
                        <span id="discountRate" class="discount-percent">0% 할인</span>
                    </div>
                    <div class="current-price">
                        <span id="finalPrice">0</span><span class="currency">원</span>
                    </div>
                    <p class="vat-notice">* 부가세(VAT) 별도 금액</p>
                    
                    <button class="btn-primary-large" id="btnOrderOpen">주문 신청하기</button>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    const CURRENT_CP_ID = "${cpId}"; // Controller에서 넘겨준 ID
</script>
<script src="/resources/js/carbon/carbon_detail.js"></script>