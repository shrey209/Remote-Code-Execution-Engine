package com.Algomania.RCE_code_execution_engine.kafkaConfigs;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.Algomania.RCE_code_execution_engine.Modals.websocketDTO;






//configurations for the consumers

@Configuration
public class KafkaConsumerConfig 
{
	@Value(value = "${kafka.bootstrapAddress}")
	private String bootstrapAddress;

	@Value(value = "${code.topic.group.id}")
	private String websocketDTOGroupId;


	public ConsumerFactory<String,websocketDTO> websocketDTOConsumerFactory() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, websocketDTOGroupId);
		props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
		return new DefaultKafkaConsumerFactory<>(props, 
				new StringDeserializer(), 
				new JsonDeserializer<>(websocketDTO.class));
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, websocketDTO> 
	websocketDTOKafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, websocketDTO> factory 
			= new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(websocketDTOConsumerFactory());
		return factory;
	}
}
