var gameCount=0;

var updateContent = function() {
	$.ajax({
		url : "api/football/showgames",
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
			/*if (!gamesArray[i].alive) {
				builderHTML += " style=\"background-color:red;\"";
			}*/
			builderHTML +=">";
		}
		builderHTML +=	
		"<div class=\"panel panel-default\">"+
			"<div class=\"panel-heading\"></div>"+
			"<div class=\"panel-body\">"+
				"<label class=\"gamerName\">"+gamesArray[i].team1+"</label> ";
		/*if (gamesArray[i].player1Yellow){
			builderHTML +="<label class=\"yellowcard\"/>";
		} else if (gamesArray[i].player1Red){
			builderHTML +="<label class=\"redcard\"/>";
		}*/
		builderHTML +="<label class=\"gamerName\">"+gamesArray[i].team2+"</label>";
		/*if (gamesArray[i].player2Yellow){
			builderHTML +="<label class=\"yellowcard\"/>";
		} else if (gamesArray[i].player2Red){
			builderHTML +="<label class=\"redcard\"/>";
		}*/
		builderHTML +="<br/>";
		/*if (gamesArray[i].timeoutPlayer1){
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
		}*/

		builderHTML += "<label class=\"infoString\"> Игроки "+"</label>"+
            "<br />" +
            "<label class=\"scoreName\">"+gamesArray[i].count1+"</label>"+
            "<label class=\"scoreName\">"+gamesArray[i].count2+"</label>"+
            "<br/>";

		/*if (gamesArray[i].brakePlayer1>0){
			builderHTML +="<label class=\"timeoutInfo\">"+gamesArray[i].brakePlayer1+"</label> " +
			"<label class=\"timeoutInfo\"></label>"+
			"<br/>";	
		} else if (gamesArray[i].brakePlayer2>0){
			builderHTML +="<label class=\"timeoutInfo\"></label> " +
			"<label class=\"timeoutInfo\">"+gamesArray[i].brakePlayer2+"</label>"+
			"<br/>";
		}; */
		if (gamesArray[i].status==="PAUSED"){
			builderHTML +="<label class=\"pauseString\">Игра приостановлена</label>";
		}
		if (gamesArray[i].status==="FINISH"){
			builderHTML +="<label class=\"pauseString\">Игра закончена</label>";
		}
		builderHTML +="<label class=\"infoString\">Общий счет в матче</label>"+ 
				"<br/>"+ 
				"<label class=\"totalScore\">"+gamesArray[i].score1+":"+gamesArray[i].score2+"</label>"+
				/*"<table>"+
					"<tr>";
			for (k=0;k<gamesArray[i].frameCount;k++){
				builderHTML+="<td>"+gamesArray[i].sets[k].score1+"</td>";
			}
			builderHTML+="</tr>"+
					"<tr>";
			for (k=0;k<gamesArray[i].frameCount;k++){
				builderHTML+="<td>"+gamesArray[i].sets[k].score2+"</td>";
			}
			builderHTML+= "</tr>"+
				"</table>"+*/
			"</div>"+
		"</div>"+
	"</div>";
	}
	$("div.containers").html(builderHTML);
}
 