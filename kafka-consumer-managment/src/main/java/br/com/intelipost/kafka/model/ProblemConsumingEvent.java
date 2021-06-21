package br.com.intelipost.kafka.model;

import static com.google.common.base.Preconditions.checkNotNull;

import org.springframework.context.ApplicationEvent;

public class ProblemConsumingEvent extends ApplicationEvent{
	private static final long serialVersionUID = 986084332360915658L;

	public ProblemConsumingEvent(ConsumerIdentifier consumerIdentifier) {
		super(checkNotNull(consumerIdentifier, "consumerIdentifier"));
	}

	public ConsumerIdentifier consumerIdentifier() {
		return (ConsumerIdentifier) getSource();
	}
}
