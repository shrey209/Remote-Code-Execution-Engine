package com.Algomania.RCE_code_execution_engine.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import com.Algomania.RCE_code_execution_engine.Modals.websocketDTO;

import java.util.concurrent.CompletableFuture;

//a simple producer publishng the values 

@Service
public class KafKaProducerService {

    private static final Logger logger = LoggerFactory.getLogger(KafKaProducerService.class);

    @Value(value = "${code.topic.name}")
    private String topicName;

    @Autowired
    private KafkaTemplate<String, websocketDTO> kafkaTemplate;

    public void sendMessage(websocketDTO dto) {
        CompletableFuture<SendResult<String, websocketDTO>> future = kafkaTemplate.send(topicName, dto);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                logger.info("Sent message: {} with offset: {}", dto, result.getRecordMetadata().offset());
            } else {
                logger.error("Unable to send message: {}", dto, ex);
            }
        });
    }
}
