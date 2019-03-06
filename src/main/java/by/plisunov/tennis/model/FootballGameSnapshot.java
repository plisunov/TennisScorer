package by.plisunov.tennis.model;

import by.plisunov.util.Constants;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@Builder
public class FootballGameSnapshot {

    private String team1;

    private String team2;

    private Integer score1;

    private Integer score2;

    private Integer count1;

    private Integer count2;

    private Constants.GameStatus status;

    private LocalDateTime lastActionDate;

    private long timeSnapshot;

}
