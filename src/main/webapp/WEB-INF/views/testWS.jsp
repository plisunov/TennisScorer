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
<spring:url value="/resources/js/index.js" var="pageJS" />
	<spring:url value="/resources/js/stomp.js" var="stomp" />

	<spring:url value="/resources/js/sockjs.js" var="sockjs" />


	http://cdn.sockjs.org/sockjs-0.3.4.js

	<spring:url value="/resources/css/bootstrap.min.css" var="bootstrapCss" />
<spring:url value="/resources/css/tabulator_bootstrap.min.css"
	var="bootstrapTableCss" />
<spring:url value="/resources/css/index.css" var="mainPageCss" />
<link href="${bootstrapCss}" rel="stylesheet" />
<link href="${bootstrapTableCss}" rel="stylesheet" />
<link href="${mainPageCss}" rel="stylesheet" />
<title>Tennis live scorring</title>




</head>
<body>
	<nav class="navbar navbar-inverse navbar-fixed-top">
		<div class="container">
			<div class="navbar-header">
				<b class="navbar-brand">Tennis live scorring</b>
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

	<div>
		<div>
			<button id="connect" onclick="connect();">Connect</button>
			<button id="disconnect" disabled="disabled" onclick="disconnect();">Disconnect</button>
		</div>
		<div id="conversationDiv">
			<button id="sendName" onclick="getGamesInfo();">Get all</button>
			<br>
			<label>Game id</label><input type="text" id="gameid" />
			<button id="sendName" onclick="getGameInfo();">Get current</button>
			<p id="response"></p>
		</div>
	</div>

	<script type="text/javascript" src="${jQuery}"></script>
	<script type="text/javascript" src="${pageJS}"></script>
	<script type="text/javascript" src="${bootstrapJs}"></script>
	<script type="text/javascript" src="${stomp}"></script>
	<script type="text/javascript" src="${sockjs}"></script>

	<script type="text/javascript">
        var stompClient = null;

        function setConnected(connected) {
            document.getElementById('connect').disabled = connected;
            document.getElementById('disconnect').disabled = !connected;
            document.getElementById('conversationDiv').style.visibility = connected ? 'visible' : 'hidden';
            document.getElementById('response').innerHTML = '';
        }

        function connect() {
            ws = new WebSocket('ws://localhost:8080/livescore');
            ws.onopen = function(){
                ws.send(JSON.stringify({"event":"subscribe","channel":"results"})
				)
            }
           /* var socket = new SockJS('/ttennis_stg/score');
            stompClient = Stomp.over(socket);
            stompClient.connect({}, function(frame) {
                setConnected(true);
                console.log('Connected: ' + frame);
                stompClient.subscribe('/results/score', function(response){
                    showResponse(JSON.parse(response.body));
                });
            });*/
        }

        function disconnect() {
            stompClient.disconnect();
            setConnected(false);
            console.log("Disconnected");
        }

        function getGamesInfo() {
            stompClient.send("/tennisscorer/showallgames", {}, null);
        }

        function getGameInfo() {
            var gameid = document.getElementById('gameid').value;
            stompClient.send("/tennisscorer/showgame", {}, JSON.stringify({ 'matchId': gameid }));
        }

        function showResponse(message) {
            var response = document.getElementById('response');
            var p = document.createElement('p');
            p.style.wordWrap = 'break-word';
            p.appendChild(document.createTextNode(message));
            response.appendChild(p);
        }
	</script>

</body>
</html>