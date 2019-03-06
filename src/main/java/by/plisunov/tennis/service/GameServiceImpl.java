package by.plisunov.tennis.service;

import by.plisunov.tennis.model.FootballGameSnapshot;
import by.plisunov.tennis.model.Game;
import by.plisunov.tennis.model.GameSet;
import by.plisunov.tennis.model.GameSnapshot;
import by.plisunov.tennis.model.androiddata.FootballGame;
import by.plisunov.util.Constants;
import by.plisunov.util.Constants.FOOTBAL_ACTION;
import by.plisunov.util.Constants.GAME_ACTION;
import by.plisunov.util.Constants.GameStatus;
import by.plisunov.util.Constants.SCORE_CHANGE_TYPE;
import org.apache.commons.lang3.text.StrBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import static by.plisunov.util.Constants.GAME_ACTION.MATCH_ENDED;
import static by.plisunov.util.Constants.GAME_ACTION.SET_ENDED;
import static by.plisunov.util.Constants.GameStatus.*;
import static by.plisunov.util.Constants.collectedAction;
import static by.plisunov.util.Constants.collectedFootballAction;

@Service
public class GameServiceImpl implements GameService {

    @Override
    public void changeScore(Game game, String player, String set, String type, String brake1, String brake2, String score1, String score2) {
        GameSet currentSet = game.getSets().get(Integer.parseInt(set) - 1);
        game.setCurrentSet(currentSet.getSetNumber());
        game.setPlayer1Score(Integer.parseInt(score1));
        game.setPlayer2Score(Integer.parseInt(score2));
        if ("1".equals(player)) {
            game.setPlayer1Yellow(false);
            game.setPlayer2Yellow(false);
        } else {
            game.setPlayer1Red(false);
            game.setPlayer2Red(false);
        }
        if (SCORE_CHANGE_TYPE.ADD_POINT.toString().equals(type)) {
            if ("1".equals(player)) {
                currentSet.setScore1(currentSet.getScore1() + 1);
                game.setLastAction(GAME_ACTION.PLAYER1_GET_POINT);
                //  generateDetails(game, currentSet, true, true);
            } else {
                currentSet.setScore2(currentSet.getScore2() + 1);
                game.setLastAction(GAME_ACTION.PLAYER2_GET_POINT);
                //generateDetails(game, currentSet, false, true);
            }
        } else if (SCORE_CHANGE_TYPE.REVERSE_POINT.toString().equals(type)) {
            if ("1".equals(player)) {
                currentSet.setScore1(currentSet.getScore1() - 1);
                game.setLastAction(GAME_ACTION.PLAYER1_LOST_POINT);
                //generateDetails(game, currentSet, true, false);
            } else {
                currentSet.setScore2(currentSet.getScore2() - 1);
                game.setLastAction(GAME_ACTION.PLAYER2_LOST_POINT);
                //generateDetails(game, currentSet, false, false);
            }
        } else if (SCORE_CHANGE_TYPE.RED_CARD.toString().equals(type)) {
            if ("1".equals(player)) {
                currentSet.setScore1(currentSet.getScore1() + 1);
                game.setLastAction(GAME_ACTION.PLAYER2_GET_RED_CARD);
                game.setPlayer2Red(true);
                game.setBrakePlayer2(Integer.parseInt(brake2));
                //generateDetails(game, currentSet, true, true);
            } else {
                currentSet.setScore2(currentSet.getScore2() + 1);
                game.setLastAction(GAME_ACTION.PLAYER1_GET_RED_CARD);
                game.setPlayer1Red(true);
                game.setPlayer1FallsCount(game.getPlayer1FallsCount() + 1);
                //generateDetails(game, currentSet, false, true);
            }
        } else if (SCORE_CHANGE_TYPE.YELLOW_CARD.toString().equals(type)) {
            if ("1".equals(player)) {
                game.setLastAction(GAME_ACTION.PLAYER1_GET_YELLOW_CARD);
                game.setPlayer1Yellow(true);
                game.setPlayer1FallsCount(game.getPlayer1FallsCount() + 1);
            } else {
                game.setLastAction(GAME_ACTION.PLAYER2_GET_YELLOW_CARD);
                game.setPlayer2Yellow(true);
                game.setBrakePlayer2(Integer.parseInt(brake2));
            }
        }
        game.setBrakePlayer1(Integer.parseInt(brake1));
        game.setBrakePlayer2(Integer.parseInt(brake2));
        //   generateGameResultContent(game);
    }

