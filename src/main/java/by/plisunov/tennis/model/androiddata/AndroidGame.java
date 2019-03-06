package by.plisunov.tennis.model.androiddata;

import by.plisunov.util.Constants;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AndroidGame {

    private int score1;

    private int score2;

    private  int setNumber;

    private int setScore1;

    private int setScore2;

    private boolean hasYellow1;

    private boolean hasYellow2;

    private int brake1;

    private int brake2;

    private String additionalInfo;

    private Constants.GAME_ACTION lastaction;
}
