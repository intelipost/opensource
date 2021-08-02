package br.com.intelipost.kafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;

@EnableFeignClients
@SpringBootApplication
@EnableKafka
public class KafkaConsumerManagmentApplication {

	public static void main(String[] args) {
		SpringApplication.run(KafkaConsumerManagmentApplication.class, args);
	}

}
