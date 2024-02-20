package com.bginfosys.dinghyracing.web.websocket;

import static com.bginfosys.dinghyracing.web.websocket.WebSocketConfiguration.MESSAGE_PREFIX;

import java.util.SortedSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterLinkSave;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.bginfosys.dinghyracing.model.Entry;
import com.bginfosys.dinghyracing.model.Lap;
import com.bginfosys.dinghyracing.model.Race;

@Component
@RepositoryEventHandler(Entry.class)
public class EntryEventHandler {
	
	Logger logger = LoggerFactory.getLogger(EntryEventHandler.class);
	
	private final SimpMessagingTemplate websocket;
	
	private final EntityLinks entityLinks;
	
	public EntryEventHandler(SimpMessagingTemplate websocket, EntityLinks entityLinks) {
		this.websocket = websocket;
		this.entityLinks = entityLinks;
	}
	
	@HandleAfterCreate
	public void newEvent(Entry entry) {
		if (logger.isDebugEnabled()) {
			logger.debug("New event: " + entry.toString());
		}
		this.websocket.convertAndSend(MESSAGE_PREFIX + "/newEvent", getURI(entry));
		// notify listeners on race events that a new event has been added for the race
		Race race = entry.getRace();
		this.websocket.convertAndSend(MESSAGE_PREFIX + "/updateRace", getURI(race));
	}
	
	@HandleAfterSave
	public void updateEntry(Entry entry) {
		if (logger.isDebugEnabled()) {
			logger.debug("Update entry: " + entry.toString());
		}
		this.websocket.convertAndSend(MESSAGE_PREFIX + "/updateEntry", getURI(entry));
	}
	
	@HandleAfterLinkSave
	public void updateEntryLink(Entry entry, SortedSet<Lap> laps) {
		if (logger.isDebugEnabled()) {
			logger.debug("Update entry link: " + entry.toString());
		}
		this.websocket.convertAndSend(MESSAGE_PREFIX + "/updateEntry", getURI(entry));
	}
	
	/**
	 * Take an {@link Object} and get the URI using Spring Data REST's {@link EntityLinks}.
	 *
	 * @param entry
	 */
	private String getURI(Object entity) {
		String uri = "";
		if (entity instanceof Entry) {
			Entry entry = (Entry) entity;
			uri = this.entityLinks.linkToItemResource(Entry.class, entry.getId()).toUri().toString();	
		}
		else if (entity instanceof Race) {
			Race race = (Race) entity;
			uri = this.entityLinks.linkToItemResource(Race.class, race.getId()).toUri().toString();
		}
		return uri;
	}
}
