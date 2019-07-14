var gameCount=0;

var updateContent = function() {
	$.ajax({
		url : "hockey/api/showallgames?withScheduled",
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
            "<table>"
            + "<tbody>"
            + "<tr>"
            + "<td class=\"td1\"></td>"
            + "<td class=\"td2\">Команда</td>"
            + "<td class=\"td3\">Счет</td>"
            + "<td class=\"td4\">1 тайм</td>"
            + "<td class=\"td5\">2 тайм</td>"
            + "<td class=\"td6\">3 тайм</td>"
            + "<td class=\"td7\">Буллиты</td>"
            + "<td class=\"td8\">Статус</td>"
            + "</tr>"
            + "<tr>"
            + "<td class=\"td1\">" + gamesArray[i].period + " период</td>"
            + "<td class=\"td2\">" + gamesArray[i].teamPlayer1.name + "</td>"
            + "<td class=\"td3\">" + gamesArray[i].team1Score + "</td>"
            + "<td class=\"td4\">" + gamesArray[i].periods[0].score1 + "</td>"
            + "<td class=\"td5\">" + gamesArray[i].periods[1].score1 + "</td>"
            + "<td class=\"td6\">" + gamesArray[i].periods[2].score1 + "</td>"
            + "<td class=\"td7\">" + gamesArray[i].fineScore1 + "</td>";
        if (gamesArray[i].team1Fine) {
            builderHTML += "<td class=\"td8\">Буллит</td>";
        } else {
            builderHTML += "<td class=\"td8\"></td>";
        }
        builderHTML +=
            "</tr>"
            + "<tr>"
            + "<td class=\"td1\">" + gamesArray[i].elapsedMinutes + ":" + gamesArray[i].elapsedSeconds + "</td>"
            + "<td class=\"td2\">" + gamesArray[i].teamPlayer2.name + "</td>"
            + "<td class=\"td3\">" + gamesArray[i].team2Score + "</td>"
            + "<td class=\"td4\">" + gamesArray[i].periods[0].score2 + "</td>"
            + "<td class=\"td5\">" + gamesArray[i].periods[1].score2 + "</td>"
            + "<td class=\"td6\">" + gamesArray[i].periods[2].score2 + "</td>"
            + "<td class=\"td7\">" + gamesArray[i].fineScore2 + "</td>";
        if (gamesArray[i].team2Fine) {
            builderHTML += "<td class=\"td8\">Буллит</td>";
        } else {
            builderHTML += "<td class=\"td8\"></td>";
        }
        builderHTML += "</tr>"
            + "</tbody>"
            + "</table>";
		if (gamesArray[i].status==="PAUSED"){
			builderHTML +="<label class=\"pauseString\">Игра приостановлена</label>";
		}
		if (gamesArray[i].status==="FINISH"){
			builderHTML +="<label class=\"pauseString\">Игра закончена</label>";
		}
        if (gamesArray[i].status==="SCHEDULED"){
            builderHTML +="<label class=\"pauseString\">Игра не началась. Запланировано  " + gamesArray[i].matchTime + "</label>";
        }
        builderHTML +="</div>"+
		"</div>"+
	"</div>";
	}
	$("div.containers").html(builderHTML);
}
 