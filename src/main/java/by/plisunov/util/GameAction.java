package by.plisunov.util;

import java.util.Arrays;
import java.util.List;

public enum GameAction {

    GAME_STARTED,
    GAME_ACTIVATED,
    GAME_TIME_PROGRESS,
    GAME_PAUSED,
    GAME_RESUMED,
    TEAM_1_SCORE_INKREMENT,
    TEAM_2_SCORE_INCREMENT;


    public static List<GameAction> actionsForHockeyGameSnapshot() {
        return Arrays.asList(GAME_STARTED);
    }

}
