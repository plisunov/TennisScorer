package by.plisunov.tennis.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameSet {

    private int Id;

    private int setNumber;

    private int score1;

    private int score2;

    @JsonIgnore
    private Game game;

    public GameSet(Game game, int setNumber) {
        this.game = game;
        this.setNumber = setNumber + 1;
        this.score1 = 0;
        this.score2 = 0;
    }
}
