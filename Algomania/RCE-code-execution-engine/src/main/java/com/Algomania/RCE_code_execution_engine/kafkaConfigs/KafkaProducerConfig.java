package com.Algomania.RCE_code_execution_engine.kafkaConfigs;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.Algomania.RCE_code_execution_engine.Modals.websocketDTO;




// configurations for producers 


@Configuration
public class KafkaProducerConfig 
{
	@Value(value = "${kafka.bootstrapAddress}")
    private String bootstrapAddress;
	

	@Bean
    public ProducerFactory<String, websocketDTO> websocketDTOProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String,websocketDTO> websocketKafkaTemplate() {
        return new KafkaTemplate<>(websocketDTOProducerFactory());
    }
}
