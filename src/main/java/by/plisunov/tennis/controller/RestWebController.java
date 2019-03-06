package by.plisunov.tennis.controller;

import by.plisunov.tennis.model.Game;
import by.plisunov.tennis.model.GameSet;
import by.plisunov.tennis.model.Player;
import by.plisunov.tennis.model.TournamentInfo;
import by.plisunov.tennis.model.androiddata.*;
import by.plisunov.tennis.service.GameService;
import by.plisunov.tennis.service.LogTime;
import by.plisunov.tennis.service.SiteContentRepository;
import by.plisunov.tennis.service.SiteContentValueRepository;
import by.plisunov.util.Constants;
import by.plisunov.util.Constants.GAME_ACTION;
import by.plisunov.util.Constants.GameStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static by.plisunov.util.Constants.FOOTBAL_ACTION.F_STARTED;
import static by.plisunov.util.Constants.GameStatus.ACTIVE;
import static by.plisunov.util.Constants.GameStatus.FINISH;
import static by.plisunov.util.Constants.PLAYER1_SCORE;
import static by.plisunov.util.Constants.PLAYER2_SCORE;
import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.singletonList;

/**
 * Basic spring rest controller for REST service
 *
 * @author Andrey
 */
@Controller
@RequestMapping("/")
public class RestWebController {

    // public static Map<String, Game> currentMatches = new HashMap<>();

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private SimpMessagingTemplate webSocket;

    @Autowired
    private GameService gameService;

    @Autowired
    @Qualifier("currentGames")
    private Map<String, Game> currentGames;

    @Autowired
    @Qualifier("futureGames")
    private Map<String, Game> scheduledGames;

    @Autowired
    @Qualifier("lastGames")
    private Map<String, Game> finishedGames;

    @Autowired
    private SiteContentRepository siteContentRepository;

    @Autowired
    private SiteContentValueRepository siteContentValueRepository;


    //TODO
    private FootballGame fGame = new FootballGame();

    private static final Logger logger = LoggerFactory.getLogger(RestWebController.class);

    @LogTime
    @RequestMapping(value = "selectplayers", method = RequestMethod.GET)
    public @ResponseBody
    List<AndroidGameInfo> selectPlayers() throws IOException {
        Date date = Date.from(LocalDateTime.now().atZone(ZoneId.of("Europe/Moscow")).toInstant());
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", new Locale("ru"));
        String today = format.format(date);
        Integer tournamentID = siteContentRepository.getTodaysTournamentHQL(today);
        String value = siteContentValueRepository.getContentValue(tournamentID);
        List<TournamentInfo> games = Arrays.asList(mapper.readValue(value, TournamentInfo[].class));
        games.stream().filter(game -> StringUtils.isEmpty(game.getNotetour()))
                .filter(game -> !scheduledGames.keySet().contains(game.getId()))
                .filter(game -> !finishedGames.keySet().contains(game.getId()))
                .filter(game -> StringUtils.isNotBlank(game.getNotour()))
                .filter(game -> StringUtils.isNotBlank(game.getFirstplayer()))
                .filter(game -> StringUtils.isNotBlank(game.getSecondplayer()))
                .map(game -> new Game(game))
                .collect(Collectors.toList())
                .forEach(game -> scheduledGames.put(game.getMatchId(), game));
        List<AndroidGameInfo> response = scheduledGames.values().stream()
                .map(game -> new AndroidGameInfo(game.getMatchId(), game.getPlayer1().getName(), game.getPlayer2().getName(), game.getMatchTime()))
                .collect(Collectors.toList());
        return response;
    }

    @LogTime
    @RequestMapping(value = "startgame", method = RequestMethod.POST)
    public @ResponseBody
    Game startMatch(@RequestParam(value = "matchId") String matchId, @RequestBody GameStartInfo gameInfo) {
        Game activeGame = currentGames.get(matchId);
        if (activeGame == null) {
            activeGame = scheduledGames.get(matchId);
            scheduledGames.remove(matchId);
            activeGame.setStatus(ACTIVE);
            activeGame.setLastAction(GAME_ACTION.GAME_START);
            activeGame.setCurrentSet(1);
            activeGame.fillGameSets(gameInfo.getFrameCount());
            activeGame.setFirstBraker(gameInfo.getFirstBraker());
            if (gameInfo.getFirstBraker() == 1) {
                activeGame.setBrakePlayer1(1);
                activeGame.setBrakePlayer2(0);
            } else {
                activeGame.setBrakePlayer1(0);
                activeGame.setBrakePlayer2(1);
            }
            activeGame.setLastActionDate(LocalDateTime.now(ZoneId.of("Europe/Moscow")));
            currentGames.put(matchId, activeGame);
        }
        return activeGame;
    }

