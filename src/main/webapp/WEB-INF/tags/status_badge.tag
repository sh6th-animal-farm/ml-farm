<%@ tag language="java" pageEncoding="UTF-8" body-content="empty" %>
<%@ attribute name="className" required="false" %>
<%@ attribute name="status" required="true" %>
<%@ attribute name="label" required="true" %>

<div class="${className} status-badge ${status}">
    ${label}
</div>

<style>
.status-badge {
	display: inline-block;
	padding: 4px 12px;
	border-radius: var(--radius-s);
	font: var(--font-button-02); 
	text-align: center;
}

.announcement {
	background-color: var(--info-light);
	color : var(--info);
}

.subscription {
	background-color: var(--warning-light);
	color : var(--warning);
}

.inProgress {
	background-color: var(--green-0);
	color : var(--green-700);
}

.others{
	background-color: var(--gray-100);
	color : var(--gray-600);
}
</style>