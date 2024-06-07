package com.Algomania.RCE_code_execution_engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@EnableKafka
@EnableWebSocket
@SpringBootApplication
public class RceCodeExecutionEngineApplication {

	public static void main(String[] args) {
		SpringApplication.run(RceCodeExecutionEngineApplication.class, args);
	}

}
