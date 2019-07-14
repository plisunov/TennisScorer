package by.plisunov.scoreboard.controller;

import by.plisunov.scoreboard.model.BaseGame;
import by.plisunov.scoreboard.model.Game;
import by.plisunov.scoreboard.model.HockeyGame;
import by.plisunov.scoreboard.model.TournamentInfo;
import by.plisunov.scoreboard.model.androiddata.AndroidGame;
import by.plisunov.scoreboard.model.androiddata.AndroidGameInfo;
import by.plisunov.scoreboard.service.GameService;
import by.plisunov.scoreboard.service.LogTime;
import by.plisunov.scoreboard.service.SiteContentRepository;
import by.plisunov.scoreboard.service.SiteContentValueRepository;
import by.plisunov.util.GameAction;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static by.plisunov.util.Constants.GameStatus.SCHEDULED;
import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.singletonList;

/**
 * Basic spring rest controller for REST service
 *
 * @author Andrey
 */
@Controller
//TODO base controller should be mapped with "/" endpoint. All specific games should be extend base controller
@RequestMapping("/hockey")
public class RestWebController {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private SimpMessagingTemplate webSocket;

    @Autowired
    @Qualifier("hockeyGameServiceImpl")
    private GameService gameService;

    @Autowired
    @Qualifier("currentGames")
    private Map<String, BaseGame> currentGames;

    @Autowired
    @Qualifier("futureGames")
    private Map<String, BaseGame> scheduledGames;

    @Autowired
    @Qualifier("lastGames")
    private Map<String, BaseGame> finishedGames;

    @Autowired
    private SiteContentRepository siteContentRepository;

    @Autowired
    private SiteContentValueRepository siteContentValueRepository;

    private static final Logger logger = LoggerFactory.getLogger(RestWebController.class);

    @LogTime
    @GetMapping(value = "selectplayers")
    @ResponseBody
    public ResponseEntity<List<AndroidGameInfo>> selectPlayers() throws IOException {
        Date date = DateUtils.addHours(new Date(), 3);
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
                .map(game -> new HockeyGame(game))
                .collect(Collectors.toList())
                .forEach(game -> scheduledGames.put(game.getGameId(), game));
        List<AndroidGameInfo> response = scheduledGames.values().stream()
                .map(game -> new AndroidGameInfo(game.getGameId(), game.getTeamPlayer1().getName(), game.getTeamPlayer2().getName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(response);
    }

    @LogTime
    @GetMapping(value = "startgame")
    @ResponseBody
    public ResponseEntity<AndroidGame> startMatch(@RequestParam(value = "matchId") String matchId) {
        BaseGame activeGame = currentGames.get(matchId);
        if (activeGame == null) {
            activeGame = scheduledGames.get(matchId);
            scheduledGames.remove(matchId);
            activeGame.setStatus(SCHEDULED);
            activeGame.setAlive(true);
            activeGame.setLastAction(GameAction.GAME_ACTIVATED);
            activeGame.setLastActionDate(LocalDateTime.now(ZoneId.of("Europe/Moscow")));
            currentGames.put(matchId, activeGame);
        }
        return ResponseEntity.ok(convertGameToAndroidGame((HockeyGame) activeGame));
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
    BaseGame showGame(@RequestParam("id") String gameId) {
        currentGames.values().stream()
                .map(BaseGame::isAlive)
                .collect(Collectors.toList());
        if (currentGames.containsKey(gameId)) {
            return currentGames.get(gameId);
        }
        return null;
    }

    @LogTime
    @RequestMapping(value = "api/scheduler", method = RequestMethod.GET)
    public @ResponseBody
    List<BaseGame> getScheduler() {
        return CollectionUtils.isEmpty(scheduledGames) ? EMPTY_LIST : new ArrayList<>(scheduledGames.values());
    }

    @LogTime
    @RequestMapping(value = "api/showallgames", method = RequestMethod.GET)
    @ResponseBody
    public List<BaseGame> showAllGames(@RequestParam(required = false, name = "scheduled") Boolean scheduled) {
        scheduled = scheduled == null ? true : false;
        List<BaseGame> result = currentGames.values().stream()
                .collect(Collectors.toList());
        //result.forEach(s -> currentGames.remove(s.getGameId()));
        currentGames.values().stream()
                .map(BaseGame::isAlive)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(currentGames.values()) && !scheduled) {
            return EMPTY_LIST;
        }
        return Stream.of(currentGames.values(), scheduledGames.values()).collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll)
                .stream().map(o -> (BaseGame) o).sorted(Comparator.comparing(BaseGame::getStatus)).collect(Collectors.toList());
    }

    @LogTime
    @GetMapping(value = "gameProcess")
    @ResponseBody
    public ResponseEntity<AndroidGame> gameProcess(@RequestParam(name = "matchid") String gameId,
                                                   @RequestParam(name = "action") String action,
                                                   @RequestParam(name = "min", required = false) Integer minValue,
                                                   @RequestParam(name = "sec", required = false) Integer secValue) {
        BaseGame processedGame = gameService.processGame(currentGames.get(gameId), GameAction.valueOf(action), minValue, secValue);
        return ResponseEntity.ok().body(convertGameToAndroidGame(((HockeyGame) processedGame)));
    }

    @MessageMapping("/showallgames")
    @SendTo("/results/score")
    public List<BaseGame> getLiveScorring() {
        logger.debug("Start check games");
        List<Game> result = currentGames.values().stream()
                .map(game -> (Game) game)
                .filter(Game::getTime)
                .collect(Collectors.toList());
        result.forEach(s -> currentGames.remove(s.getMatchId()));
        currentGames.values().stream()
                .map(BaseGame::isAlive)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(currentGames.values())) {
            return EMPTY_LIST;
        }
        return new ArrayList<>(currentGames.values());
    }

    @MessageMapping("/showgame")
    @SendTo("/results/score")
    public List<BaseGame> getLiveScorringForCurrentGame(Game currentGame) {
        currentGames.values().stream()
                .map(BaseGame::isAlive)
                .collect(Collectors.toList());
        return currentGames.get(currentGame.getMatchId()) != null ? singletonList(currentGames.get(currentGame.getMatchId())) : EMPTY_LIST;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity exception(Exception e) {
        logger.error(e.getMessage());
        return ResponseEntity.badRequest().body(e.getMessage());
    }


    private AndroidGame convertGameToAndroidGame(HockeyGame activeGame) {
        return AndroidGame.builder()
                .gameId(activeGame.getGameId())
                .team1Name(activeGame.getTeamPlayer1().getName())
                .team1Score(activeGame.getTeam1Score())
                .team1Fine(activeGame.isTeam1Fine())
                .team1FineScore(activeGame.getFineScore1())
                .team2Name(activeGame.getTeamPlayer2().getName())
                .team2Score(activeGame.getTeam2Score())
                .team2Fine(activeGame.isTeam2Fine())
                .team2FineScore(activeGame.getFineScore2())
                .period(activeGame.getPeriod())
                .fineSession(activeGame.isFineSession())
                .extraTime(activeGame.isExtraTime())
                .elapsedMinutes(activeGame.getElapsedMinutes())
                .elapsedSeconds(activeGame.getElapsedSeconds())
                .build();
    }

}