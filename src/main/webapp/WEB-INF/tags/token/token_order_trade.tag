<%@ tag language="java" pageEncoding="UTF-8" body-content="empty" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="card trade-card">
    <div class="tab-menu">
        <button class="trade-btn active" data-tab="order-tab">호가</button>
        <button class="trade-btn" data-tab="trade-tab">체결</button>
    </div>
    <div id="order-tab" class="scroll active">
        <table class="hoga-table order-book-table">
            <thead>
            </thead>
            <tbody id="order-hist-body">
            </tbody>
        </table>
    </div>

    <div id="trade-tab" class="scroll">
        <table class="hoga-table trade-history-table">
            <tbody id="trade-hist-body">
            </tbody>
        </table>
    </div>
</div>