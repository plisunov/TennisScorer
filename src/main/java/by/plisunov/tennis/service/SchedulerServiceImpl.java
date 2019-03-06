package by.plisunov.tennis.service;

import by.plisunov.tennis.model.Game;
import by.plisunov.tennis.model.GameSet;
import by.plisunov.tennis.model.GameSnapshot;
import by.plisunov.tennis.model.TournamentInfo;
import by.plisunov.tennis.model.modxsite.SiteContentValue;
import by.plisunov.tennis.scheduler.GameScheduler;
import by.plisunov.util.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrBuilder;
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

import static by.plisunov.util.Constants.POST_DETAIL_INFO.*;

@Service
public class SchedulerServiceImpl implements SchedulerService {

    @Autowired
    @Qualifier("lastGames")
    private Map<String, Game> finishedGames;

    @Autowired
    @Qualifier("futureGames")
    private Map<String, Game> scheduledGames;

    @Autowired
    @Qualifier("currentGames")
    private Map<String, Game> currentGames;

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
                .map(game -> game.getMatchId())
                .collect(Collectors.toList());
        logger.debug("Out off date games count = " + outOffDateGames.size());
        if (!CollectionUtils.isEmpty(outOffDateGames)) {
            finishedGames.keySet().removeAll(outOffDateGames);
        }
        List<Game> unsavedGames = finishedGames.values().stream().filter(game -> !game.isSaved()).collect(Collectors.toList());
        logger.debug("Unsaved games count = " + unsavedGames.size());
        for (Game game : unsavedGames) {
            saveGameToSite(game);
            game.setSaved(true);
            game.getSnapshots().clear();
            finishedGames.remove(game.getMatchId());
        }
    }

    private void saveGameToSite(Game game) throws IOException {
        Date date = Date.from(LocalDateTime.now().atZone(ZoneId.of("Europe/Moscow")).toInstant());
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
        Date date = Date.from(LocalDateTime.now().atZone(ZoneId.of("Europe/Moscow")).toInstant());
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", new Locale("ru"));
        String today = format.format(date);
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
                    .map(game -> new Game(game))
                    .collect(Collectors.toList())
                    .forEach(game -> scheduledGames.put(game.getMatchId(), game));
        }
    }

    private void generateGameResultDetails(Game game) {
        /*LinkedList<GameSnapshot> snapshots = game.getSnapshots();
        GameSnapshot previos = null;
        List<WPPostContent> postDetails = new ArrayList<>();
        WPPostContent counterContent = wpPostContentRepository.getWPPostContentByPostIdAndMetaKey(Long.valueOf(game.getMatchId()), COUNTER_KEY);
        for (int counterNumber = 0; counterNumber < snapshots.size(); counterNumber++) {
            GameSnapshot snapshot = snapshots.get(counterNumber);

            postDetails.addAll(generateDetails(snapshot, game.getMatchId(), counterNumber, checkaction(snapshot, previos)));
            previos = snapshot;
        }
        wpPostContentRepository.save(postDetails);
        counterContent.setMetaValue(String.valueOf(snapshots.size() - 1));
        wpPostContentRepository.save(counterContent);*/

    }

    private Constants.POST_DETAIL_INFO checkaction(GameSnapshot snapshot, GameSnapshot previos) {
        if (previos == null) {
            if (snapshot.getSetScore1() == 1) {
                return PLAYER1_POINT;
            }
            if (snapshot.getSetScore2() == 1) {
                return PLAYER2_POINT;
            }
        } else {
            if (snapshot.getSetScore1() > previos.getSetScore1()) {
                return PLAYER1_POINT;
            }
            if (snapshot.getSetScore2() > previos.getSetScore1()) {
                return PLAYER2_POINT;
            }
            if (snapshot.getSetScore1() < previos.getSetScore1() && snapshot.getSetNumber() == previos.getSetNumber()) {
                return PLAYER1_UNPOINT;
            }
            if (snapshot.getSetScore2() < previos.getSetScore1() && snapshot.getSetNumber() == previos.getSetNumber()) {
                return PLAYER2_UNPOINT;
            }
        }
        return NOTHING_CHANGES;
    }

   /* private List<WPPostContent> generateDetails(GameSnapshot snapshot, String matchId, int counterKey, Constants.POST_DETAIL_INFO info) {
        SimpleDateFormat format = new SimpleDateFormat("hh:mm", new Locale("ru"));
        List<WPPostContent> actionDetail = new ArrayList<>();
        Long postId = Long.valueOf(matchId);
        WPPostContent counterContent = wpPostContentRepository.getWPPostContentByPostIdAndMetaKey(postId, COUNTER_KEY);
        actionDetail.add(WPPostContent.builder()
                .postId(postId)
                .id(null)
                .metaKey(MessageFormat.format(DETAILS_TIME_FIELD, counterKey))
                .metaValue(format.format(snapshot.getActionTime()))
                .build());
        actionDetail.add(WPPostContent.builder()
                .id(null)
                .postId(postId)
                .metaKey(MessageFormat.format(DETAILS_PLAYER1_SCORE_FIELD, counterKey))
                .metaValue(PLAYER1_POINT == info ? "1" : PLAYER1_UNPOINT == info ? "-1" : "")
                .build());
        actionDetail.add(WPPostContent.builder()
                .id(null)
                .postId(postId)
                .metaKey(MessageFormat.format(DETAILS_PLAYER2_SCORE_FIELD, counterKey))
                .metaValue(PLAYER2_POINT == info ? "1" : PLAYER2_UNPOINT == info ? "-1" : "")
                .build());
        StrBuilder setScore = new StrBuilder()
                .append(String.valueOf(snapshot.getSetScore1()))
                .append(":")
                .append(String.valueOf(snapshot.getSetScore2()))
                .append(" (")
                .append(String.valueOf(snapshot.getScore1()))
                .append(":")
                .append(String.valueOf(snapshot.getScore2()))
                .append(")");
        actionDetail.add(WPPostContent.builder()
                .id(null)
                .postId(postId)
                .metaKey(MessageFormat.format(DETAILS_TOTAL_SCORE_FIELD, counterKey))
                .metaValue(setScore.toString())
                .build());
        return actionDetail;
    }

    private void generateGameResultContent(Game currentGame) {
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
        detailsBuilder.append("a:6:{s:4:\"time\";s:5:\"")
                .append(StringUtils.isNotBlank(currentGame.getMatchTime()) ? currentGame.getMatchTime().trim() : "     ")
                .append("\";s:7:\"meeting\";s:1:\"x\";s:9:\"playerone\";s:")
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
        logger.debug("Data to begetDB " + detailsBuilder.toString());
        wpPostContentRepository.save(details);
    }*/

    private String getSetScores(Game currentGame) {
        List<GameSet> sets = currentGame.getSets();
        StrBuilder setScore = new StrBuilder();
        for (int i = 0; i < currentGame.getCurrentSet() - 1; i++) {
            setScore.append(String.valueOf(sets.get(i).getScore1()))
                    .append(":")
                    .append(String.valueOf(sets.get(i).getScore2()))
                    .append("; ");
        }
        return setScore.toString();
    }
}
