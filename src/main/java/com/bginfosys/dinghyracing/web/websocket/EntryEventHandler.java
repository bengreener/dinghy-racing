package com.bginfosys.dinghyracing.web.websocket;

import static com.bginfosys.dinghyracing.web.websocket.WebSocketConfiguration.MESSAGE_PREFIX;

import java.util.SortedSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.rest.core.annotation.HandleAfterLinkSave;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.bginfosys.dinghyracing.model.Entry;
import com.bginfosys.dinghyracing.model.Lap;

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
	 * Take an {@link Entry} and get the URI using Spring Data REST's {@link EntityLinks}.
	 *
	 * @param entry
	 */
	private String getURI(Entry entry) {
		return this.entityLinks.linkToItemResource(entry.getClass(), entry.getId()).toUri().toString();
	}
}
