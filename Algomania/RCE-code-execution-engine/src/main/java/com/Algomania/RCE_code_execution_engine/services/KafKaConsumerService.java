package com.Algomania.RCE_code_execution_engine.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.Algomania.RCE_code_execution_engine.Modals.CodeInput;
import com.Algomania.RCE_code_execution_engine.Modals.websocketDTO;
import com.Algomania.RCE_code_execution_engine.websocketConfigs.MyWebSocketHandler;

import java.io.IOException;

// consumer implementation
// core logic is to just call the url localhost:8081/code
// send the result using the websocket sessions from the map
@Service
public class KafKaConsumerService {
    private final Logger logger = LoggerFactory.getLogger(KafKaConsumerService.class);

	@Value(value = "${urlpart}")
     String urlpart;
    
    @Autowired
    private MyWebSocketHandler myWebSocketHandler;

    @Autowired
    private RestTemplate restTemplate;

    @KafkaListener(topics = "${code.topic.name}", 
                   groupId = "${code.topic.group.id}", 
                   containerFactory = "websocketDTOKafkaListenerContainerFactory", 
                   concurrency = "${num_partitions}")
    public void consume(websocketDTO websocketdto) {
        logger.info(String.format("Consumer created -> %s", websocketdto));

        CodeInput codeInput = new CodeInput(websocketdto.getCode(), websocketdto.getInput_data(), websocketdto.getLang());

        String url = "http://43.204.197.186:8080/code";
        String response = restTemplate.postForObject(url, codeInput, String.class);

        WebSocketSession session = myWebSocketHandler.getSession(websocketdto.getUuid());

        if (session != null && session.isOpen()) {
            try {
      
                session.sendMessage(new TextMessage(response));
                logger.info("Sent response to WebSocket session. UUID: {}", websocketdto.getUuid());
          
                session.close();
                logger.info("Closed WebSocket session. UUID: {}", websocketdto.getUuid());     
                myWebSocketHandler.removeSession(websocketdto.getUuid());
                logger.info("Removed WebSocket session from map. UUID: {}", websocketdto.getUuid());
            } catch (IOException e) {
                logger.error("Failed to send message to WebSocket session. UUID: {}", websocketdto.getUuid(), e);
            }
        } else {
            logger.warn("WebSocket session is not open or does not exist. UUID: {}", websocketdto.getUuid());
        }
        logger.info("Response from URL: " + response);
    }
}
