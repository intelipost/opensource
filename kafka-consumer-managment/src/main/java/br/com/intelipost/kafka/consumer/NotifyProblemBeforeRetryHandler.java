package br.com.intelipost.kafka.consumer;

import java.util.List;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.listener.ErrorHandler;
import org.springframework.kafka.listener.MessageListenerContainer;

import br.com.intelipost.kafka.model.ConsumerIdentifier;
import br.com.intelipost.kafka.model.ProblemConsumingEvent;

public class NotifyProblemBeforeRetryHandler implements ErrorHandler {

	private ApplicationEventPublisher applicationEventPublisher;
	private ErrorHandler delegate;

	public NotifyProblemBeforeRetryHandler(ApplicationEventPublisher applicationEventPublisher, ErrorHandler delegate) {
		this.applicationEventPublisher = applicationEventPublisher;
		this.delegate = delegate;
	}
	
	@Override
	public void handle(Exception thrownException, List<ConsumerRecord<?, ?>> records, Consumer<?, ?> consumer,
			MessageListenerContainer container) {
		
		applicationEventPublisher.publishEvent(new ProblemConsumingEvent(new ConsumerIdentifier(container.getListenerId())));
		this.delegate.handle(thrownException, records, consumer, container);
	}

	@Override
	public void handle(Exception thrownException, ConsumerRecord<?, ?> data) {
		this.delegate.handle(thrownException, data);
	}

}
