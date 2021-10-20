package com.bginfosys.dinghyracing.race;

import static com.bginfosys.dinghyracing.race.RaceWebSocketConfiguration.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterDelete;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler(Race.class)
public class RaceEventHandler {
	
	private final SimpMessagingTemplate websocket;
	
	private final EntityLinks entityLinks;
	
	@Autowired
	public RaceEventHandler(SimpMessagingTemplate websocket, EntityLinks entityLinks) {
		this.websocket = websocket;
		this.entityLinks = entityLinks;
	}

	@HandleAfterCreate
	public void newRace(Race race) {
		this.websocket.convertAndSend(
				MESSAGE_PREFIX + "/newRace", getPath(race));
	}

	@HandleAfterDelete
	public void deleteRace(Race race) {
		this.websocket.convertAndSend(
				MESSAGE_PREFIX + "/deleteRace", getPath(race));
	}

	@HandleAfterSave
	public void updateRace(Race race) {
		this.websocket.convertAndSend(
				MESSAGE_PREFIX + "/updateRace", getPath(race));
	}

	/**
	 * Take an {@link Race} and get the URI using Spring Data REST's {@link EntityLinks}.
	 *
	 * @param race
	 */
	private String getPath(Race race) {
		return this.entityLinks.linkForItemResource(race.getClass(),
				race.getId()).toUri().getPath();
	}
	
}
