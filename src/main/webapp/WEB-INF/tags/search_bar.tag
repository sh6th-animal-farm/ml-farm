<%@ tag language="java" pageEncoding="UTF-8" body-content="empty" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ attribute name="width" required="false" %>

<%--<div class="search-container col-4" style="width:${width};">--%>
<%--    <input type="text" placeholder="검색어를 입력해주세요" onkeyup="if(window.event.keyCode==13){searchKeyword()}" class="search-input">--%>
<%--    <button class="search-icon-btn" onclick="searchKeyword()">--%>
<%--        <t:icon name="search" size="20" color="var(--gray-900)"/>--%>
<%--    </button>--%>
<%--</div>--%>

<style>
.search-container {
    position: relative;
    height: 40px;
    padding: 0px;
    box-shadow: var(--shadow);
}
.search-input {
    width: 100%;
    height: 100%;
    padding: 0px;
    border-radius: var(--radius-xl);
    font: var(--font-body-01);
    border: none;
    outline: none;
    background: transparent;
}
.search-input:focus {
    border-color: var(--green-600);
}
.search-icon-btn {
    position: absolute;
    right: 16px;
    top: 50%;
    transform: translateY(-50%);
    background: none;
    border: none;
    cursor: pointer;
}
</style>
