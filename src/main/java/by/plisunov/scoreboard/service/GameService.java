package by.plisunov.scoreboard.service;

import by.plisunov.scoreboard.model.BaseGame;
import by.plisunov.scoreboard.model.Game;
import by.plisunov.util.GameAction;

public interface GameService {

    BaseGame processGame(BaseGame game, GameAction action, Integer minValue, Integer secValue);

}
