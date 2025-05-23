/*
 * Copyright 2022-2024 BG Information Systems Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
   
package com.bginfosys.dinghyracing.web.websocket;

import static com.bginfosys.dinghyracing.web.websocket.WebSocketConfiguration.MESSAGE_PREFIX;

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
	
	// entry created
	@HandleAfterCreate
	public void newEntry(Entry entry) {
		if (logger.isDebugEnabled()) {
			logger.debug("Create entry: " + entry.toString());
		}
		this.websocket.convertAndSend(MESSAGE_PREFIX + "/createEntry", getURI(entry));
		// notify listeners on race events that a new entry has been added for the race
		Race race = entry.getRace();
		this.websocket.convertAndSend(MESSAGE_PREFIX + "/updateRace", getURI(race));
	}
	
	// entry updated
	@HandleAfterSave
	public void updateEntry(Entry entry) {
		if (logger.isDebugEnabled()) {
			logger.debug("Update entry: " + entry.toString());
		}
		this.websocket.convertAndSend(MESSAGE_PREFIX + "/updateEntry", getURI(entry));
	}
	
	// entry laps updated
	@HandleAfterLinkSave
	public void updateEntryLink(Entry entry, SortedSet<Lap> laps) {
		if (logger.isDebugEnabled()) {
			logger.debug("Update entry link: " + entry.toString());
		}
		this.websocket.convertAndSend(MESSAGE_PREFIX + "/updateEntry", getURI(entry));
		// notify listeners on race events that race may have updated
		Race race = entry.getRace();
		this.websocket.convertAndSend(MESSAGE_PREFIX + "/updateRaceEntryLaps", getURI(race)); // use a different message type so as not to force refresh on race watchers not interested in race lap updates
	}
	
	@HandleAfterDelete
	public void deleteEntry(Entry entry) {
		if (logger.isDebugEnabled()) {
			logger.debug("Delete entry: " + entry.toString());
		}
		this.websocket.convertAndSend(MESSAGE_PREFIX + "/deleteEntry", getURI(entry));
		// notify listeners on race events that an entry has been removed for the race
		Race race = entry.getRace();
		this.websocket.convertAndSend(MESSAGE_PREFIX + "/updateRace", getURI(race));
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