    /*private void generateDetails(Game game, GameSet currentSet, boolean isFirstPlayer, boolean isAddPoint) {
        SimpleDateFormat format = new SimpleDateFormat("hh:mm", new Locale("ru"));
        Long postId = Long.valueOf(game.getMatchId());
        WPPostContent counterContent = wpPostContentRepository.getWPPostContentByPostIdAndMetaKey(postId, COUNTER_KEY);
        int counterKey = Integer.parseInt(counterContent.getMetaValue());
        WPPostContent timePostContent = WPPostContent.builder()
                .postId(postId)
                .id(null)
                .metaKey(MessageFormat.format(DETAILS_TIME_FIELD, counterKey))
                .metaValue(format.format(new Date()))
                .build();
        wpPostContentRepository.save(timePostContent);
        WPPostContent player1ScoreContent = WPPostContent.builder()
                .id(null)
                .postId(postId)
                .metaKey(MessageFormat.format(DETAILS_PLAYER1_SCORE_FIELD, counterKey))
                .metaValue(isFirstPlayer ? isAddPoint ? "1" : "-1" : "")
                .build();
        wpPostContentRepository.save(player1ScoreContent);
        WPPostContent player2ScoreContent = WPPostContent.builder()
                .id(null)
                .postId(postId)
                .metaKey(MessageFormat.format(DETAILS_PLAYER2_SCORE_FIELD, counterKey))
                .metaValue(isFirstPlayer ? "" : isAddPoint ? "1" : "-1")
                .build();
        wpPostContentRepository.save(player2ScoreContent);
        StrBuilder setScore = new StrBuilder()
                .append(String.valueOf(currentSet.getScore1()))
                .append(":")
                .append(String.valueOf(currentSet.getScore2()))
                .append(" (")
                .append(String.valueOf(game.getPlayer1Score()))
                .append(":")
                .append(String.valueOf(game.getPlayer2Score()))
                .append(")");
        WPPostContent setScoreContent = WPPostContent.builder()
                .id(null)
                .postId(postId)
                .metaKey(MessageFormat.format(DETAILS_TOTAL_SCORE_FIELD, counterKey))
                .metaValue(setScore.toString())
                .build();
        wpPostContentRepository.save(setScoreContent);
        counterContent.setMetaValue(String.valueOf(counterKey + 1));
        wpPostContentRepository.save(counterContent);
    }*/

    @Override
    public void timeoutManager(Game game, String player, String type) {
        if (Constants.START_TO.equals(type)) {
            if ("1".equals(player)) {
                game.setTimeoutPlayer1(true);
                game.setLastAction(GAME_ACTION.PLAYER1_GET_TIMEOUT);
            } else {
                game.setTimeoutPlayer2(true);
                game.setLastAction(GAME_ACTION.PLAYER2_GET_TIMEOUT);
            }
        } else {
            if ("1".equals(player)) {
                game.setTimeoutPlayer1(false);
                game.setLastAction(GAME_ACTION.PLAYER1_FINISH_TIMEOUT);
            } else {
                game.setTimeoutPlayer2(false);
                game.setLastAction(GAME_ACTION.PLAYER2_FINISH_TIMEOUT);
            }
        }

    }

    @Override
    public void finishGame(Game currentGame) {
        currentGame.setStatus(GameStatus.FINISH);
        currentGame.setLastActionDate(LocalDateTime.now(ZoneId.of("Europe/Moscow")));
        currentGame.setLastAction(GAME_ACTION.MATCH_ENDED);
    }

