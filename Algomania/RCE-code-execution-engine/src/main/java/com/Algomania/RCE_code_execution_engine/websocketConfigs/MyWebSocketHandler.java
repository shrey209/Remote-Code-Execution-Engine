package com.Algomania.RCE_code_execution_engine.websocketConfigs;

import com.Algomania.RCE_code_execution_engine.Modals.websocketDTO;
import com.Algomania.RCE_code_execution_engine.services.KafKaProducerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Component
public class MyWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    KafKaProducerService kafKaProducerService;
    
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(MyWebSocketHandler.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    // On connection generate a UUID and send it back to the user  
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String uuid = UUID.randomUUID().toString();
        session.getAttributes().put("uuid", uuid);
        sessions.put(uuid, session);
        logger.info("WebSocket session established. UUID: {}", uuid);
        session.sendMessage(new TextMessage("Your UUID is: " + uuid));
    }

    // User will send us the UUID and the code input together in form of websocketDTO
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        logger.info("Received message from user: {}", message.getPayload());
        try {
            // Converting the string object to websocket object 
            websocketDTO dto = objectMapper.readValue(message.getPayload(), websocketDTO.class);
            logger.info("Converted message to websocketDTO: {}", dto);
            
            kafKaProducerService.sendMessage(dto);
        } catch (IOException e) {
            logger.error("Failed to convert message to websocketDTO", e);
        }
    }

    // On closing the connection we will remove the related information from the map
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String uuid = (String) session.getAttributes().get("uuid");
        if (uuid != null) {
            sessions.remove(uuid);
            logger.info("WebSocket session closed. UUID: {}", uuid);
        }
    }

    public WebSocketSession getSession(String uuid) {
        return sessions.get(uuid);
    }

    public void removeSession(String uuid) {
        sessions.remove(uuid);
    }
}
