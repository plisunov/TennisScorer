package by.plisunov.scoreboard.model;

import lombok.Data;

@Data
public class Player extends Team {

    public Player(String playerName) {
        this.name = playerName;
    }

}