    @Override
    public void finishSet(Game currentGame, String isContinius, String player1Score, String player2Score, String player1Brake, String player2Brake) {
        if ("true".equals(isContinius)) {
            currentGame.setCurrentSet(currentGame.getCurrentSet() + 1);
        }
        currentGame.setPlayer1Score(Integer.parseInt(player1Score));
        currentGame.setPlayer2Score(Integer.parseInt(player2Score));
        currentGame.setBrakePlayer1(Integer.parseInt(player1Brake));
        currentGame.setBrakePlayer2(Integer.parseInt(player2Brake));
        currentGame.setLastAction(GAME_ACTION.SET_ENDED);
    }

    @Override
    public void getAlive(Game game) {
        if (game.getLastActionDate() != null) {
            if (ChronoUnit.SECONDS.between(game.getLastActionDate(), LocalDateTime.now(ZoneId.of("Europe/Moscow"))) >= 6) {
                game.setAlive(false);
            }
        }
        game.setAlive(true);
    }

    @Override
    public Game processGame(Game game, GAME_ACTION action) {
        if (collectedAction.contains(action)) {
            createSnapshot(game);
        }
        int score1 = 0;
        int score2 = 0;
        game.setPlayer2Red(false);
        game.setPlayer1Red(false);
        setLastAction(game, action);
        switch (action) {
            case PLAYER1_GET_POINT:
                score1 = game.getSets().get(game.getCurrentSet() - 1).getScore1();
                game.getSets().get(game.getCurrentSet() - 1).setScore1(score1 + 1);
                changeBrake(game, checkFinishSet(game));
                checkFinishGame(game);
                break;
            case PLAYER2_GET_POINT:
                score2 = game.getSets().get(game.getCurrentSet() - 1).getScore2();
                game.getSets().get(game.getCurrentSet() - 1).setScore2(score2 + 1);
                changeBrake(game, checkFinishSet(game));
                checkFinishGame(game);
                break;
            case PLAYER1_GET_YELLOW_CARD:
                boolean player1Yellow = game.isPlayer1Yellow();
                if (player1Yellow) {
                    game.setPlayer1Yellow(false);
                    score2 = game.getSets().get(game.getCurrentSet() - 1).getScore2();
                    game.getSets().get(game.getCurrentSet() - 1).setScore2(score2 + 1);
                    checkFinishSet(game);
                    checkFinishGame(game);
                }
                game.setPlayer1Yellow(player1Yellow ? false : true);
                break;
            case PLAYER2_GET_YELLOW_CARD:
                boolean player2Yellow = game.isPlayer2Yellow();
                if (player2Yellow) {
                    game.setPlayer2Yellow(false);
                    score1 = game.getSets().get(game.getCurrentSet() - 1).getScore1();
                    game.getSets().get(game.getCurrentSet() - 1).setScore1(score1 + 1);
                    checkFinishSet(game);
                    checkFinishGame(game);
                }
                game.setPlayer2Yellow(player2Yellow ? false : true);
                break;
            case PLAYER1_GET_RED_CARD:
                score2 = game.getSets().get(game.getCurrentSet() - 1).getScore2();
                game.getSets().get(game.getCurrentSet() - 1).setScore2(score2 + 1);
                game.setPlayer1Red(true);
                checkFinishSet(game);
                checkFinishGame(game);
                break;
            case PLAYER2_GET_RED_CARD:
                score1 = game.getSets().get(game.getCurrentSet() - 1).getScore1();
                game.getSets().get(game.getCurrentSet() - 1).setScore1(score1 + 1);
                game.setPlayer2Red(true);
                checkFinishSet(game);
                checkFinishGame(game);
                break;
            case PLAYER1_GET_TIMEOUT:
                game.setTimeoutPlayer1(game.isTimeoutPlayer1() ? false : true);
                break;
            case PLAYER2_GET_TIMEOUT:
                game.setTimeoutPlayer2(game.isTimeoutPlayer2() ? false : true);
                break;
            case MATCH_PAUSED:
                game.setStatus(ACTIVE == game.getStatus() ? GameStatus.PAUSED : ACTIVE);
                break;
            case GAME_REVERSED:
                getLastSnapshot(game);
                break;
            case REVERSE_BRAKER:
                game.setFirstBraker(game.getFirstBraker() == 1 ? 2 : 1);
                int tempBrake = game.getBrakePlayer2();
                game.setBrakePlayer2(game.getBrakePlayer1());
                game.setBrakePlayer1(tempBrake);
                break;
        }
        return game;
    }

