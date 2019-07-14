package by.plisunov.scoreboard.model;

import by.plisunov.util.Constants;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class HockeyGame extends BaseGame {

    private int fineScore1;

    private int fineScore2;

    private int elapsedMinutes;

    private int elapsedSeconds;

    private boolean fineSession;

    private boolean extraTime;

    private List<HockeyPeriod> periods = Arrays.asList(new HockeyPeriod(), new HockeyPeriod(), new HockeyPeriod());

    private List<HockeyFine> fines = new ArrayList<>();

    public HockeyGame(TournamentInfo game) {
        this.gameId = game.getId();
        period = 1;
        String[] firstPlayerData = game.getFirstplayer().split(":");
        String[] secondPlayerData = game.getSecondplayer().split(":");
        this.teamPlayer1 = Team.builder().id(firstPlayerData[1]).name(firstPlayerData[0]).build();
        this.teamPlayer2 = Team.builder().id(secondPlayerData[1]).name(secondPlayerData[0]).build();
        this.status = Constants.GameStatus.SCHEDULED;
    }

}
