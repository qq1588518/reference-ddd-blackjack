package org.home.blackjack.integration.test.steps.testagent;

import java.util.List;
import java.util.Map;

import org.home.blackjack.domain.shared.PlayerID;
import org.home.blackjack.domain.table.Table;
import org.home.blackjack.domain.table.TableRepository;
import org.home.blackjack.domain.table.core.TableID;
import org.home.blackjack.integration.test.dto.CardDO;
import org.home.blackjack.integration.test.dto.TableDO;
import org.home.blackjack.integration.test.fakes.FakeDeckFactory;
import org.home.blackjack.integration.test.util.CucumberService;
import org.home.blackjack.integration.test.util.Util;

import com.google.common.collect.Maps;


public abstract class TestAgent {
	
	protected Map<Integer, TableID> tableIdMap = Maps.newHashMap();
	protected Map<Integer, PlayerID> playerIdMap = Maps.newHashMap();
	
	protected FakeDeckFactory fakeDeckFactory;
	protected TableRepository tableRepository;
	
	
    public TestAgent() {
        initDependencies();
    }

    protected void initDependencies() {
    	fakeDeckFactory = cucumberService().getBean(FakeDeckFactory.class);
    	tableRepository = cucumberService().getBean(TableRepository.class);
    }
    
    protected abstract CucumberService cucumberService();
    
    public void reset() {
    	fakeDeckFactory.reset();
    	tableRepository.clear();
    	tableIdMap.clear();
    }
    
	protected PlayerID generatePlayerId(Integer playerId) {
		PlayerID realId = new PlayerID();
		playerIdMap.put(playerId, realId);
		return realId;
	}
	protected TableID generateTableId(Integer tableId) {
		TableID realId = new TableID();
		tableIdMap.put(tableId, realId);
		return realId;
	}
	protected TableID getRealTableId(Integer tableId) {
		return tableIdMap.get(tableId);
	}
	protected PlayerID getRealPlayerId(Integer playerId) {
		return playerIdMap.get(playerId);
	}
	public void givenAPreparedDeck(List<CardDO> cards) {
		fakeDeckFactory.prepareDeckFrom(Util.transform(cards));
		
	}

	public void givenAnEmptyTable(Integer tableID) {
		tableRepository.create(new Table(generateTableId(tableID)));
	}

	public abstract void thenTablesSeenInLobby(List<TableDO> tables);

	public abstract void playerSitsToTable(Integer playerId, Integer tableId);

	public abstract void thenGameStartedAtTable(Integer tableID);

	public abstract void thenPlayerBeenDealt(Integer playerId, Integer tableId, String card);

	public abstract void playerHits(Integer playerId, Integer tableId);

	public abstract void thenPlayerWon(Integer playerId, Integer tableId);

}