    @Override
    public FootballGame processFootBall(FootballGame fGame, FOOTBAL_ACTION action) {
        fGame.setLastActionDate(LocalDateTime.now(ZoneId.of("Europe/Moscow")));
        if (collectedFootballAction.contains(action)) {
            createFootBallSnapshot(fGame);
        }
        switch (action) {
            case F_STARTED:
                fGame.setCount1(4);
                fGame.setCount2(4);
                fGame.setScore1(0);
                fGame.setScore2(0);
                fGame.setStatus(ACTIVE);
                break;
            case F_PAUSED:
                if (PAUSED == fGame.getStatus()) {
                    fGame.setStatus(ACTIVE);
                } else {
                    fGame.setStatus(PAUSED);
                }
                break;
            case F_TEAM1_GOAL:
                fGame.setScore1(fGame.getScore1() + 1);
                fGame.setCount2(fGame.getCount2() - 1);
                if (fGame.getCount2() == 0) {
                    fGame.setStatus(POSSIBLE_FINISH);
                }
                break;
            case F_TEAM2_GOAL:
                fGame.setScore2(fGame.getScore2() + 1);
                fGame.setCount1(fGame.getCount1() - 1);
                if (fGame.getCount1() == 0) {
                    fGame.setStatus(POSSIBLE_FINISH);
                }
                break;
            case F_TEAM1_RED:
                fGame.setCount1(fGame.getCount1() - 1);
                if (fGame.getCount1() == 0) {
                    fGame.setStatus(POSSIBLE_FINISH);
                }
                break;
            case F_TEAM2_RED:
                fGame.setCount2(fGame.getCount2() - 1);
                if (fGame.getCount2() == 0) {
                    fGame.setStatus(POSSIBLE_FINISH);
                }
                break;
            case F_REVERS:
                getLastFootBallSnapshot(fGame);
                break;
            case F_FINISHED:
                fGame.setStatus(FINISH);
                break;
        }

        return fGame;
    }

    private void createFootBallSnapshot(FootballGame fGame) {
        FootballGameSnapshot snapshot = FootballGameSnapshot.builder()
                .count1(fGame.getCount1())
                .count2(fGame.getCount2())
                .score1(fGame.getScore1())
                .score2(fGame.getScore2())
                .timeSnapshot(fGame.getTimeSnapshot())
                .build();
        fGame.getSnapshots().addLast(snapshot);
    }

    private void getLastFootBallSnapshot(FootballGame fGame) {
        if (!CollectionUtils.isEmpty(fGame.getSnapshots())) {
            FootballGameSnapshot snapshot = fGame.getSnapshots().getLast();
            fGame.setCount2(snapshot.getCount2());
            fGame.setScore2(snapshot.getScore2());
            fGame.setScore1(snapshot.getScore1());
            fGame.setCount1(snapshot.getCount1());
            fGame.setTimeSnapshot(snapshot.getTimeSnapshot());
            fGame.getSnapshots().removeLast();
        }
    }

    private void checkFinishGame(Game game) {
        int totalScore1 = game.getPlayer1Score();
        int totalScore2 = game.getPlayer2Score();
        int totalFrames = game.getFrameCount();
        if (totalScore1 > totalFrames / 2 || totalScore2 > totalFrames / 2) {
            game.setStatus(GameStatus.FINISH);
            setLastAction(game, MATCH_ENDED);
        }
    }

