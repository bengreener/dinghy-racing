package com.bginfosys.dinghyracing.web.websocket;

import static com.bginfosys.dinghyracing.web.websocket.WebSocketConfiguration.MESSAGE_PREFIX;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.bginfosys.dinghyracing.model.Dinghy;

@Component
@RepositoryEventHandler(Dinghy.class)
public class DinghyEventHandler {

	Logger logger = LoggerFactory.getLogger(RaceEventHandler.class);
	
	private final SimpMessagingTemplate websocket;
	
	private final EntityLinks entityLinks;
	
	public DinghyEventHandler(SimpMessagingTemplate websocket, EntityLinks entityLinks) {
		this.websocket = websocket;
		this.entityLinks = entityLinks;
	}

	@HandleAfterCreate
	public void newDinghy(Dinghy dinghy) {
		if (logger.isDebugEnabled()) {
			logger.debug("Create dinghy: " + dinghy.toString());
		}
		this.websocket.convertAndSend(MESSAGE_PREFIX + "/createDinghy", getURI(dinghy));
	}
	
	/**
	 * Take an {@link Competitor} and get the URI using Spring Data REST's {@link EntityLinks}.
	 *
	 * @param race
	 */
	private String getURI(Dinghy dinghy) {
		return this.entityLinks.linkToItemResource(dinghy.getClass(), dinghy.getId()).toUri().toString();
	}
}
