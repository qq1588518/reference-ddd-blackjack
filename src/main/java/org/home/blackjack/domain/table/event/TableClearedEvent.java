package org.home.blackjack.domain.table.event;

import org.home.blackjack.domain.table.core.TableID;

public class TableClearedEvent extends TableEvent {

    public TableClearedEvent(TableID id) {
        super(id);
    }

}
