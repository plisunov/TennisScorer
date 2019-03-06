<!DOCTYPE html>
<%@page pageEncoding="UTF-8"%>
<%
	request.setCharacterEncoding("UTF-8");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
	<div class="container">

		<div class="starter-template">
			<h1>403 - Доступ запрещен</h1>
			<div th:inline="text">Недостаточно полномочий для просмотра
				этой страницы. Обратитесь к администратору.</div>
		</div>
	</div>
</body>
</html>