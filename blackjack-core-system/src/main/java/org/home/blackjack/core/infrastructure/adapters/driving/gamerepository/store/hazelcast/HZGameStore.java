package org.home.blackjack.core.infrastructure.adapters.driving.gamerepository.store.hazelcast;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.inject.Named;

import org.home.blackjack.core.domain.game.Game;
import org.home.blackjack.core.domain.game.core.GameID;
import org.home.blackjack.core.domain.shared.TableID;
import org.home.blackjack.core.infrastructure.adapters.driving.gamerepository.store.GameStore;
import org.home.blackjack.core.infrastructure.adapters.driving.gamerepository.store.serializing.GameGsonProvider;
import org.home.blackjack.core.infrastructure.adapters.util.persistence.json.JsonPersistenceAssembler;
import org.home.blackjack.core.infrastructure.adapters.util.persistence.json.JsonPersistenceObject;
import org.home.blackjack.core.infrastructure.adapters.util.persistence.json.StringPersistenceId;
import org.home.blackjack.util.ddd.pattern.infrastructure.persistence.PersistenceObject;
import org.home.blackjack.util.ddd.pattern.infrastructure.persistence.PersistenceObjectId;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Maps;
import com.hazelcast.core.HazelcastInstance;

@Named
public class HZGameStore implements GameStore {
	
	private final Map<StringPersistenceId<GameID>, String> jsonMap;
	private final ConcurrentMap<GameID, Lock> locks = Maps.newConcurrentMap();

	private JsonPersistenceAssembler<Game> gameStoreAssembler = new JsonPersistenceAssembler<Game>(Game.class, new GameGsonProvider());
	
    @Autowired
    public HZGameStore(HazelcastInstance hzInstance) {
        jsonMap = hzInstance.getMap("gameHZMap");
    }
	
	@Override
	public JsonPersistenceAssembler<Game> assembler() {
		return gameStoreAssembler;
	}

	@Override
	public JsonPersistenceObject<Game> find(PersistenceObjectId<GameID> id) {
		StringPersistenceId<GameID> gameID = (StringPersistenceId<GameID>) id;
		String json = jsonMap.get(gameID);
		return new JsonPersistenceObject<Game>(json);
	}
	

	@Override
	public PersistenceObject<Game> find(TableID tableId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(PersistenceObject<Game> po) {
		JsonPersistenceObject<Game> mpg = (JsonPersistenceObject<Game>) po;
		jsonMap.put((StringPersistenceId<GameID>) mpg.id(), mpg.getJson());
	}

    @Override
    public void create(PersistenceObject<Game> po) {
    	JsonPersistenceObject<Game> mpg = (JsonPersistenceObject<Game>) po;
        jsonMap.put((StringPersistenceId<GameID>)mpg.id(), mpg.getJson());
    }

    @Override
    public Lock getLockForKey(GameID key) {
        locks.putIfAbsent(key, new ReentrantLock());
        return locks.get(key);
    }

	@Override
	public void clear() {
		jsonMap.clear();
		locks.clear();
	}


}
