<%@ tag language="java" pageEncoding="UTF-8" body-content="empty" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<div class="search-container col-4">
    <input type="text" placeholder="검색어를 입력해주세요" class="search-input">
    <button class="search-icon-btn">
        <t:icon name="search" size="20" color="var(--gray-900)"/>
    </button>
</div>

<style>
.search-container {
    position: relative;
    height: 40px;
}
.search-input {
    width: 100%;
    height: 100%;
    padding: 0px;
    border-radius: var(--radius-xl);
    border: 1px solid var(--gray-400);
    font: var(--font-body-01);
    outline: none;
}
.search-input:focus {
    border-color: var(--green-600);
}
.search-icon-btn {
    position: absolute;
    right: 20px;
    top: 50%;
    transform: translateY(-50%);
    background: none;
    border: none;
    cursor: pointer;
}
</style>