package by.plisunov.scoreboard.model.androiddata;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
//TODO replace it to abstract
public class AndroidGameInfo {

    private String gameId;

    private String player1;

    private String player2;

}
