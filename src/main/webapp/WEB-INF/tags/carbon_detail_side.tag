<%@ tag language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="invest-card" style="width: 416px; min-height: 261px; padding: 32px; background: #fff; border: 1px solid var(--gray-0); border-radius: var(--radius-m); box-shadow: var(--shadow); position: relative; box-sizing: border-box;">
    <p style="color: var(--gray-500); font: var(--font-caption-02); margin-bottom: 8px;">현재 단가 (1 tCO2e)</p>
    
    <div style="display: flex; align-items: center; gap: 8px; margin-bottom: 12px;">
        <span id="sideOriginalPrice" style="color: var(--gray-400); font: var(--font-caption-01); text-decoration: line-through;"></span>
        <span id="sideDiscount" style="color: var(--error); font: var(--font-caption-03);"></span>
    </div>

    <strong id="sideCurrentPrice" style="font: var(--font-header-01); color: var(--gray-900); display: block; margin-bottom: 8px;"></strong>
    <p style="color: var(--gray-400); font: var(--font-caption-01); margin-bottom: 32px;">* 부가세(VAT) 별도 금액</p>

    <%-- [수정] 버튼 폰트 및 패딩 최적화 --%>
    <button onclick="openOrderModal()" 
            style="width: 100%; 
                background: var(--green-600); 
                color: #fff; 
                font: var(--font-button-01); 
                font-weight: 700; <%-- 굵게 강조 --%>
                padding: 24px 0; <%-- 디자인 가이드와 동일하게 높이 상향 --%>
                border-radius: var(--radius-s); 
                cursor: pointer; 
                border: none;">
        주문 신청하기
    </button>
</div>