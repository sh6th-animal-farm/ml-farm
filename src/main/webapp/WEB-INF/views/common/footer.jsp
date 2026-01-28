<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<footer class="main-footer">
    <div class="container">
        <div class="row">
            <div class="col-8">
                <nav class="footer-top-menu">
                    <a href="#">회사 소개</a>
                    <a href="#">서비스 소개</a>
                    <a href="${pageContext.request.contextPath}/policy">서비스 약관</a>
                    <a href="#">Q&A</a>
                </nav>
                <div class="footer-info-text">
                    상호명: (주)마이리틀스마트팜 | 대표자: 홍길동 | 사업자등록번호: 123-45-67890<br>
                    주소: 서울특별시 강남구 테헤란로 123 팜타워 15층<br>
                    통신판매업신고: 제 2026-서울강남-01234호
                </div>
            </div>

            <div class="col-4 footer-right">
                <div class="footer-logo">
                    마이리틀<span>스마트팜</span>
                </div>
                <div class="sns-group">
                    <a href="#"><t:icon name="instagram" size="24"/></a>
                    <a href="#"><t:icon name="youtube" size="24"/></a>
                </div>
                <div class="copyright">
                    © 2026 My Little SmartFarm.
                </div>
            </div>
        </div>
    </div>
</footer>