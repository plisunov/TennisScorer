package by.plisunov.tennis.service;

import by.plisunov.tennis.model.Game;
import by.plisunov.tennis.model.androiddata.FootballGame;
import by.plisunov.util.Constants;

public interface GameService {

	void changeScore(Game game, String player, String set, String type, String brake1, String brake2, String score1, String Score2);

	void timeoutManager(Game game, String player, String type);

	void finishGame(Game currentGame);

	void finishSet(Game currentGame, String isContinius, String player1Score, String player2Score, String player1Brake, String player2Brake);

	void getAlive(Game game);

	Game processGame(Game game, Constants.GAME_ACTION action);

    FootballGame processFootBall(FootballGame fGame, Constants.FOOTBAL_ACTION action);
}
