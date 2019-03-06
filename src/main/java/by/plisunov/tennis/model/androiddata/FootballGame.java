package by.plisunov.tennis.model.androiddata;

import by.plisunov.tennis.model.FootballGameSnapshot;
import by.plisunov.util.Constants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.LinkedList;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class FootballGame {

    private String team1;

    private String team2;

    private Integer score1;

    private Integer score2;

    private Integer count1;

    private Integer count2;

    private Constants.GameStatus status;

    private LocalDateTime lastActionDate;

    @JsonIgnore
    private long timeSnapshot;

    @JsonIgnore
    private LinkedList<FootballGameSnapshot> snapshots = new LinkedList<>();

}
