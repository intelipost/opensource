package br.com.intelipost.kafka.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.intelipost.kafka.model.ResetOffsetToEarliest;
import br.com.intelipost.kafka.model.ResetOffsetToTimestamp;
import br.com.intelipost.kafka.rest.body.ResetOffsetRequestBody;

@RestController
public class KafkaOffsetController {

	private static final String CONSUMERS_CONTROLLER_TOPIC = "consumers.controller";
	
	@Autowired KafkaListenerEndpointRegistry registry;
	@Autowired KafkaTemplate<Object, Object> kafkaTemplate;
	
	
	@PostMapping(value = "/kafka/seek_to_beggining", produces = "application/json")
	public ResponseEntity<?> seekToBeggining(@RequestBody List<ResetOffsetRequestBody> consumers) {
		if(consumers == null) return ResponseEntity.badRequest().build();
		
		for (ResetOffsetRequestBody consumerIdentifierDTO : consumers) {
			kafkaTemplate.send(CONSUMERS_CONTROLLER_TOPIC, ResetOffsetToEarliest.newBuilder()//
					.setConsumerId(consumerIdentifierDTO.getId()) //
					.build());
		}
		return ResponseEntity.accepted().build();
	}
	
	@PostMapping(value = "/kafka/seek_to_timestamp", produces = "application/json")
	public ResponseEntity<?> seekToTimestamp(@RequestBody List<ResetOffsetRequestBody> consumers) {
		if(consumers == null) return ResponseEntity.badRequest().body("At least one consumer must be set");
		
		if(!consumers.stream().allMatch(cmd -> cmd.getId() != null || cmd.getTimestamp() != null))
			return ResponseEntity.badRequest().body("Id and timestamp must be set in all consumers");
			
		for (ResetOffsetRequestBody cmd : consumers) {
			kafkaTemplate.send(CONSUMERS_CONTROLLER_TOPIC, ResetOffsetToTimestamp.newBuilder()//
					.setConsumerId(cmd.getId()) //
					.setTimestamp(cmd.getTimestamp())
					.build());
		}
		return ResponseEntity.accepted().build();
	}
}
