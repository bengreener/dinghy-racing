package com.bginfosys.dinghyracing.web.websocket;

import static com.bginfosys.dinghyracing.web.websocket.WebSocketConfiguration.MESSAGE_PREFIX;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.bginfosys.dinghyracing.model.Competitor;

@Component
@RepositoryEventHandler(Competitor.class)
public class CompetitorEventHandler {

	Logger logger = LoggerFactory.getLogger(RaceEventHandler.class);
	
	private final SimpMessagingTemplate websocket;
	
	private final EntityLinks entityLinks;
	
	public CompetitorEventHandler(SimpMessagingTemplate websocket, EntityLinks entityLinks) {
		this.websocket = websocket;
		this.entityLinks = entityLinks;
	}

	@HandleAfterCreate
	public void newCompetitor(Competitor competitor) {
		if (logger.isDebugEnabled()) {
			logger.debug("New race: " + competitor.toString());
		}
		this.websocket.convertAndSend(MESSAGE_PREFIX + "/newRace", getURI(competitor));
	}
	
	/**
	 * Take an {@link Competitor} and get the URI using Spring Data REST's {@link EntityLinks}.
	 *
	 * @param race
	 */
	private String getURI(Competitor competitor) {
		return this.entityLinks.linkToItemResource(competitor.getClass(), competitor.getId()).toUri().toString();
	}
}
