package by.plisunov.scoreboard.controller;

import by.plisunov.scoreboard.model.BaseGame;
import by.plisunov.scoreboard.model.androiddata.AndroidGameInfo;
import by.plisunov.scoreboard.model.androiddata.GameStartInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController(value = "/apiv2")
public class GameController {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    @Qualifier("currentGames")
    private Map<String, BaseGame> currentGames;

    @RequestMapping(value = "/selectgame", method = RequestMethod.GET)
    public List<AndroidGameInfo> selectGame() {
        //TODO
        return new ArrayList<>();
    }

    @RequestMapping(value = "/startgame/{id}", method = RequestMethod.POST)
    public void startGame(@RequestParam(required = true) Long gameId, @RequestBody final String request) throws Exception {
        GameStartInfo gameStartInfo = mapper.readValue(request, GameStartInfo.class);

    }


}
