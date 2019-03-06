package by.plisunov.tennis.model;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class GameSnapshot {

    private String gameId;

    private int score1;

    private int score2;

    private  int setNumber;

    private int setScore1;

    private int setScore2;

    private boolean hasYellow1;

    private boolean hasYellow2;

    private int brake1;

    private int brake2;

    private Date actionTime;

}