    @Deprecated
    @SuppressWarnings({"unchecked"})
    @RequestMapping(value = "changegame", method = RequestMethod.POST)
    public void changeScore(@RequestBody final String request)
            throws IOException {
        logger.debug("Request = " + request);
        Map<String, String> mapFromAndroidClient = new HashMap<>();
        mapFromAndroidClient = mapper.readValue(request, mapFromAndroidClient.getClass());
        String matchId = mapFromAndroidClient.get(Constants.MATCH_ID);
        Game currentGame = currentGames.get(matchId);
        gameService.changeScore(currentGame,
                mapFromAndroidClient.get(Constants.PLAYER_NUMBER), mapFromAndroidClient.get(Constants.SET_NUMBER),
                mapFromAndroidClient.get(Constants.TYPE_POINT), mapFromAndroidClient.get(Constants.BRAKE_PLAYER1),
                mapFromAndroidClient.get(Constants.BRAKE_PLAYER2), mapFromAndroidClient.get(PLAYER1_SCORE),
                mapFromAndroidClient.get(PLAYER2_SCORE));

        webSocket.convertAndSend("/results", filterGames());
    }

    @Deprecated
    @SuppressWarnings({"unchecked"})
    @RequestMapping(value = "timeout", method = RequestMethod.POST)
    public void timeout(@RequestBody final String request)
            throws IOException {
        Map<String, String> mapFromAndroidClient = new HashMap<>();
        mapFromAndroidClient = mapper.readValue(request, mapFromAndroidClient.getClass());
        gameService.timeoutManager(currentGames.get(mapFromAndroidClient.get(Constants.MATCH_ID)),
                mapFromAndroidClient.get(Constants.PLAYER_NUMBER), mapFromAndroidClient.get(Constants.TYPE_POINT));
        webSocket.convertAndSend("/results", filterGames());
    }

    @Deprecated
    @SuppressWarnings({"unchecked"})
    @RequestMapping(value = "finishgame", method = RequestMethod.POST)
    public void finishMap(@RequestBody final String request)
            throws IOException {
        Map<String, String> mapFromAndroidClient = new HashMap<>();
        mapFromAndroidClient = mapper.readValue(request, mapFromAndroidClient.getClass());
        String matchId = mapFromAndroidClient.get(Constants.MATCH_ID);
        Game currentGame = currentGames.get(matchId);
        gameService.finishGame(currentGame);
        webSocket.convertAndSend("/results", filterGames());
    }

    @Deprecated
    @SuppressWarnings({"unchecked"})
    @RequestMapping(value = "finishset", method = RequestMethod.POST)
    public void finishSet(@RequestBody final String request)
            throws IOException {
        Map<String, String> mapFromAndroidClient = new HashMap<>();
        mapFromAndroidClient = mapper.readValue(request, mapFromAndroidClient.getClass());
        String matchId = mapFromAndroidClient.get(Constants.MATCH_ID);
        Game currentGame = currentGames.get(matchId);
        gameService.finishSet(currentGame, mapFromAndroidClient.get(Constants.IS_GAME_CONTINIUS),
                mapFromAndroidClient.get(Constants.PLAYER1_SCORE),
                mapFromAndroidClient.get(Constants.PLAYER2_SCORE),
                mapFromAndroidClient.get(Constants.BRAKE_PLAYER1),
                mapFromAndroidClient.get(Constants.BRAKE_PLAYER2));
        webSocket.convertAndSend("/results", filterGames());
    }


    @RequestMapping(value = "test", method = RequestMethod.GET)
    public @ResponseBody
    List<Game> test() {
        webSocket.convertAndSend("/results", filterGames());
        return new ArrayList<>(currentGames.values());
    }

    @RequestMapping(value = "imalive", method = RequestMethod.GET)
    public ResponseEntity imalive(@RequestParam(name = "match_id") String matchID) {
        try {
            currentGames.get(matchID).setLastActionDate(LocalDateTime.now(ZoneId.of("Europe/Moscow")));
        } catch (Exception e) {
            logger.debug("match id = " + matchID + "Exception  = " + e.getCause().toString());
        }
        return ResponseEntity.ok().build();
    }

    @LogTime
    @RequestMapping(value = "api/showgame", method = RequestMethod.GET)
    public @ResponseBody
    Game showGame(@RequestParam("id") String gameId) {
        currentGames.values().stream()
                .map(Game::getAlive)
                .collect(Collectors.toList());
        if (currentGames.containsKey(gameId)) {
            return currentGames.get(gameId);
        }
        return null;
    }


