<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>페이지를 찾을 수 없습니다 | MLF</title>
    <style>
        :root {
            --green-600: #16a34a;
            --gray-900: #111827;
            --gray-500: #6b7280;
            --gray-100: #f3f4f6;
        }

        body {
            margin: 0;
            padding: 0;
            display: flex;
            align-items: center;
            justify-content: center;
            height: 100vh;
            font-family: 'Pretendard', -apple-system, sans-serif;
            background-color: white;
            color: var(--gray-900);
        }

        .error-container {
            text-align: center;
            padding: 20px;
        }

        .error-code {
            font-size: 120px;
            font-weight: 800;
            margin: 0;
            line-height: 1;
            background: linear-gradient(135deg, var(--green-600), #22c55e);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
        }

        .error-title {
            font-size: 24px;
            font-weight: 700;
            margin-top: 24px;
            margin-bottom: 12px;
        }

        .error-desc {
            font-size: 16px;
            color: var(--gray-500);
            line-height: 1.6;
            margin-bottom: 40px;
        }

        .btn-group {
            display: flex;
            gap: 12px;
            justify-content: center;
        }

        .btn {
            padding: 12px 24px;
            border-radius: 8px;
            font-size: 16px;
            font-weight: 600;
            text-decoration: none;
            transition: all 0.2s;
            cursor: pointer;
        }

        .btn-home {
            background-color: var(--gray-900);
            color: white;
        }

        .btn-home:hover {
            background-color: #000;
            transform: translateY(-2px);
        }

        .btn-back {
            background-color: var(--gray-100);
            color: var(--gray-900);
        }

        .btn-back:hover {
            background-color: #e5e7eb;
        }

        /* 아이콘처럼 보일 수 있는 장식 */
        .icon-box {
            font-size: 64px;
            margin-bottom: 8px;
        }
    </style>
</head>
<body>

<div class="error-container">
	<t:icon name="seedling" size="100" color="var(--green-600)"/>
    <h1 class="error-code">404</h1>
    <h2 class="error-title">원하시는 페이지를 찾을 수 없습니다.</h2>
    <p class="error-desc">
        존재하지 않는 주소이거나, 페이지가 삭제되었을 수 있습니다.<br>
        입력하신 주소가 정확한지 다시 한번 확인해 주세요.
    </p>
    
    <div class="btn-group">
        <a href="javascript:history.back();" class="btn btn-back">이전으로</a>
        <a href="${pageContext.request.contextPath}/main" class="btn btn-home">홈으로 이동</a>
    </div>
</div>

</body>
</html>