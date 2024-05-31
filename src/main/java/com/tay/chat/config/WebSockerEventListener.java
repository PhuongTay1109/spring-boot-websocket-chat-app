package com.tay.chat.config;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.tay.chat.chat.ChatMessage;
import com.tay.chat.chat.MessageType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSockerEventListener {
	
	public final SimpMessageSendingOperations messageTemplate;
	
	// listen to all the session disconnect event
	
	@EventListener
	public void handleWebSockerDisconnectEvent(SessionDisconnectEvent event) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
		String username = (String) headerAccessor.getSessionAttributes().get("username");
		if (username == null) {
			log.info("User disconnected: {}", username);
			var chatMessage = ChatMessage.builder()
					.type(MessageType.LEAVER)
					.sender(username)
					.build();
			messageTemplate.convertAndSend("/topic/public", chatMessage);
		}
	}

}