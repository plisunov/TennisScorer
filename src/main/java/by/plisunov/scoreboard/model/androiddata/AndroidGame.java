package by.plisunov.scoreboard.model.androiddata;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
//TODO replace to abstract.
public class AndroidGame {

    private String gameId;

    private int period;

    private boolean fineSession;

    private boolean extraTime;

    private int elapsedMinutes;

    private int elapsedSeconds;

    private String team1Name;

    private int team1Score;

    private boolean team1Fine;

    private int team1FineScore;

    private String team2Name;

    private int team2Score;

    private boolean team2Fine;

    private int team2FineScore;

}
