<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html>
<%@page pageEncoding="UTF-8"%>
<%
	request.setCharacterEncoding("UTF-8");
%>
<html lang="en">
<head>
<spring:url value="/resources/js/bootstrap.min.js" var="bootstrapJs" />
<spring:url value="/resources/js/jquery-3.2.1.min.js" var="jQuery" />
<spring:url value="/resources/js/football.js" var="pageJS" />
	<spring:url value="/resources/css/bootstrap.min.css" var="bootstrapCss" />
<spring:url value="/resources/css/tabulator_bootstrap.min.css"
	var="bootstrapTableCss" />
<spring:url value="/resources/css/football.css" var="mainPageCss" />
<link href="${bootstrapCss}" rel="stylesheet" />
<link href="${bootstrapTableCss}" rel="stylesheet" />
<link href="${mainPageCss}" rel="stylesheet" />
<title>Football live scorring</title>




</head>
<body>
	<nav class="navbar navbar-inverse navbar-fixed-top">
		<div class="container">
			<div class="navbar-header">
				<b class="navbar-brand">Football live scorring</b>
				<ul class="nav navbar-nav navbar-right">
					<sec:authorize access="isAuthenticated()">
						<li><a href="#" /><span class="glyphicon glyphicon-user"></span>
							Пользователь: <sec:authentication property="principal.username" /></a></li>
					</sec:authorize>
					<li><a href="/logout" /><span class="glyphicon glyphicon-log-in"></span>
							Выйти</a></li>
				</ul>
			</div>
		</div>
	</nav>
	<div class="containers">
	</div>
	<script type="text/javascript" src="${jQuery}"></script>
	<script type="text/javascript" src="${pageJS}"></script>
	<script type="text/javascript" src="${bootstrapJs}"></script>

	<script type='text/javascript'>
		$(document).ready(function() {
			setInterval(updateContent, 1000);
		});
	</script>
</body>
</html>