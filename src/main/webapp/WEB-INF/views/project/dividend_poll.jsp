<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/project_poll.css">
<script type="module" src="${pageContext.request.contextPath}/resources/js/domain/project/dividend_poll.js"></script>

<div class="poll-container">
    <h2 class="poll-title">배당 수령 방식 선택</h2>
    
    <div class="amount-box">
        <p class="amount-label">예상 배당금 (세후)</p>
        <p class="amount-value">
            <fmt:formatNumber value="${dividend.amountAftTax}" type="number"/>원
        </p>
    </div>

    <form id="pollForm" >
        <input type="hidden" name="dividendId" value="${dividend.dividendId}">
        <input type="hidden" id="selectedType" name="dividendType" value="CASH">

        <label class="choice-label">원하시는 방식을 선택해주세요</label>
        
        <div class="card-choice active" onclick="selectType('CASH', this)">
            <div class="text-wrapper">
                <h6>현금으로 받기</h6>
                <small>등록된 계좌로 입금됩니다.</small>
            </div>
            <span class="won-symbol">₩</span>
        </div>

        <div class="card-choice" onclick="selectType('CROP', this)">
            <div class="text-wrapper">
                <h6>작물로 받기</h6>
                <small>집 앞으로 신선하게 배송됩니다.</small>
            </div>
            <svg class="bg-icon" viewBox="0 0 24 24">
		        <path d="M17,8C8,10 5.9,16.17 3.82,21.34L5.71,22L6.66,19.7C7.14,19.87 7.64,20 8,20C19,20 22,3 22,3C21,5 14,5.25 9,6.25C4,7.25 2,11.5 2,13.5C2,15.5 3.75,17.25 3.75,17.25C7,8 17,8 17,8Z" />
		    </svg>
        </div>

        <div class="poll-alert">
            마감기한: <strong>${pollEndDisplay}</strong><br>
            기한 내 미선택 시 현금으로 자동 확정됩니다.
        </div>

        <button type="submit" class="submit-button">선택 완료</button>
    </form>
</div>

<script src="//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>

<div id="addressModal" class="poll-modal">
    <div class="modal-content">
        <span class="close-modal" onclick="closeAddressModal()">&times;</span>
        
        <div class="modal-header">
            <h5>배송지 확인</h5>
        </div>
        
        <div class="modal-body">
            <p>선택하신 작물을 아래 주소로 보내드릴까요?</p>
            
            <div class="address-section">
                <div class="current-address-box">
                    <span id="displayAddress">
                        ${not empty curAddress ? curAddress : '등록된 주소가 없습니다.'}
                    </span>
                    
                    <button type="button" class="btn-edit-address" onclick="toggleAddressEdit()">
                        수정
                    </button>
                </div>
                

                <div id="addressInputArea" style="display: none; margin-top: 16px;">
                    <input type="text" id="detailAddress" class="address-input" 
                        placeholder="상세 주소(동·호수 등)를 입력해주세요" oninput="combineAddress()">
                    <input type="hidden" id="baseAddress">
                </div>
            </div>
        </div>

        <div class="modal-footer">
            <button type="button" class="btn-cancel" onclick="closeAddressModal()">취소</button>
            <button type="button" class="btn-confirm" onclick="confirmCropSelection()">배송 확정</button>
        </div>
    </div>
</div>