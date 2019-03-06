package by.plisunov.tennis.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;


import by.plisunov.tennis.model.androiddata.AndroidGameInfo;
import by.plisunov.util.Constants.GAME_ACTION;
import by.plisunov.util.Constants.GameStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.omg.PortableInterceptor.ACTIVE;

import static by.plisunov.util.Constants.GameStatus.SCHEDULED;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Game implements Serializable {

    private int Id;

    private String matchId;

    private String matchTime;

    private Player player1;

    private Player player2;

    private int player1Score;

    private int player2Score;

    private int frameCount;

    private GameStatus status;

    private int currentSet;

    private boolean timeoutPlayer1;

    private boolean timeoutPlayer2;

    private int brakePlayer1;

    private int brakePlayer2;

    private boolean player1Yellow;

    private boolean player2Yellow;

    private boolean player1Red;

    private boolean player2Red;

    private boolean isAlive;

    private int firstBraker;

    private GAME_ACTION lastAction;

    private LocalDateTime finishTime;

    private int player1FallsCount;

    private int player2FallsCount;

    private List<GameSet> sets;

    private LocalDateTime lastActionDate;

    @JsonIgnore
    private LinkedList<GameSnapshot> snapshots = new LinkedList<>();

    @JsonIgnore
    private boolean isSaved;

    public Game(Game game) {
        this.player1 = game.getPlayer1();
        this.player2 = game.getPlayer2();
        this.player1Score = 0;
        this.player2Score = 0;
        this.matchId = game.matchId;
        this.frameCount = game.getFrameCount();
        this.currentSet = 1;
        this.timeoutPlayer1 = false;
        this.timeoutPlayer2 = false;
        this.status = GameStatus.ACTIVE;
        this.brakePlayer1 = game.getBrakePlayer1() > 0 ? game.getBrakePlayer1() : 0;
        this.brakePlayer2 = game.getBrakePlayer2() > 0 ? game.getBrakePlayer2() : 0;
        List<GameSet> frames = new ArrayList<>(game.getFrameCount());
        for (int k = 0; k <= game.getFrameCount(); k++) {
            frames.add(new GameSet(this, k));
        }
        this.sets = frames;
        this.lastAction = GAME_ACTION.GAME_START;
        this.lastActionDate = LocalDateTime.now(ZoneId.of("Europe/Moscow"));
        this.firstBraker = game.firstBraker;
        this.status = SCHEDULED;
    }

    public Game(String gameId, String player1, String player2, String gameTime) {
        this.matchId = gameId;
        this.player1 = Player.builder().name(player1).build();
        this.player2 = Player.builder().name(player2).build();
        this.matchTime = gameTime;
        this.status = SCHEDULED;

    }

    public Game(TournamentInfo game) {
        this.matchId = game.getId();
        String[] firstPlayerData = game.getFirstplayer().split(":");
        String[] secondPlayerData = game.getSecondplayer().split(":");
        this.player1 = Player.builder().name(firstPlayerData[0]).Id(firstPlayerData[1]).build();
        this.player2 = Player.builder().name(secondPlayerData[0]).Id(secondPlayerData[1]).build();
        this.matchTime = game.getTourdate().split(" ")[1].substring(0,5);
        this.status = SCHEDULED;
    }

    public boolean getTime() {
        if (this.getLastActionDate() != null) {
            long totalMins = ChronoUnit.MINUTES.between(this.getLastActionDate(), LocalDateTime.now(ZoneId.of("Europe/Moscow")));
            if (totalMins >= 10) {
                return true;
            }
        }
        return false;
    }

    public static Game getAlive(Game game) {
        if (game.getLastActionDate() != null) {
            if ((game.getStatus().equals(GameStatus.ACTIVE)) && (ChronoUnit.SECONDS.between(game.getLastActionDate(), LocalDateTime.now(ZoneId.of("Europe/Moscow"))) >= 10)) {
                if (!game.isSaved) {
                    game.setAlive(false);
                }
            } else {
                game.setAlive(true);
            }
        }
        return game;
    }

    public static Game createGame(AndroidGameInfo gameInfo) {
        return new Game(gameInfo.getGameId(), gameInfo.getPlayer1(), gameInfo.getPlayer2(), gameInfo.getGameTime());
    }

    public void fillGameSets(int setCount) {
        this.frameCount = setCount;
        List<GameSet> frames = new ArrayList<>(setCount);
        for (int k = 0; k <= setCount; k++) {
            frames.add(new GameSet(this, k));
        }
        this.sets = frames;
    }

}
