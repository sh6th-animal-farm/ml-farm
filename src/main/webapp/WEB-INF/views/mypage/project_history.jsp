<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>

<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/mypage.css">

<script>
  window.ctx = "${pageContext.request.contextPath}";
</script>

<style>
  .content-wrapper { padding-top: 8px; }

  .mp-tabs {
    display: flex;
    gap: 24px;
    align-items: flex-end;
    margin: 10px 0 16px;
    border-bottom: 1px solid var(--gray-200);
  }
  .mp-tab {
    border: none;
    background: transparent;
    padding: 10px 0 12px;
    cursor: pointer;
    color: var(--gray-400);
    font: var(--font-body-02);
    font-weight: 700;
    position: relative;
  }
  .mp-tab .mp-tab-count { margin-left: 6px; font-weight: 800; }
  .mp-tab.is-active { color: var(--green-600); }
  .mp-tab.is-active::after{
    content:"";
    position:absolute;
    left:0; right:0; bottom:-1px;
    height:2px;
    background: var(--green-600);
    border-radius: 2px;
  }

  .filter-group { display:flex; gap:8px; margin-bottom: 16px; }
  /* t:menu_button이 button을 뽑든 a를 뽑든, 공통으로 맞추기 */
  .filter-group button,
  .filter-group a {
    border: 0;
    background: #F3F4F6;
    color: var(--gray-500);
    padding: 8px 14px;
    border-radius: 999px;
    cursor: pointer;
    font: var(--font-caption-02);
    font-weight: 700;
    text-decoration: none;
    display: inline-flex;
    align-items: center;
    gap: 6px;
  }
  .filter-group .is-active,
  .filter-group button.is-active,
  .filter-group a.is-active {
    background: #E8F5E9;
    color: var(--green-600);
  }

  .project-list {
    background: #fff;
    border-radius: var(--radius-l);
    box-shadow: var(--shadow);
    padding: 6px 24px;
  }

  .mp-item {
    display:flex;
    justify-content: space-between;
    align-items: center;
    padding: 18px 0;
    border-bottom: 1px solid var(--gray-100);
  }
  .mp-item:last-child { border-bottom: none; }

  .mp-item-title { font: var(--font-body-02); font-weight: 800; color: var(--gray-900); }
  .mp-item-period { margin-top: 6px; font: var(--font-caption-01); color: var(--gray-400); }

  .mp-item-right { display:flex; align-items:center; gap: 10px; }

  .mp-badge {
    padding: 6px 12px;
    border-radius: 999px;
    font: var(--font-caption-02);
    font-weight: 800;
    white-space: nowrap;
  }
  .mp-badge.blue { background:#EAF1FF; color:#3B6EDC; }
  .mp-badge.green { background:#ECF8EC; color:#3AA65C; }
  .mp-badge.yellow { background:#FFF5E0; color:#E3A300; }
  .mp-badge.gray { background:#F2F2F2; color:#666; }

  .btn-token {
    border: 0;
    background: #111827;
    color: #fff;
    padding: 10px 18px;
    border-radius: 12px;
    cursor: pointer;
    font-weight: 800;
    font-size: 13px;
  }

  .mp-empty {
    padding: 24px 0;
    text-align: center;
    color: var(--gray-400);
    font: var(--font-body-02);
  }

</style>

<div class="mypage-container">
  <div class="sidebar-wrapper">
    <jsp:include page="/WEB-INF/views/common/mypage_sidebar.jsp" />
  </div>

  <div class="content-wrapper">
    <t:section_header title="나의 프로젝트" subtitle="참여 중이거나 관심 있는 농업 재생 프로젝트 현황입니다." />

    <!-- 탭: JS가 count 채우고 active 토글 -->
    <div class="mp-tabs" id="projectTabs">
      <button class="mp-tab is-active" type="button" data-type="JOIN">
        참여한 프로젝트 <span class="mp-tab-count" id="tabJoinCount">0</span>
      </button>
      <button class="mp-tab" type="button" data-type="STAR">
        관심 프로젝트 <span class="mp-tab-count" id="tabStarCount">0</span>
      </button>
    </div>

    <div class="filter-group" id="projectFilters">
      <t:menu_button label="전체보기" active="true"  onClick="window.mpSetStatus('ALL')" />
      <t:menu_button label="청약중"   active="false" onClick="window.mpSetStatus('SUBSCRIPTION')" />
      <t:menu_button label="공고중"   active="false" onClick="window.mpSetStatus('ANNOUNCEMENT')" />
      <t:menu_button label="종료됨"   active="false" onClick="window.mpSetStatus('ENDED')" />
    </div>

    <div class="project-list" id="mpProjectList"></div>

    <button class="btn-more" id="mpMoreBtn" type="button" style="display:none;">+ 더보기</button>
  </div>
</div>

<script defer src="${pageContext.request.contextPath}/resources/js/domain/mypage/project_history.js"></script>
