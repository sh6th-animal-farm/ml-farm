<%@ tag language="java" pageEncoding="UTF-8" body-content="empty" %>
<%@ attribute name="label" required="true" %>
<%@ attribute name="active" required="false" type="java.lang.Boolean" %>
<%@ attribute name="onClick" required="false" %>

<button class="menu-btn ${active ? 'active' : ''}" onClick="${onClick}">
    ${label}
</button>

<style>
.menu-btn {
    padding: 8px 12px;
    border-radius: var(--radius-m);
    background-color:transparent;
    font: var(--font-button-02);
    color: var(--gray-500);
    cursor: pointer;
    transition: all 0.2s;
    border:none;
}
.menu-btn.active {
    background: var(--green-600);
    color: white;
}
.menu-btn:hover:not(.active) {
    background: var(--green-0);
    color: var(--green-600);
}
</style>