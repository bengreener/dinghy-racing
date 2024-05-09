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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterDelete;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.bginfosys.dinghyracing.model.DinghyClass;

@Component
@RepositoryEventHandler(DinghyClass.class)
public class DinghyClassEventHandler {

	Logger logger = LoggerFactory.getLogger(DinghyClassEventHandler.class);
	
	private final SimpMessagingTemplate websocket;
	
	private final EntityLinks entityLinks;
	
	public DinghyClassEventHandler(SimpMessagingTemplate websocket, EntityLinks entityLinks) {
		this.websocket = websocket;
		this.entityLinks = entityLinks;
	}

	@HandleAfterCreate
	public void newDinghyClass(DinghyClass dinghyClass) {
		if (logger.isDebugEnabled()) {
			logger.debug("New dinghy class: " + dinghyClass.toString());
		}
		this.websocket.convertAndSend(
				MESSAGE_PREFIX + "/newDinghyClass", getURI(dinghyClass));
	}

	@HandleAfterDelete
	public void deleteDinghyClass(DinghyClass dinghyClass) {
		if (logger.isDebugEnabled()) {
			logger.debug("Delete dinghy class: " + dinghyClass.toString());
		}
		this.websocket.convertAndSend(
				MESSAGE_PREFIX + "/deleteDinghyClass", getURI(dinghyClass));
	}

	@HandleAfterSave
	public void updateDinghyClass(DinghyClass dinghyClass) {
		if (logger.isDebugEnabled()) {
			logger.debug("Update dinghy class: " + dinghyClass.toString());
		}
		this.websocket.convertAndSend(
				MESSAGE_PREFIX + "/updateDinghyClass", getURI(dinghyClass));
	}

	/**
	 * Take an {@link DinghyClass} and get the URI using Spring Data REST's {@link EntityLinks}.
	 *
	 * @param dinghyClass
	 */
	private String getURI(DinghyClass dinghyClass) {
		return this.entityLinks.linkForItemResource(dinghyClass.getClass(), dinghyClass.getId()).toUri().toString();
	}
}