    private void changeBrake(Game game, boolean isSetFinished) {
        int player1Brake = game.getBrakePlayer1();
        int player2Brake = game.getBrakePlayer2();
        int firstBraker = game.getFirstBraker();
        int setNumber = game.getCurrentSet();
        boolean isCornerScore = (game.getSets().get(setNumber - 1).getScore1() >= 10 && game.getSets().get(setNumber - 1).getScore2() >= 10);
        if (!isSetFinished) {
            if (player1Brake == 1) {
                if (isCornerScore) {
                    game.setBrakePlayer1(0);
                    game.setBrakePlayer2(1);
                } else {
                    game.setBrakePlayer1(2);
                }
            }
            if (player1Brake == 2) {
                game.setBrakePlayer2(1);
                game.setBrakePlayer1(0);
            }
            if (player2Brake == 1) {
                if (isCornerScore) {
                    game.setBrakePlayer1(1);
                    game.setBrakePlayer2(0);
                } else {
                    game.setBrakePlayer2(2);
                }
            }
            if (player2Brake == 2) {
                game.setBrakePlayer1(1);
                game.setBrakePlayer2(0);
            }
        } else {
            if (setNumber % 2 > 0) {
                if (firstBraker == 1) {
                    game.setBrakePlayer1(1);
                    game.setBrakePlayer2(0);
                } else {
                    game.setBrakePlayer1(0);
                    game.setBrakePlayer2(1);
                }
            } else {
                if (firstBraker == 1) {
                    game.setBrakePlayer1(0);
                    game.setBrakePlayer2(1);
                } else {
                    game.setBrakePlayer1(1);
                    game.setBrakePlayer2(0);
                }
            }
        }
    }

    private boolean checkFinishSet(Game game) {
        int player1Score = game.getSets().get(game.getCurrentSet() - 1).getScore1();
        int player2Score = game.getSets().get(game.getCurrentSet() - 1).getScore2();
        if (game.getFrameCount() == 5) {
            if (player1Score >= 11 && player1Score > player2Score + 1) {
                game.setCurrentSet(game.getCurrentSet() + 1);
                game.setPlayer1Score(game.getPlayer1Score() + 1);
                setLastAction(game, SET_ENDED);
                return true;
            }
            if (player2Score >= 11 && player2Score > player1Score + 1) {
                game.setCurrentSet(game.getCurrentSet() + 1);
                game.setPlayer2Score(game.getPlayer2Score() + 1);
                setLastAction(game, SET_ENDED);
                return true;
            }
        } else {
            if (player1Score == 7) {
                game.setCurrentSet(game.getCurrentSet() + 1);
                game.setPlayer1Score(game.getPlayer1Score() + 1);
                setLastAction(game, SET_ENDED);
                return true;
            }
            if (player2Score == 7) {
                game.setCurrentSet(game.getCurrentSet() + 1);
                game.setPlayer2Score(game.getPlayer2Score() + 1);
                setLastAction(game, SET_ENDED);
                return true;
            }
        }
        return false;
    }

    private void setLastAction(Game game, GAME_ACTION action) {
        game.setLastAction(action);
        game.setLastActionDate(LocalDateTime.now(ZoneId.of("Europe/Moscow")));
    }


    private void createSnapshot(Game game) {
        GameSet currentSet = game.getSets().get(game.getCurrentSet() - 1);
        GameSnapshot snapshot = GameSnapshot.builder().gameId(game.getMatchId())
                .brake1(game.getBrakePlayer1())
                .brake2(game.getBrakePlayer2())
                .hasYellow1(game.isPlayer1Yellow())
                .hasYellow2(game.isPlayer2Yellow())
                .score1(game.getPlayer1Score())
                .score2(game.getPlayer2Score())
                .setScore1(currentSet.getScore1())
                .setScore2(currentSet.getScore2())
                .setNumber(game.getCurrentSet())
                .actionTime(Date.from(LocalDateTime.now().atZone(ZoneId.of("Europe/Moscow")).toInstant()))
                .build();
        String.join("", "");
        game.getSnapshots().addLast(snapshot);
    }

