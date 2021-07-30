package br.com.intelipost.kafka.consumer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.event.ConsumerPausedEvent;
import org.springframework.kafka.event.ConsumerStoppedEvent;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.intelipost.kafka.model.ConsumerIdentifier;
import br.com.intelipost.kafka.model.ManualPauseRequested;
import br.com.intelipost.kafka.model.ManualResumeRequested;
import br.com.intelipost.kafka.model.ManualStopRequested;
import br.com.intelipost.kafka.model.ProblemConsumingEvent;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PauseResumeService {

	@Autowired KafkaListenerEndpointRegistry registry;
	@Autowired KafkaTemplate<Object, Object> kafkaTemplate;
	@Autowired MeterRegistry meterRegistry;
	@Autowired Environment environment;

	private Map<String, AtomicInteger> gaugesState = new HashMap<>();
	private Map<String, ManualPauseRequested> manualControl = new HashMap<>();
	private Map<String, ManualStopRequested> stoppedManualControl = new HashMap<>();

	@Scheduled(fixedDelay = 120000, initialDelay = 60000)
	public void autoStartScheduler() {
		Collection<MessageListenerContainer> allListenerContainers = registry.getAllListenerContainers();
		for (MessageListenerContainer container : allListenerContainers) {
			String key = String.format("kafka.consumer.%s.auto.start", container.getListenerId());
			boolean autoStop = environment.getProperty(key, Boolean.class, false);
			if(!autoStop) continue;
			
			if (manualControl.containsKey(container.getListenerId())) {
				log.info("Consumer {} was paused manually and will not be started", container.getListenerId());
				continue;
			}
			if (container.isContainerPaused()) {
				log.info("Consumer {} is paused, it will be automatically started", container.getListenerId());
				container.resume();
				if (gaugesState.containsKey(container.getListenerId()))
					gaugesState.get(container.getListenerId()).set(0);
			}
		}
	}

	@EventListener
	public void problemConsumingEvent(ProblemConsumingEvent event) {
		ConsumerIdentifier identifier = event.consumerIdentifier();
		
		String key = String.format("kafka.consumer.%s.errors.threshold", identifier.getId());
		int threshold = environment.getProperty(key, Integer.class, Integer.MAX_VALUE);
		
		int gauge = gauge(identifier.getId()).incrementAndGet();
		if (gauge < threshold) {
			log.warn("Consumer erros is {}, it will not be pause because max is {}", gauge,
					threshold);
			return;
		}

		log.warn("Consumer`s {} error count is {} it will be paused", identifier.getId(), gauge);
		MessageListenerContainer listenerContainer = registry.getListenerContainer(identifier.getId());
		if (!listenerContainer.isPauseRequested()) {
			listenerContainer.pause();
		}
	}

	private AtomicInteger gauge(String id) {
		Gauge gauge = meterRegistry.find(id).tags("type", "errors").gauge();
		if (gauge == null) {
			gaugesState.put(id, new AtomicInteger(0));
		}
		meterRegistry.gauge(id, Tags.of("type", "errors"), gaugesState.get(id));

		return gaugesState.get(id);
	}

	@EventListener
	public void consumerPausedEvent(ConsumerPausedEvent event) {
		log.warn(event.toString());
	}

	@EventListener
	public void manualPauseRequested(ManualPauseRequested event) {
		MessageListenerContainer container = registry.getListenerContainer(event.getId());
		if (container != null) {
			container.pause();
			manualControl.put(event.getId(), event);
			log.info(event.toString());
		}
	}

	@EventListener
	public void consumerStopedEvent(ConsumerStoppedEvent event) {
		log.warn(event.toString());
	}
	
	@EventListener
	public void manualStopRequested(ManualStopRequested event) {
		MessageListenerContainer container = registry.getListenerContainer(event.getId());
		if (container != null) {
			container.stop();
			stoppedManualControl.put(event.getId(), event);
			log.info(event.toString());
		}
	}
	
	@EventListener
	public void manualResumeRequested(ManualResumeRequested event) {
		MessageListenerContainer container = registry.getListenerContainer(event.getId());
		if (container != null) {
			container.resume();
			
			if (manualControl.containsKey(event.getId()))
				manualControl.remove(event.getId());
			else
				stoppedManualControl.remove(event.getId());
			
			log.info(event.toString());
		}
	}
}
