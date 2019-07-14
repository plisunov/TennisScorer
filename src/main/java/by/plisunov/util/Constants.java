package by.plisunov.util;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static by.plisunov.util.Constants.FOOTBAL_ACTION.*;
import static by.plisunov.util.Constants.GAME_ACTION.*;
import static by.plisunov.util.Constants.HOCKEY_ACTION.*;

public class Constants {

    //TODO remove all unused. COllect actions to single enum with custom sets.

    public static final String MATCH_ID = "match_id";

    public static final String SET_NUMBER = "set_number";

    public static final String PLAYER_NUMBER = "player_number";

    public static final String TYPE_POINT = "type_point";

    public static final String BRAKE_PLAYER2 = "brake_pl2";

    public static final String BRAKE_PLAYER1 = "brake_pl1";

    public static final Object PLAYER1_SCORE = "player1Score";

    public static final Object PLAYER2_SCORE = "player2Score";

    public static final Object IS_GAME_CONTINIUS = "last_frame";

    public static final String START_TO = "add";

    public static final String DAY_KEY = "sp_day";

    public static final String RESULTS_KEY = "sp_results";

    public static final String PLAYERS_KEY = "sp_player";

    public static final String SPECS_KEY = "sp_specs";

    public static final String ORDERS_KEY = "order_player";

    public static final String DETAILS_KEY = "sp_specs";

    public static final String EVENT_KEY = "sp_event";

    public static final String PUBLISH_KEY = "publish";

    public static final String COUNTER_KEY = "counter_players";

    public static final String DETAILS_TIME_FIELD = "counter_players_{0}_time";

    public static final String DETAILS_PLAYER1_SCORE_FIELD = "counter_players_{0}_player_1";

    public static final String DETAILS_PLAYER2_SCORE_FIELD = "counter_players_{0}_player_2";

    public static final String DETAILS_TOTAL_SCORE_FIELD = "counter_players_{0}_points";


    public enum GameStatus {
        ACTIVE, PAUSED, FINISH, POSSIBLE_FINISH, SCHEDULED
    }

    public enum SCORE_CHANGE_TYPE {
        ADD_POINT, REVERSE_POINT, YELLOW_CARD, RED_CARD
    }

    public enum GAME_ACTION {
        GAME_START, GAME_REVERSED, REVERSE_BRAKER, SET_ENDED, MATCH_ENDED, MATCH_PAUSED, PLAYER1_GET_TIMEOUT, PLAYER2_GET_TIMEOUT, PLAYER1_GET_POINT, PLAYER2_GET_POINT, PLAYER1_LOST_POINT, PLAYER2_LOST_POINT, PLAYER1_GET_YELLOW_CARD, PLAYER2_GET_YELLOW_CARD, PLAYER1_GET_RED_CARD, PLAYER2_GET_RED_CARD, PLAYER1_FINISH_TIMEOUT, PLAYER2_FINISH_TIMEOUT, MATCH_RESUMED;
    }

    public static Set<GAME_ACTION> collectedAction = initCollectedAction();

    private static Set<GAME_ACTION> initCollectedAction() {
        return Arrays.asList(PLAYER1_GET_POINT, PLAYER2_GET_POINT, PLAYER1_GET_YELLOW_CARD, PLAYER2_GET_YELLOW_CARD).stream().collect(Collectors.toSet());
    }


    public enum FOOTBAL_ACTION {
        F_STARTED, F_PAUSED, F_RESUMED, F_FINISHED, F_TEAM1_GOAL, F_TEAM2_GOAL, F_TEAM1_YELLOW, F_TEAM1_RED, F_TEAM2_YELLOW, F_TEAM2_RED, F_REVERS, F_TEAM1_FINE, F_TEAM2_FINE
    }

    public static Set<FOOTBAL_ACTION> collectedFootballAction = initCollectedFootBallAction();

    private static Set<FOOTBAL_ACTION> initCollectedFootBallAction() {
        return Arrays.asList(F_TEAM1_GOAL, F_TEAM2_GOAL, F_TEAM1_RED, F_TEAM2_RED).stream().collect(Collectors.toSet());
    }



    public enum POST_DETAIL_INFO {PLAYER1_POINT, PLAYER2_POINT, PLAYER1_UNPOINT, PLAYER2_UNPOINT, NOTHING_CHANGES;}

    public enum HOCKEY_ACTION {
        H_BEGIN, H_STARTED, H_PAUSED, H_RESUMED, H_FINISHED, H_TEAM1_GOAL, H_TEAM2_GOAL, H_TEAM1_YELLOW, H_TEAM1_RED, H_TEAM2_YELLOW, H_TEAM2_RED, H_REVERS, H_TEAM1_FINE, H_TEAM2_FINE
    }

    public static Set<HOCKEY_ACTION> collectedHockeylAction = initCollectedHockeyAction();

    private static Set<HOCKEY_ACTION> initCollectedHockeyAction() {
        return Arrays.asList(H_TEAM1_GOAL, H_TEAM2_GOAL, H_TEAM1_RED, H_TEAM2_RED).stream().collect(Collectors.toSet());
    }

}
