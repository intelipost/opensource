package br.com.intelipost.kafka.consumer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import br.com.intelipost.kafka.model.SeekToBeginning;
import br.com.intelipost.kafka.model.SeekToTimestamp;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SeekOffsetService {

	@Autowired
	private List<KafkaService> kafkaServices;

	@EventListener
	public void seekToBeggining(SeekToBeginning event) {
		for (KafkaService kafkaService : kafkaServices) {
			if (kafkaService.consumerId().equals(event.getConsumerId())) {
				log.info(event.toString());
				kafkaService.seekToBeginning();
			}
		}
	}

	@EventListener
	public void seekToTimestamp(SeekToTimestamp event) {
		for (KafkaService kafkaService : kafkaServices) {
			if (kafkaService.consumerId().equals(event.getConsumerId())) {
				log.info(event.toString());
				kafkaService.seekToTimestamp(event.getTimestamp());
			}
		}
	}
}