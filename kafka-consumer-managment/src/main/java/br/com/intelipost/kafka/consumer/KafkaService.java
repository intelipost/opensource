package br.com.intelipost.kafka.consumer;

import org.springframework.kafka.listener.AbstractConsumerSeekAware;

public abstract class KafkaService extends AbstractConsumerSeekAware {

	public abstract String consumerId();

}