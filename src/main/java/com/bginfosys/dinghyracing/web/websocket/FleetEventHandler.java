package com.bginfosys.dinghyracing.web.websocket;

import static com.bginfosys.dinghyracing.web.websocket.WebSocketConfiguration.MESSAGE_PREFIX;

import java.util.Set;
import java.util.SortedSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterDelete;
import org.springframework.data.rest.core.annotation.HandleAfterLinkSave;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.bginfosys.dinghyracing.model.DinghyClass;
import com.bginfosys.dinghyracing.model.Fleet;

@Component
@RepositoryEventHandler
public class FleetEventHandler {
	
	Logger logger = LoggerFactory.getLogger(EntryEventHandler.class);

	private final SimpMessagingTemplate websocket;
	
	private final EntityLinks entityLinks;
	
	public FleetEventHandler(SimpMessagingTemplate websocket, EntityLinks entityLinks) {
		this.websocket = websocket;
		this.entityLinks = entityLinks;
	}
	
	@HandleAfterCreate
	public void newFleet(Fleet fleet) {
		if (logger.isDebugEnabled()) {
			logger.debug("Create fleet: " + fleet.toString());
		}
		this.websocket.convertAndSend(MESSAGE_PREFIX + "/createFleet", getURI(fleet));
	}
	
	@HandleAfterSave
	public void updateFleet(Fleet fleet) {
		if (logger.isDebugEnabled()) {
			logger.debug("Update fleet: " + fleet.toString());
		}
		this.websocket.convertAndSend(MESSAGE_PREFIX + "/updateFleet", getURI(fleet));
	}
	
	@HandleAfterLinkSave
	public void updateFleetLink(Fleet fleet, Set<DinghyClass> dinghyClasses) {
		if (logger.isDebugEnabled()) {
			logger.debug("Update fleet link: " + fleet.toString());
		}
		this.websocket.convertAndSend(MESSAGE_PREFIX + "/updateFleet", getURI(fleet));
	}
	
	@HandleAfterDelete
	public void deleteEntry(Fleet fleet) {
		if (logger.isDebugEnabled()) {
			logger.debug("Delete fleet: " + fleet.toString());
		}
		this.websocket.convertAndSend(MESSAGE_PREFIX + "/deleteEntry", getURI(fleet));
	}
	
	/**
	 * Take an {@link Object} and get the URI using Spring Data REST's {@link EntityLinks}.
	 *
	 * @param entry
	 */
	private String getURI(Object entity) {
		String uri = "";
		if (entity instanceof Fleet) {
			Fleet fleet = (Fleet) entity;
			uri = this.entityLinks.linkToItemResource(Fleet.class, fleet.getId()).toUri().toString();	
		}
		return uri;
	}
}