    private void getLastSnapshot(Game game) {
        if (!CollectionUtils.isEmpty(game.getSnapshots())) {
            GameSnapshot snapshot = game.getSnapshots().getLast();
            game.setBrakePlayer1(snapshot.getBrake1());
            game.setBrakePlayer2(snapshot.getBrake2());
            game.setPlayer1Score(snapshot.getScore1());
            game.setPlayer2Score(snapshot.getScore2());
            game.setCurrentSet(snapshot.getSetNumber());
            game.getSets().get(snapshot.getSetNumber() - 1).setScore1(snapshot.getSetScore1());
            game.getSets().get(snapshot.getSetNumber() - 1).setScore2(snapshot.getSetScore2());
            game.setPlayer1Yellow(snapshot.isHasYellow1());
            game.setPlayer2Yellow(snapshot.isHasYellow2());
            game.getSnapshots().removeLast();
        }
    }

    /*private void generateGameResultContent(Game currentGame) {
        WPPostContent results = wpPostContentRepository.getWPPostContentByPostIdAndMetaKey(Long.valueOf(currentGame.getMatchId()), RESULTS_KEY);
        List<WPPostContent> players = wpPostContentRepository.getAllWPPostContentByPostIdAndMetaKeyAndMetaValueNotLike(Long.valueOf(currentGame.getMatchId()), PLAYERS_KEY, "0");
        String player1 = players.get(0).getMetaValue();
        String player1Name = wpPostRepository.getWPPostById(Long.valueOf(player1)).getPostTitle();
        String player2 = players.get(1).getMetaValue();
        String player2Name = wpPostRepository.getWPPostById(Long.valueOf(player2)).getPostTitle();
        List<WPPostContent> playersOrder = wpPostContentRepository.getAllWPPostContentByPostIdAndMetaKeyAndMetaValueNotLike(Long.valueOf(currentGame.getMatchId()), ORDERS_KEY, "0");
        if (!CollectionUtils.isEmpty(playersOrder)) {
            String ordering = playersOrder.get(0).getMetaValue();
            if (ordering.indexOf(players.get(0).getMetaValue()) > ordering.indexOf(players.get(1).getMetaValue())) {
                String tmp = player1Name;
                player1Name = player2Name;
                player2Name = tmp;
            }
        }
        StrBuilder resultBuilder = new StrBuilder();
        resultBuilder.append("a:2:{i:")
                .append(player1)
                .append(";a:1:{s:4:\"sets\";s:1:\"")
                .append(String.valueOf(currentGame.getPlayer1Score()))
                .append("\";}i:")
                .append(player2)
                .append(";a:1:{s:4:\"sets\";s:1:\"")
                .append(String.valueOf(currentGame.getPlayer2Score()))
                .append("\";}}");
        results.setMetaValue(resultBuilder.toString());
        wpPostContentRepository.save(results);
        WPPostContent details = wpPostContentRepository.getWPPostContentByPostIdAndMetaKey(Long.valueOf(currentGame.getMatchId()), DETAILS_KEY);
        StrBuilder detailsBuilder = new StrBuilder();
        String scoreString = getSetScores(currentGame);
        detailsBuilder.append("a:6:{s:4:\"time\";s:0:\"\";s:7:\"meeting\";s:0:\"\";s:9:\"playerone\";s:")
                .append(String.valueOf(2 * player1Name.length() - 1))
                .append(":\"")
                .append(player1Name)
                .append("\";s:9:\"playertwo\";s:")
                .append(String.valueOf(2 * player2Name.length() - 1))
                .append(":\"")
                .append(player2Name)
                .append("\";s:8:\"invoices\";s:")
                .append(String.valueOf(scoreString.length()))
                .append(":\"")
                .append(scoreString)
                .append("\";s:7:\"parties\";s:3:\"")
                .append(String.valueOf(currentGame.getPlayer1Score()))
                .append(":")
                .append(String.valueOf(currentGame.getPlayer2Score()))
                .append("\";}");
        details.setMetaValue(detailsBuilder.toString());
        wpPostContentRepository.save(details);
    }*/

    private String getSetScores(Game currentGame) {
        List<GameSet> sets = currentGame.getSets();
        StrBuilder setScore = new StrBuilder();
        for (int i = 0; i < currentGame.getCurrentSet(); i++) {
            setScore.append(String.valueOf(sets.get(i).getScore1()))
                    .append(":")
                    .append(String.valueOf(sets.get(i).getScore2()))
                    .append("; ");
        }
        return setScore.toString();
    }
}
