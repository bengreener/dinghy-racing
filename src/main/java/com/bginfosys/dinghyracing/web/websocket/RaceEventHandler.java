package com.bginfosys.dinghyracing.web.websocket;

import static com.bginfosys.dinghyracing.web.websocket.WebSocketConfiguration.MESSAGE_PREFIX;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterDelete;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.bginfosys.dinghyracing.model.Race;

@Component
@RepositoryEventHandler(Race.class)
public class RaceEventHandler {
	
	Logger logger = LoggerFactory.getLogger(RaceEventHandler.class);
	
	private final SimpMessagingTemplate websocket;
	
	private final EntityLinks entityLinks;
	
	public RaceEventHandler(SimpMessagingTemplate websocket, EntityLinks entityLinks) {
		this.websocket = websocket;
		this.entityLinks = entityLinks;
	}

	@HandleAfterCreate
	public void newRace(Race race) {
		if (logger.isDebugEnabled()) {
			logger.debug("New race: " + race.toString());
		}
		this.websocket.convertAndSend(MESSAGE_PREFIX + "/newRace", getURI(race));
	}

	@HandleAfterDelete
	public void deleteRace(Race race) {
		if (logger.isDebugEnabled()) {
			logger.debug("Delete race: " + race.toString());
		}
		this.websocket.convertAndSend(MESSAGE_PREFIX + "/deleteRace", getURI(race));
	}

	@HandleAfterSave
	public void updateRace(Race race) {
		if (logger.isDebugEnabled()) {
			logger.debug("Update race: " + race.toString());
		}
		this.websocket.convertAndSend(
				MESSAGE_PREFIX + "/updateRace", getURI(race));
	}
	
	/**
	 * Take an {@link Race} and get the URI using Spring Data REST's {@link EntityLinks}.
	 *
	 * @param race
	 */
	private String getURI(Race race) {
		return this.entityLinks.linkToItemResource(race.getClass(), race.getId()).toUri().toString();
	}
}
