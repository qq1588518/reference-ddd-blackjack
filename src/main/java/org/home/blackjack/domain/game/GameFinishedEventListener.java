package org.home.blackjack.domain.game;

import org.home.blackjack.domain.game.event.GameFinishedEvent;
import org.home.blackjack.domain.player.Player;
import org.home.blackjack.domain.player.PlayerRepository;

/**
 * This should be refactored, since we must lock the DB for the update. It means
 * we have to go through the app layer. So the Listener in the Domain must catch
 * the event, then pass it to an App service. But it can't be done directly,
 * since Domain cn't call App. Maybe Domain Interface + App implementation?
 * 
 * @author Mate
 * 
 */
public class GameFinishedEventListener {

	private PlayerRepository playerRepository;

	public void receive(GameFinishedEvent event) {
		Player player = playerRepository.find(event.getWinner());
		player.recordWin();
		playerRepository.update(player);
	}

}
