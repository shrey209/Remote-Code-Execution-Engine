package com.Algomania.RCE_code_execution_engine.kafkaConfigs;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import lombok.val;


// to create topic in kafka 

@Configuration
public class TopicConfigs {

	@Value(value = "${kafka.bootstrapAddress}")
	private String bootstrapAddress;
	
	@Value(value = "${code.topic.name}")
	private String TopicName;
	
	@Value(value="${num_partitions}")
	private int parts;
	
	@Bean
	public NewTopic generalTopic() {
		return TopicBuilder.name(TopicName)
			      .partitions(parts)
			      .replicas(1)
			      .build();
	}
	
}
