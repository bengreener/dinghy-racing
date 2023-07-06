package com.bginfosys.dinghyracing.web.websocket;

import static com.bginfosys.dinghyracing.web.websocket.RaceWebSocketConfiguration.MESSAGE_PREFIX;

import org.springframework.beans.factory.annotation.Autowired;
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
	
	private final SimpMessagingTemplate websocket;
	
	private final EntityLinks entityLinks;
	
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
