<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<%@page pageEncoding="UTF-8"%>
<%
	request.setCharacterEncoding("UTF-8");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<spring:url value="/resources/js/login.js" var="loginJS" />
<spring:url value="/resources/css/login.css" var="loginPageCss" />
<link href="${loginPageCss}" rel="stylesheet" />
<title>Tennis live scorring</title>
</head>
<body>
	<div class="container">
		<c:url value="/j_spring_security_check" var="loginUrl" />
		<div class="row">
			<div class="col-md-12">
				<div class="pr-wrap">
					<div class="pass-reset">
						<label> Enter the email you signed up with</label> <input
							type="email" placeholder="Email" /> <input type="submit"
							value="Submit" class="pass-reset-submit btn btn-success btn-sm" />
					</div>
				</div>
				<div class="wrap">
					<p class="form-title">Авторизация</p>
					<form action="${loginUrl}" method="post" class="login">
						<input type="text" placeholder="Имя" name="j_username">
						<input type="password" name="j_password" placeholder="Пароль">
						<input type="submit" value="Войти" class="btn btn-success btn-sm">
						<div class="remember-forgot">
							<div class="row">
								<div class="col-md-6">
									<!--div class="checkbox">
										<label> <input type="checkbox" /> Remember Me
										</label>
									</div-->
								</div>
								<div class="col-md-6 forgot-pass-content">
									<a href="javascription:void(0)" class="forgot-pass">Вспомнить
										пароль</a>
								</div>
							</div>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>
	<script type="text/javascript" src="${loginJS}"></script>
</body>

</html>