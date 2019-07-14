package by.plisunov.scoreboard.service.impl;

import by.plisunov.scoreboard.model.BaseGame;
import by.plisunov.scoreboard.model.HockeyGame;
import by.plisunov.scoreboard.service.GameService;
import by.plisunov.util.Constants;
import by.plisunov.util.GameAction;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static by.plisunov.util.GameAction.actionsForHockeyGameSnapshot;

@Service
public class HockeyGameServiceImpl implements GameService {

    @Override
    public BaseGame processGame(BaseGame baseGame,
                                GameAction action,
                                Integer minValue,
                                Integer secValue) {
        HockeyGame game = ((HockeyGame) baseGame);
        if (actionsForHockeyGameSnapshot().contains(action)) {
            createHockeySnapshot(game);
        }
        if (minValue != null && secValue != null) {
            game.setElapsedMinutes(minValue.intValue());
            game.setElapsedSeconds(secValue.intValue());
        }
        game.setLastAction(action);
        game.setLastActionDate(LocalDateTime.now(ZoneId.of("Europe/Moscow")));
        switch (action) {
            case GAME_STARTED:
                game.setStatus(Constants.GameStatus.ACTIVE);
                break;
            case GAME_RESUMED:
                game.setStatus(Constants.GameStatus.ACTIVE);
                break;
            case GAME_PAUSED:
                game.setStatus(Constants.GameStatus.PAUSED);
                break;
        }
        return game;
    }

    private void createHockeySnapshot(HockeyGame game) {
    }
}