    @LogTime
    @RequestMapping(value = "api/scheduler", method = RequestMethod.GET)
    public @ResponseBody
    List<Game> getScheduler() {
        return CollectionUtils.isEmpty(scheduledGames) ? EMPTY_LIST : new ArrayList<>(scheduledGames.values());
    }

    @LogTime
    @RequestMapping(value = "api/showallgames", method = RequestMethod.GET)
    public @ResponseBody
    List<Game> showAllGames(@RequestParam(required = false, name = "scheduled") Boolean scheduled) {
        scheduled = scheduled == null ? true : false;
        List<Game> result = currentGames.values().stream()
                .filter(Game::getTime)
                .collect(Collectors.toList());
        result.forEach(s -> currentGames.remove(s.getMatchId()));
        currentGames.values().stream()
                .map(Game::getAlive)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(currentGames.values()) && !scheduled) {
            return EMPTY_LIST;
        }
        return Stream.of(currentGames.values(), scheduledGames.values()).collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll)
                .stream().map(o -> (Game) o).sorted((o1, o2) -> o1.getStatus().compareTo(o2.getStatus())).collect(Collectors.toList());
        //new ArrayList<>(currentGames.values(), scheduledGames.v);
    }

    private List<Game> fillTestData() {
        //Just for test
        Game game1 = new Game();
        game1.setMatchId("test_game1");
        game1.setLastActionDate(LocalDateTime.now(ZoneId.of("Europe/Moscow")));
        Player player1test = new Player();
        player1test.setName("TestPlayer1");
        Player player2test = new Player();
        player2test.setName("TestPlayer2");
        game1.setPlayer1(player1test);
        game1.setPlayer2(player2test);
        game1.setCurrentSet(1);
        game1.setBrakePlayer2(2);
        game1.setBrakePlayer1(0);
        game1.setLastAction(GAME_ACTION.PLAYER1_GET_RED_CARD);
        game1.setAlive(true);
        game1.setTimeoutPlayer1(false);
        game1.setTimeoutPlayer2(false);
        game1.setFrameCount(5);
        game1.setStatus(ACTIVE);
        game1.setPlayer1Score(0);
        game1.setPlayer2Score(0);
        game1.setPlayer1Yellow(true);
        game1.setSets(new ArrayList<>());
        GameSet set1 = new GameSet(game1, 0);
        set1.setScore1(2);
        set1.setScore2(4);
        game1.getSets().add(set1);
        GameSet set2 = new GameSet(game1, 1);
        game1.getSets().add(set2);
        GameSet set3 = new GameSet(game1, 2);
        game1.getSets().add(set3);
        GameSet set4 = new GameSet(game1, 3);
        game1.getSets().add(set4);
        GameSet set5 = new GameSet(game1, 4);
        game1.getSets().add(set5);
        game1.setSaved(true);
        currentGames.put(game1.getMatchId(), game1);


        Game game12 = new Game();
        game12.setMatchId("test_game2");
        game12.setLastActionDate(LocalDateTime.now(ZoneId.of("Europe/Moscow")));
        Player player12test = new Player();
        player12test.setName("TestPlayer12");
        Player player22test = new Player();
        player22test.setName("TestPlayer22");
        game12.setPlayer1(player12test);
        game12.setPlayer2(player22test);
        game12.setCurrentSet(3);
        game12.setBrakePlayer2(1);
        game12.setBrakePlayer1(0);
        game12.setLastAction(GAME_ACTION.PLAYER1_GET_RED_CARD);
        game12.setSaved(true);
        game12.setTimeoutPlayer1(false);
        game12.setTimeoutPlayer2(false);
        game12.setFrameCount(5);
        game12.setStatus(ACTIVE);
        game12.setPlayer1Score(0);
        game12.setPlayer2Score(0);
        game12.setPlayer1Yellow(true);
        game12.setSets(new ArrayList<>());
        GameSet set12 = new GameSet(game12, 0);
        set12.setScore1(2);
        set12.setScore2(4);
        game12.getSets().add(set12);
        GameSet set22 = new GameSet(game12, 1);
        set22.setScore1(4);
        set22.setScore2(7);
        game12.getSets().add(set22);
        GameSet set23 = new GameSet(game12, 2);
        set23.setScore1(5);
        set23.setScore2(4);
        game12.getSets().add(set23);
        GameSet set24 = new GameSet(game12, 3);
        game12.getSets().add(set24);
        GameSet set25 = new GameSet(game12, 4);
        game12.getSets().add(set25);
        currentGames.put(game12.getMatchId(), game12);
        return new ArrayList<>(currentGames.values());
    }


    @RequestMapping(value = "api/football/showgames", method = RequestMethod.GET)
    public @ResponseBody
    List<FootballGame> showAllFootBallGames() {
        if (fGame.getStatus() != null) {
            long totalMins = ChronoUnit.MINUTES.between(fGame.getLastActionDate(), LocalDateTime.now(ZoneId.of("Europe/Moscow")));
            if (FINISH == fGame.getStatus() && totalMins > 10) {
                fGame = new FootballGame();
            }
            return Arrays.asList(fGame);
        } else {
            return EMPTY_LIST;
        }
    }

    @Deprecated
    @RequestMapping(value = "pausegame", method = RequestMethod.GET)
    public @ResponseBody
    void pauseGame(@RequestParam("id") String gameId) {
        Game game = currentGames.get(gameId);
        if (GameStatus.ACTIVE.equals(game.getStatus())) {
            game.setStatus(GameStatus.PAUSED);
            game.setLastAction(GAME_ACTION.MATCH_PAUSED);
        } else {
            game.setStatus(GameStatus.ACTIVE);
            game.setLastAction(GAME_ACTION.MATCH_RESUMED);
        }
    }


    @RequestMapping(value = "football/gameProcess")
    public @ResponseBody
    FootballGame footballProcess(@RequestBody AndroidFootBallRequest request) {
        if (F_STARTED == request.getAction()) {
            fGame.setTeam1(request.getTeam1());
            fGame.setTeam2(request.getTeam2());
        }
        gameService.processFootBall(fGame, request.getAction());
        return fGame;
    }


    @LogTime
    @RequestMapping(value = "gameProcess", method = RequestMethod.POST)
    public @ResponseBody
    AndroidGame gameProcess(@RequestParam(name = "matchid") String matchId, @RequestBody AndroidRequest request) {
        Game processedGame = gameService.processGame(currentGames.get(matchId), request.getAction());
        currentGames.put(matchId, processedGame);
        if (FINISH == processedGame.getStatus()) {
            finishedGames.put(processedGame.getMatchId(), processedGame);
        }
        return convertGameToAndroidGame(processedGame);
    }

    private AndroidGame convertGameToAndroidGame(Game processedGame) {
        return AndroidGame.builder()
                .brake1(processedGame.getBrakePlayer1())
                .brake2(processedGame.getBrakePlayer2())
                .hasYellow1(processedGame.isPlayer1Yellow())
                .hasYellow2(processedGame.isPlayer2Yellow())
                .score1(processedGame.getPlayer1Score())
                .score2(processedGame.getPlayer2Score())
                .setNumber(processedGame.getCurrentSet())
                .setScore1(processedGame.getSets().get(processedGame.getCurrentSet() - 1).getScore1())
                .setScore2(processedGame.getSets().get(processedGame.getCurrentSet() - 1).getScore2())
                .lastaction(processedGame.getLastAction())
                .additionalInfo(generateScoreInfo(processedGame.getSets(), processedGame.getCurrentSet()))
                .build();
    }

    private String generateScoreInfo(List<GameSet> sets, int currentSet) {
        StringBuilder sBuilder = new StringBuilder();
        for (int i = 0; i < currentSet; i++) {
            GameSet set = sets.get(i);
            sBuilder.append(set.getScore1())
                    .append(":")
                    .append(set.getScore2())
                    .append(" ");
        }
        return sBuilder.toString();
    }


    @MessageMapping("/showallgames")
    @SendTo("/results/score")
    public List<Game> getLiveScorring() {
        logger.debug("Start check games");
        List<Game> result = currentGames.values().stream()
                .filter(Game::getTime)
                .collect(Collectors.toList());
        result.forEach(s -> currentGames.remove(s.getMatchId()));
        currentGames.values().stream()
                .map(Game::getAlive)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(currentGames.values())) {
            return EMPTY_LIST;
        }
        return new ArrayList<>(currentGames.values());
    }

    @MessageMapping("/showgame")
    @SendTo("/results/score")
    public List<Game> getLiveScorringForCurrentGame(Game currentGame) {
        currentGames.values().stream()
                .map(Game::getAlive)
                .collect(Collectors.toList());
        return currentGames.get(currentGame.getMatchId()) != null ? singletonList(currentGames.get(currentGame.getMatchId())) : EMPTY_LIST;
    }

    private List<Game> filterGames() {
        List<Game> result = currentGames.values().stream()
                .filter(Game::getTime)
                .collect(Collectors.toList());
        result.forEach(s -> currentGames.remove(s.getMatchId()));
        currentGames.values().stream()
                .map(Game::getAlive)
                .collect(Collectors.toList());
        return new ArrayList<>(currentGames.values());
    }
}