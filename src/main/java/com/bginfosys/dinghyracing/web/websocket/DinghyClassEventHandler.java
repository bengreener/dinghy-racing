package com.bginfosys.dinghyracing.web.websocket;

import static com.bginfosys.dinghyracing.web.websocket.WebSocketConfiguration.MESSAGE_PREFIX;

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
	
	private final SimpMessagingTemplate websocket;
	
	private final EntityLinks entityLinks;
	
	public DinghyClassEventHandler(SimpMessagingTemplate websocket, EntityLinks entityLinks) {
		this.websocket = websocket;
		this.entityLinks = entityLinks;
	}

	@HandleAfterCreate
	public void newDinghyClass(DinghyClass dinghyClass) {
		this.websocket.convertAndSend(
				MESSAGE_PREFIX + "/newDinghyClass", getPath(dinghyClass));
	}

	@HandleAfterDelete
	public void deleteDinghyClass(DinghyClass dinghyClass) {
		this.websocket.convertAndSend(
				MESSAGE_PREFIX + "/deleteDinghyClass", getPath(dinghyClass));
	}

	@HandleAfterSave
	public void updateDinghyClass(DinghyClass dinghyClass) {
		this.websocket.convertAndSend(
				MESSAGE_PREFIX + "/updateDinghyClass", getPath(dinghyClass));
	}

	/**
	 * Take an {@link DinghyClass} and get the URI using Spring Data REST's {@link EntityLinks}.
	 *
	 * @param dinghyClass
	 */
	private String getPath(DinghyClass dinghyClass) {
		return this.entityLinks.linkForItemResource(dinghyClass.getClass(),
				dinghyClass.getId()).toUri().getPath();
	}
}
