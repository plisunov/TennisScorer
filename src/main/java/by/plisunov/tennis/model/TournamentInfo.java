package by.plisunov.tennis.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TournamentInfo {

    @JsonProperty("MIGX_id")
    private String id;

    @JsonProperty("notour")
            private String notour;

    @JsonProperty("tourdate")
    private String tourdate;

    @JsonProperty("firstplayer")
    private String firstplayer;

    @JsonProperty("secondplayer")
    private String secondplayer;

    @JsonProperty("result_a")
    private String result_a;

    @JsonProperty("result_b")
    private String result_b;

    @JsonProperty("notetour")
    private String notetour;

}
