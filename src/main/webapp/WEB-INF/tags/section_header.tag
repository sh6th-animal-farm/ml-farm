<%@ tag language="java" pageEncoding="UTF-8" body-content="empty" %>

<%@ attribute name="title" required="true" %>
<%@ attribute name="subtitle" required="true" %>

<div class="section-header">
    <h2>${title}</h2>
    <p>${subtitle}</p>
</div>

<style>
.section-header {
  display:flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 24px;
}

.section-header h2 {
  font: var(--font-header-01);
  color: var(--gray-900);
}

.section-header p {
  font: var(--font-body-01);
  color: var(--gray-700);
}
</style>