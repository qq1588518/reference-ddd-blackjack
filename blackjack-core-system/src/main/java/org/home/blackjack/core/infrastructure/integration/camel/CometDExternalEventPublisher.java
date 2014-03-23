package org.home.blackjack.core.infrastructure.integration.camel;

import javax.annotation.Resource;
import javax.inject.Named;

import org.apache.camel.ProducerTemplate;
import org.apache.log4j.Logger;
import org.home.blackjack.core.app.events.external.ExternalDomainEvent;
import org.home.blackjack.core.app.events.external.ExternalDomainEvent.Addressee;
import org.home.blackjack.core.app.events.external.ExternalEventPublisher;
import org.home.blackjack.util.ddd.pattern.events.DomainEvent;
import org.springframework.beans.factory.annotation.Value;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

@Named
public class CometDExternalEventPublisher implements ExternalEventPublisher {

	private static Logger LOGGER = Logger.getLogger(CometDExternalEventPublisher.class);

	@Resource
	private ProducerTemplate producerTemplate;
	@Value("${blackjack.cometd.uri}")
	private String source;
	

	@Override
	public void publish(ExternalDomainEvent event) {
		LOGGER.info("publish " + event);
		String channel = channel(event.getAddressee());
		DomainEvent domainEvent = event.getEvent();
		JsonObject jsonObject = new Gson().toJsonTree(domainEvent).getAsJsonObject();
		jsonObject.addProperty("type", domainEvent.getClass().getSimpleName());
		producerTemplate.asyncSendBody(source + channel, jsonObject.toString());
	}

	private String channel(Addressee addressee) {
		if (addressee.tableId != null)
			if (addressee.playerId != null)
				return "/table/" + addressee.tableId.toString() + "/player/" + addressee.playerId.toString();
			else
				return "/table/" + addressee.tableId.toString();
		else
			return "/player/" + addressee.playerId.toString();
	}

}