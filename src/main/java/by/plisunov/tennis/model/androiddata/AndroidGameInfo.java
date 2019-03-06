package by.plisunov.tennis.model.androiddata;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AndroidGameInfo {

    private String gameId;

    private String stringGameId;

    private String player1;

    private String player2;

    private String gameTime;

    public AndroidGameInfo(String id, String player1Name, String player2Name, String gameTime) {
        this.gameId = id;
        this.stringGameId = String.valueOf(id);
        this.player1 = player1Name;
        this.player2 = player2Name;
        this.gameTime = gameTime;
    }
}
