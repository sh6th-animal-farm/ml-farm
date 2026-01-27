<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/carbon_detail.css">
<script src="${pageContext.request.contextPath}/resources/js/domain/carbon/carbon_detail.js" defer></script>

<input type="hidden" id="targetCpId" value="${cpId}">

<div class="container" style="display: flex; gap: var(--gutter); padding-top: 48px; padding-bottom: 100px;">
    
    <main style="flex: 1;">
        <div class="image-placeholder" style="width: 100%; height: 500px; background: var(--green-0); border-radius: var(--radius-l); display: flex; align-items: center; justify-content: center; font: var(--font-header-02); color: var(--green-800);">
            [전주 스마트팜 바이오차 프로젝트 메인 이미지]
        </div>

        <div class="project-header" style="margin-top: 48px;">
            <div id="tagText" style="color: var(--green-600); font: var(--font-caption-03); font-weight: 700; margin-bottom: 8px;"></div>
            <h1 id="titleText" style="font: var(--font-header-01); color: var(--gray-900); margin-bottom: 12px; font-weight: 700;"></h1>
            <p id="locationText" style="font: var(--font-subtitle-02); color: var(--gray-500);"></p>
        </div>

        <div class="info-section">
            <h2 style="font: var(--font-header-02); color: var(--green-600); border-bottom: 2px solid var(--green-100); padding-bottom: 16px; margin: 0;">주요 정보 및 기대 효과</h2>
            <div class="info-grid">
                <div class="info-card"><label>발급 주체</label><p>마이리틀 스마트팜 협회</p></div>
                <div class="info-card"><label>인증기관</label><p id="valCertificate"></p></div>
                <div class="info-card"><label>상품 유형</label><p id="valType"></p></div>
                <div class="info-card"><label>발급 수량</label><p id="valInitAmount"></p></div>
                <div class="info-card"><label>재고 수량</label><p id="valCpAmount"></p></div>
                <div class="info-card"><label>최소 주문 단위</label><p>1 tCO2e</p></div>
                <div class="info-card full">
                    <label>설명</label>
                    <p id="valDetail" style="line-height: 1.6;"></p>
                </div>
            </div>
        </div>

        <a href="#" class="btn-view-project">프로젝트 보러가기</a>
    </main>

    <aside class="content-side">
        <div class="sticky-side">
            <div class="invest-card">
                <p style="color: var(--gray-500); font: var(--font-caption-01); margin-bottom: 8px; font-weight: 600;">현재 단가 (1 tCO2e)</p>
                
                <div style="display: flex; align-items: center; gap: 8px; margin-bottom: 12px;">
                    <span id="sideOriginalPrice" style="color: var(--gray-400); font: var(--font-caption-01); text-decoration: line-through;"></span>
                    <span id="sideDiscount" style="color: var(--error); font: var(--font-caption-03); font-weight: 700;"></span>
                </div>

                <strong id="sideCurrentPrice" style="font: var(--font-header-01); color: var(--gray-900); display: block; margin-bottom: 8px; font-size: 32px;"></strong>
                <p style="color: var(--gray-400); font: var(--font-caption-01); margin-bottom: 32px;">* 부가세(VAT) 별도 금액</p>

                <button onclick="openOrderModal()" style="width: 100%; background: var(--green-600); color: #fff; font: var(--font-button-01); padding: 20px 0; border-radius: var(--radius-m); cursor: pointer; border: none; font-weight: 700;">
                    주문 신청하기
                </button>
            </div>
        </div>
    </aside>

</div>