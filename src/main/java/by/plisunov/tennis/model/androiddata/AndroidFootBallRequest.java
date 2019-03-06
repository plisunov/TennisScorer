package by.plisunov.tennis.model.androiddata;

import by.plisunov.util.Constants;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AndroidFootBallRequest {

    private String team1;

    private String team2;

    private long timeSnapshot;

    private Constants.FOOTBAL_ACTION action;
}
