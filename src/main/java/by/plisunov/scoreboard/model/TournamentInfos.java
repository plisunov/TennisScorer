package by.plisunov.scoreboard.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class TournamentInfos {

    private List<TournamentInfo> infoList;

}
