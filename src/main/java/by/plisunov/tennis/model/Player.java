package by.plisunov.tennis.model;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Player {

	private String Id;

	private String name;

	public Player(String playerName) {
		this.name = playerName;
	}

}
