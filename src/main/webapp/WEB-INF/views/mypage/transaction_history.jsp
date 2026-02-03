<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="mp" tagdir="/WEB-INF/tags/mypage"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%
    /* 상단 탭 데이터 생성 (value를 API 명세의 대분류 category 값으로 설정) */
    List<Map<String, Object>> transactionTabs = new ArrayList<>();
    
    Map<String, Object> tab1 = new HashMap<>();
    tab1.put("title", "토큰");
    tab1.put("value", "TOKEN"); // 클릭 시 category=TOKEN 전송
    transactionTabs.add(tab1);
    
    Map<String, Object> tab2 = new HashMap<>();
    tab2.put("title", "프로젝트");
    tab2.put("value", "PROJECT"); // 클릭 시 category=PROJECT 전송
    transactionTabs.add(tab2);
    
    request.setAttribute("transactionTabs", transactionTabs);
%>

<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/mypage.css">
<style>
    /* 거래 내역 전용 스타일 */
    .filter-container { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
    .filter-group { display: flex; gap: 4px; flex:1;}
    
    .period-select { 
    	width: 100px;
        padding: 8px 12px; border: 1px solid #F1F1F1; border-radius: 8px; 
        font-size: 13px; color: var(--gray-700); cursor: pointer; outline: none;
    }

</style>

<div class="mypage-container">
    <div class="sidebar-wrapper">
        <jsp:include page="/WEB-INF/views/common/mypage_sidebar.jsp" />
    </div>

    <div class="content-wrapper">
    
    	<t:section_header title="거래 내역" subtitle="투자, 충전, 정산 등 모든 거래 기록을 확인하세요." />
		<t:category_tab 
            items="${transactionTabs}" 
            activeValue="${empty param.category ? 'TOKEN' : param.category}"         
        />
		
        <div class="filter-container">
	        <div id="filter-group-token" class="filter-group">
                <t:menu_button label="전체보기" active="true" onClick="changeFilter('TOKEN', this)"/>
                <t:menu_button label="매수" active="false" onClick="changeFilter('BUY', this)"/>
                <t:menu_button label="매도" active="false" onClick="changeFilter('SELL', this)"/>
            </div>

            <div id="filter-group-project" class="filter-group" style="display: none;">
                <t:menu_button label="전체보기" active="true" onClick="changeFilter('PROJECT', this)"/>
                <t:menu_button label="당첨(청약)" active="false" onClick="changeFilter('PASS', this)"/>
                <t:menu_button label="낙첨(환불)" active="false" onClick="changeFilter('FAIL', this)"/>
                <t:menu_button label="배당" active="false" onClick="changeFilter('DIVIDEND', this)"/>
                <t:menu_button label="소각" active="false" onClick="changeFilter('BURN', this)"/>
            </div>
            <select class="period-select" id="periodSelect" onchange="filterPeriod()">
                <option value="0">전체 기간</option>
                <option value="1">최근 1개월</option>
                <option value="3">최근 3개월</option>
                <option value="6">최근 6개월</option>
            </select>
        </div>

        <div id="transactionTableContainer">
            <%-- 초기 로딩 시 빈 리스트로 껍데기만 렌더링하고 JS로 채웁니다 --%>
		    <mp:transaction_history_table transactionList="${emptyList}"/>
		</div>
		
		<button class="btn-more" id="btnMore" onclick="loadMore()" style="display: none;">+ 더보기</button>
		
    </div>
</div>

<script>
// 전역 변수
let currentPage = 1;
// main 없이 category만 사용 (기본값: TOKEN)
let currentCategory = 'TOKEN'; 
let currentPeriod = 0;
let currentTabType = 'TOKEN'; 

document.addEventListener("DOMContentLoaded", function() {
    loadTransactionHistory(false);
});

// 탭 변경
function handleTabClick(value) {
    if(currentTabType === value) return;

 // 1. 데이터 변경
    currentTabType = value;     
    currentCategory = value;    
    currentPage = 1;

    // 2. [중요] UI 업데이트 (녹색 밑줄 이동)
    // 태그 파일이 만들어낸 .tab-item 요소들을 찾아서 active 클래스를 직접 제어합니다.
    const tabs = document.querySelectorAll('.tab-item');
    tabs.forEach(tab => {
        // 탭의 텍스트나 내부 구조를 통해 클릭된 탭인지 확인
        // (가장 확실한 방법은 태그 파일의 onclick 문자열을 확인하는 것입니다)
        const onclickAttr = tab.getAttribute('onclick');
        if (onclickAttr && onclickAttr.includes("'" + value + "'")) {
            tab.classList.add('active');
        } else {
            tab.classList.remove('active');
        }
    });

    // 3. 하단 필터 그룹 교체 (토큰 <-> 프로젝트)
    const tokenFilters = document.getElementById('filter-group-token');
    const projectFilters = document.getElementById('filter-group-project');

    if (currentTabType === 'TOKEN') {
        tokenFilters.style.display = 'flex';
        projectFilters.style.display = 'none';
        resetActiveButton(tokenFilters);
    } else {
        tokenFilters.style.display = 'none';
        projectFilters.style.display = 'flex';
        resetActiveButton(projectFilters);
    }
    
    // 4. 데이터 로드
    loadTransactionHistory(false);
}

// 필터 변경
function changeFilter(categoryValue, btnElement) {
    if(currentCategory === categoryValue && currentPage === 1) return;

    currentCategory = categoryValue;
    currentPage = 1;

    const parentGroup = btnElement.closest('.filter-group');
    const buttons = parentGroup.querySelectorAll('button');
    buttons.forEach(btn => btn.classList.remove('active'));
    btnElement.classList.add('active');

    loadTransactionHistory(false);
}

function resetActiveButton(groupElement) {
    const buttons = groupElement.querySelectorAll('button');
    buttons.forEach((btn, index) => {
        if(index === 0) btn.classList.add('active');
        else btn.classList.remove('active');
    });
}

// 데이터 로드
function loadTransactionHistory(isAppend) {
    const token = localStorage.getItem("accessToken");
  
    
    // API 호출 (category만 전송)
   let url = ctx + "/api/mypage/transaction-history?page=" + currentPage + 
        "&period=" + currentPeriod + 
        "&category=" + currentCategory;

    fetch(url, {
        method: "GET",
        headers: {
            "Accept": "application/json",
            "Authorization": "Bearer " + token
        }
    })
    .then(res => {
        if (!res.ok) throw new Error("데이터 로드 실패");
        return res.json();
    })
    .then(data => {
        const tbody = document.querySelector(".transaction-table tbody");
        const btnMore = document.getElementById("btnMore");
        if (!tbody) return;

        const list = data.payload || [];

        if (!isAppend) tbody.innerHTML = "";

        if (list.length === 0 && !isAppend) {
            tbody.innerHTML = '<tr><td colspan="6" style="text-align: center; padding: 60px; color: var(--gray-400);">거래 내역이 존재하지 않습니다.</td></tr>';
            if(btnMore) btnMore.style.display = "none";
            return;
        }

        let html = "";
        const typeConfig = {
            'BUY': { label: '매수', class: 'type-buy' },
            'SELL': { label: '매도', class: 'type-sell' },
            'PASS': { label: '청약', class: 'type-sub' },
            'FAIL': { label: '환불', class: 'type-ref' },
            'DIVIDEND': { label: '배당', class: 'type-div' },
            'BURN': { label: '소각', class: 'type-div' }
        };

        list.forEach(tx => {
            const config = typeConfig[tx.transactionType] || { label: tx.transactionType, class: '' };
            
            let timestamp = tx.createdAt;
            if (typeof timestamp === 'number' && timestamp < 10000000000) {
                timestamp = timestamp * 1000;
            }
            const date = new Date(timestamp);
            const dateStr = date.getFullYear() + '-' + 
                            String(date.getMonth() + 1).padStart(2, '0') + '-' + 
                            String(date.getDate()).padStart(2, '0') + ' ' + 
                            String(date.getHours()).padStart(2, '0') + ':' + 
                            String(date.getMinutes()).padStart(2, '0');

            html += `
                <tr>
                    <td class="t-date">\${dateStr}</td>
                    <td class="t-type \${config.class}">\${config.label}</td>
                    <td>
                        <div class="t-name">\${tx.tokenName}</div>
                        <div class="t-code">\${tx.tickerSymbol}</div>
                    </td>
                    <td style="text-align: right; font-weight: 700;">
                        \${tx.executedPrice ? tx.executedPrice.toLocaleString() + ' 원' : '-'}
                    </td>
                    <td style="text-align: right; font-weight: 700;">
                        \${tx.executedVolume ? (tx.executedVolume > 0 ? '+' : '') + tx.executedVolume.toLocaleString() + ' st' : '-'}
                    </td>
                    <td>
                        <div class="t-amount">\${(tx.executedAmount > 0 ? '+' : '') + tx.executedAmount.toLocaleString()} 원</div>
                        <div class="t-balance">\${tx.balanceAfter.toLocaleString()} 원</div>
                    </td>
                </tr>
            `;
        });
        
        if (isAppend) tbody.innerHTML += html;
        else tbody.innerHTML = html;

        if (btnMore) btnMore.style.display = (list.length < 10) ? "none" : "block";
    })
    .catch(err => console.error("Fetch Error:", err));
}

function filterPeriod() {
    currentPeriod = document.querySelector(".period-select").value;
    currentPage = 1;
    loadTransactionHistory(false);
}

function loadMore() {
    currentPage++;
    loadTransactionHistory(true);
}
</script>