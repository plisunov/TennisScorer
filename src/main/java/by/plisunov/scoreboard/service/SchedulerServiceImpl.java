package by.plisunov.scoreboard.service;

import by.plisunov.scoreboard.model.*;
import by.plisunov.scoreboard.model.modxsite.SiteContentValue;
import by.plisunov.scoreboard.scheduler.GameScheduler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SchedulerServiceImpl implements SchedulerService {

    @Autowired
    @Qualifier("lastGames")
    private Map<String, BaseGame> finishedGames;

    @Autowired
    @Qualifier("futureGames")
    private Map<String, BaseGame> scheduledGames;

    @Autowired
    @Qualifier("currentGames")
    private Map<String, BaseGame> currentGames;

    @Autowired
    private SiteContentRepository siteContentRepository;

    @Autowired
    private SiteContentValueRepository siteContentValueRepository;

    @Autowired
    private ObjectMapper mapper;

    private static final Logger logger = LoggerFactory.getLogger(GameScheduler.class);

    @Override
    public void writeInfoToDataBase() throws IOException {
        logger.debug("Start new task. Finished games count = " + finishedGames.keySet().size());
        logger.debug("Delete out off date games");
        List<String> outOffDateGames = finishedGames.values().stream()
                .filter(game -> game.getLastActionDate().isBefore(LocalDateTime.now(ZoneId.of("Europe/Moscow")).minusHours(12L)))
                .map(game -> game.getGameId())
                .collect(Collectors.toList());
        logger.debug("Out off date games count = " + outOffDateGames.size());
        if (!CollectionUtils.isEmpty(outOffDateGames)) {
            finishedGames.keySet().removeAll(outOffDateGames);
        }
        List<BaseGame> unsavedGames = finishedGames.values().stream().filter(game -> !game.isSaved()).collect(Collectors.toList());
        logger.debug("Unsaved games count = " + unsavedGames.size());
        for (BaseGame game : unsavedGames) {
            saveGameToSite(game);
            game.setSaved(true);
            game.getSnapshots().clear();
            finishedGames.remove(game.getGameId());
        }
    }

    private void saveGameToSite(BaseGame abstractGame) throws IOException {
        Game game = (Game) abstractGame;
        Date date = DateUtils.addHours(new Date(), 3);
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", new Locale("ru"));
        String today = format.format(date);
        Integer tournamentID = siteContentRepository.getTodaysTournamentHQL(today);
        SiteContentValue siteContentValue = siteContentValueRepository.findByContentId(tournamentID);
        String value = siteContentValue.getValue();
        List<TournamentInfo> games = Arrays.asList(mapper.readValue(value, TournamentInfo[].class));
        TournamentInfo currentGame = games.stream().filter(sitegame -> StringUtils.equals(sitegame.getId(), game.getMatchId())).findFirst().get();
        currentGame.setResult_a(String.valueOf(game.getPlayer1Score()));
        currentGame.setResult_b(String.valueOf(game.getPlayer2Score()));
        currentGame.setNotetour("Матч закончен. Счет по сетам " + generateSetsStatistic(game));
        siteContentValue.setValue(mapper.writeValueAsString(games));
        siteContentValueRepository.save(siteContentValue);

    }

    private String generateSetsStatistic(Game game) {
        String result = "";
        for (GameSet set : game.getSets()) {
            if (set.getScore1() == set.getScore2() && set.getScore1() == 0) {
                break;
            }
            result += "(" + set.getScore1() + ":" + set.getScore2() + ") ";
        }
        return result;
    }


    @Override
    public void getScheduledGames() throws IOException {
        logger.debug("Start new task. Get scheduler");
        logger.debug("Current scheduler size = " + scheduledGames.keySet().size());
        scheduledGames.clear();
        Date date = DateUtils.addHours(new Date(), 3);
        logger.debug("Today date = " + date);
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", new Locale("ru"));
        String today = format.format(date);
        logger.debug("Today date(string) = " + today);
        Integer tournamentID = siteContentRepository.getTodaysTournamentHQL(today);
        String value = siteContentValueRepository.getContentValue(tournamentID);
        if (value != null) {
            List<TournamentInfo> games = Arrays.asList(mapper.readValue(value, TournamentInfo[].class));
            games.stream().filter(game -> StringUtils.isEmpty(game.getNotetour()))
                    .filter(game -> StringUtils.isNotBlank(game.getNotour()))
                    .filter(game -> StringUtils.isNotBlank(game.getFirstplayer()))
                    .filter(game -> StringUtils.isNotBlank(game.getSecondplayer()))
                    .filter(game -> !currentGames.keySet().contains(game.getId()))
                    .filter(game -> !finishedGames.keySet().contains(game.getId()))
                    .map(game -> new HockeyGame(game))
                    .collect(Collectors.toList())
                    .forEach(game -> scheduledGames.put(game.getGameId(), game));
        }
    }

}
