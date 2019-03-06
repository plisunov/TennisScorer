var gameCount=0;

var updateContent = function() {
	$.ajax({
		url : "api/showallgames?withScheduled",
		type : "GET",
		complete : function(data) {
			if (data.status >= 200 && data.status < 300) {
				var responseSize = data.responseJSON.length;
				//if (responseSize!=gameCount){
					gameCount = responseSize; 
					updateMatches(data.responseJSON);
				//}
			}
		}
	});
}
var updateMatches = function(gamesArray){
	$("div.containers").html("");
	var builderHTML="";
	for (i=0; i<gamesArray.length; i++){
		if (gamesArray[i].status==="PAUSED"){
			builderHTML +=
				"<div class=\"container\" id="+gamesArray[i].matchId+" style=\"background-color:gray;\">";
		} else {
			builderHTML +=
				"<div class=\"container\" id="+gamesArray[i].matchId;
			if (!gamesArray[i].alive && gamesArray[i].status==="ACTIVE") {
				builderHTML += " style=\"background-color:red;\"";
			}
			builderHTML +=">";
		}
		builderHTML +=	
		"<div class=\"panel panel-default\">"+
			"<div class=\"panel-heading\"></div>"+
			"<div class=\"panel-body\">"+
				"<label class=\"gamerName\">"+gamesArray[i].player1.name+"</label> ";
		if (gamesArray[i].player1Yellow){
			builderHTML +="<label class=\"yellowcard\"/>";
		} else if (gamesArray[i].player1Red){
			builderHTML +="<label class=\"redcard\"/>";
		}
		builderHTML +="<label class=\"gamerName\">"+gamesArray[i].player2.name+"</label>";
		if (gamesArray[i].player2Yellow){
			builderHTML +="<label class=\"yellowcard\"/>";
		} else if (gamesArray[i].player2Red){
			builderHTML +="<label class=\"redcard\"/>";
		}
		builderHTML +="<br/>";
		if (gamesArray[i].timeoutPlayer1){
			builderHTML +="<label class=\"timeoutInfo\">Таймаут</label> " +
			"<label class=\"timeoutInfo\"></label>"+
			"<br/>";	
		} else if (gamesArray[i].timeoutPlayer2){
			builderHTML +="<label class=\"timeoutInfo\"></label> " +
			"<label class=\"timeoutInfo\">Таймаут</label>"+
			"<br/>";
		} else {
			builderHTML +="<label class=\"timeoutInfo\"></label> " +
			"<label class=\"timeoutInfo\"></label>"+
			"<br/>";
		}
        if (gamesArray[i].status != "SCHEDULED") {
            builderHTML += "<label class=\"infoString\"> Сет " + gamesArray[i].currentSet + " из " + gamesArray[i].frameCount + "</label>" +
                "<br />" +
                "<label class=\"scoreName\">" + gamesArray[i].sets[gamesArray[i].currentSet - 1].score1 + "</label>" +
                "<label class=\"scoreName\">" + gamesArray[i].sets[gamesArray[i].currentSet - 1].score2 + "</label>" +
                "<br/>";
            if (gamesArray[i].brakePlayer1 > 0) {
                builderHTML += "<label class=\"timeoutInfo\">" + gamesArray[i].brakePlayer1 + "</label> " +
                    "<label class=\"timeoutInfo\"></label>" +
                    "<br/>";
            } else if (gamesArray[i].brakePlayer2 > 0) {
                builderHTML += "<label class=\"timeoutInfo\"></label> " +
                    "<label class=\"timeoutInfo\">" + gamesArray[i].brakePlayer2 + "</label>" +
                    "<br/>";
            }
        }
		if (gamesArray[i].status==="PAUSED"){
			builderHTML +="<label class=\"pauseString\">Игра приостановлена</label>";
		}
		if (gamesArray[i].status==="FINISH"){
			builderHTML +="<label class=\"pauseString\">Игра закончена</label>";
		}
        if (gamesArray[i].status==="SCHEDULED"){
            builderHTML +="<label class=\"pauseString\">Игра не началась. Запланировано  " + gamesArray[i].matchTime + "</label>";
        }
        if (gamesArray[i].status != "SCHEDULED") {
            builderHTML += "<label class=\"infoString\">Общий счет в матче</label>" +
                "<br/>" +
                "<label class=\"totalScore\">" + gamesArray[i].player1Score + ":" + gamesArray[i].player2Score + "</label>" +
                "<table>" +
                "<tr>";
            for (k = 0; k < gamesArray[i].frameCount; k++) {
                builderHTML += "<td>" + gamesArray[i].sets[k].score1 + "</td>";
            }
            builderHTML += "</tr>" +
                "<tr>";
            for (k = 0; k < gamesArray[i].frameCount; k++) {
                builderHTML += "<td>" + gamesArray[i].sets[k].score2 + "</td>";
            }
            builderHTML += "</tr>" +
                "</table>";
        }
        builderHTML +="</div>"+
		"</div>"+
	"</div>";
	}
	$("div.containers").html(builderHTML);
}
 