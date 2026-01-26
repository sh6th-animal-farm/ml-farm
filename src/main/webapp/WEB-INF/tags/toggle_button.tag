<%@ tag language="java" pageEncoding="UTF-8" body-content="empty" %>
<%@ attribute name="id" required="false" %>
<%@ attribute name="checked" required="false" type="java.lang.Boolean" %>
<%@ attribute name="onchange" required="false" %> <%-- 호출할 함수명이나 스크립트 --%>

<label class="switch">
    <input type="checkbox"
            ${not empty id ? 'id="' += id += '"' : ''} 
           ${checked ? 'checked' : ''}
           onchange="${onchange}">
    <span class="slider"></span>
</label>

<style>
.switch {
    position: relative; display: inline-block; width: 44px; height: 24px;
}
.switch input { opacity: 0; width: 0; height: 0; }
.slider {
    position: absolute; cursor: pointer; top: 0; left: 0; right: 0; bottom: 0;
    background-color: #E0E0E0; transition: .4s; border-radius: 24px;
}
.slider:before {
    position: absolute; content: ""; height: 18px; width: 18px; left: 3px; bottom: 3px;
    background-color: white; transition: .4s; border-radius: 50%;
}
input:checked + .slider { background-color: var(--green-600); }
input:checked + .slider:before { transform: translateX(20px); }
</style>
