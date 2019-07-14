package by.plisunov.scoreboard.service.impl;

import by.plisunov.scoreboard.model.BaseGame;
import by.plisunov.scoreboard.service.GameService;
import by.plisunov.util.GameAction;
import org.springframework.stereotype.Service;

@Service
public class GameServiceImpl implements GameService {


    @Override
    public BaseGame processGame(BaseGame game, GameAction action, Integer minValue, Integer secValue) {
        return null;
    }
}
