package org.home.blackjack.core.infrastructure.adapters.driving.gamerepository.repository;

import java.util.concurrent.locks.Lock;

import javax.inject.Inject;

import org.home.blackjack.core.domain.game.Game;
import org.home.blackjack.core.domain.game.GameRepository;
import org.home.blackjack.core.domain.game.core.GameID;
import org.home.blackjack.core.domain.game.exception.GameNotFoundException;
import org.home.blackjack.core.domain.shared.TableID;
import org.home.blackjack.core.infrastructure.adapters.driving.gamerepository.store.GameStore;
import org.home.blackjack.util.ddd.pattern.domain.events.DomainEventPublisherFactory;
import org.home.blackjack.util.ddd.pattern.infrastructure.persistence.PersistenceAssembler;
import org.home.blackjack.util.ddd.pattern.infrastructure.persistence.PersistenceObject;
import org.home.blackjack.util.locking.FinegrainedLockable;
import org.home.blackjack.util.marker.hexagonal.DrivingAdapter;

public class SerializingGameRepository implements GameRepository, FinegrainedLockable<GameID>, DrivingAdapter<GameRepository> {
	
	private final GameStore gameStore;
	private final PersistenceAssembler<Game, PersistenceObject<Game>> gameStoreAssembler;

    @Inject
    private DomainEventPublisherFactory domainEventPublisherFactory;

	public SerializingGameRepository(GameStore gameStore) {
		this.gameStore = gameStore;
		this.gameStoreAssembler = gameStore.assembler();
	}
	
	@Override
	public Game find(GameID gameID) {
		PersistenceObject<Game> po = gameStore.find(gameStoreAssembler.toPersistence(gameID));
		if (po == null) {
			throw new GameNotFoundException(gameID);
		}
		Game game = gameStoreAssembler.toDomain(po);
		game.setDomainEventPublisher(domainEventPublisherFactory.domainEventPublisherInstance());
		return game;
	}
	

	@Override
	public Game find(TableID tableId) {
		PersistenceObject<Game> po = gameStore.find(tableId);
		if (po == null) {
			throw new GameNotFoundException(tableId);
		}
		Game game = gameStoreAssembler.toDomain(po);
		game.setDomainEventPublisher(domainEventPublisherFactory.domainEventPublisherInstance());
		return game;
	}

	@Override
	public void create(Game game) {
		PersistenceObject<Game> po = gameStoreAssembler.toPersistence(game);
		gameStore.create(po);
	}

	@Override
	public void update(Game game) {
		PersistenceObject<Game> po = gameStoreAssembler.toPersistence(game);
		gameStore.update(po);
	}
	
	@Override
	public Lock getLockForKey(GameID key) {
		return gameStore.getLockForKey(key);
	}
	
}
