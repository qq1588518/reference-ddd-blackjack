package org.home.blackjack.domain;

/**
 * Value Object
 * 
 * @author Mate
 * 
 */
public class GameId extends NumericId {
	
	public GameId(long id) {
		super(id);
	}

	@Override
	public String toString() {
		return ""+id;
	}
}
