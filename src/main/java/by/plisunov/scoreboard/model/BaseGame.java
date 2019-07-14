package by.plisunov.scoreboard.model;

import by.plisunov.util.Constants;
import by.plisunov.util.GameAction;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.LinkedList;

@Getter
@Setter
@EqualsAndHashCode()
public abstract class BaseGame {

    protected String gameId;

    protected int period;

    protected Team teamPlayer1;

    protected Team teamPlayer2;

    protected int team1Score;

    protected int team2Score;

    protected boolean team1Fine;

    protected boolean team2Fine;

    protected Constants.GameStatus status;

    protected GameAction lastAction;

    protected LocalDateTime lastActionDate;

    protected boolean isAlive;

    @JsonIgnore
    protected LinkedList<GameSnapshot> snapshots = new LinkedList<>();

    @JsonIgnore
    protected boolean isSaved;

}
