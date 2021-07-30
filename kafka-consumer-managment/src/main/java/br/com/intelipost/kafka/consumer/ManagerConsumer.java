package br.com.intelipost.kafka.consumer;

import java.io.IOException;

import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import br.com.intelipost.kafka.model.ManualPauseRequested;
import br.com.intelipost.kafka.model.ManualResumeRequested;
import br.com.intelipost.kafka.model.RequestPauseConsumer;
import br.com.intelipost.kafka.model.RequestResumeConsumer;
import br.com.intelipost.kafka.model.RequestStopConsumer;
import br.com.intelipost.kafka.model.ResetOffsetToEarliest;
import br.com.intelipost.kafka.model.ResetOffsetToTimestamp;
import br.com.intelipost.kafka.model.SeekToBeginning;
import br.com.intelipost.kafka.model.SeekToTimestamp;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ManagerConsumer {

	@Autowired ApplicationEventPublisher applicationEventPublisher;
	
	@KafkaListener(id = "${spring.application.name}-${spring.cloud.client.hostname}", 
			topics = "${kafka.managment.topic.name:consumers.controller}", 
			concurrency = "${kafka.managment.topic.concurrency:1}")
	public void avroConsumer(ConsumerRecord<SpecificRecord, SpecificRecord> record) throws IOException {
		if(record.value() instanceof ResetOffsetToEarliest) {
			ResetOffsetToEarliest cmd = (ResetOffsetToEarliest) record.value();
			applicationEventPublisher.publishEvent(new SeekToBeginning(cmd.getConsumerId()));
		} else if(record.value() instanceof ResetOffsetToTimestamp) {
			ResetOffsetToTimestamp cmd = (ResetOffsetToTimestamp) record.value();
			applicationEventPublisher.publishEvent(new SeekToTimestamp(cmd.getConsumerId(), cmd.getTimestamp()));
		} else if(record.value() instanceof RequestPauseConsumer) {
			RequestPauseConsumer cmd = (RequestPauseConsumer) record.value();
			applicationEventPublisher.publishEvent(new ManualPauseRequested(cmd.getConsumerId()));
		} else if(record.value() instanceof RequestResumeConsumer) {
			RequestResumeConsumer cmd = (RequestResumeConsumer) record.value();
			applicationEventPublisher.publishEvent(new ManualResumeRequested(cmd.getConsumerId()));
		} else if (record.value() instanceof RequestStopConsumer){
			RequestStopConsumer cmd = (RequestStopConsumer) record.value();
			applicationEventPublisher.publishEvent(new ManualResumeRequested(cmd.getConsumerId()));
		} else {
			log.warn("Unknown type {}", record.value().getSchema().getName());
		}
	}
	
}
