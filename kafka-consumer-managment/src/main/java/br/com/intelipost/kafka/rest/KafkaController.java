package br.com.intelipost.kafka.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.intelipost.kafka.consumer.LagAnalyzerService;
import br.com.intelipost.kafka.model.RequestPauseConsumer;
import br.com.intelipost.kafka.model.RequestResumeConsumer;
import br.com.intelipost.kafka.model.RequestStartConsumer;
import br.com.intelipost.kafka.model.RequestStopConsumer;
import br.com.intelipost.kafka.rest.body.LaginRequestBody;
import br.com.intelipost.kafka.rest.body.LaginResponseBody;
import br.com.intelipost.kafka.rest.body.PauseRequestBody;
import br.com.intelipost.kafka.rest.body.PauseResponseBody;
import br.com.intelipost.kafka.rest.body.ResumeRequestBody;
import br.com.intelipost.kafka.rest.body.ResumeResponseBody;
import br.com.intelipost.kafka.rest.body.StartRequestBody;
import br.com.intelipost.kafka.rest.body.StartResponseBody;
import br.com.intelipost.kafka.rest.body.StopResponseBody;

@RestController
public class KafkaController {

	private static final String RESUME_REQUESTED = "RESUME_REQUESTED";
	private static final String PAUSE_REQUESTED = "PAUSE_REQUESTED";
	private static final String START_REQUESTED = "START_REQUESTED";
	private static final String NOT_FOUND = "NOT_FOUND";
	private static final String PAUSED = "PAUSED";
	private static final String CONSUMERS_CONTROLLER_TOPIC = "consumers.controller";
	
	private static final String STOPPED = "STOPPED";
	private static final String STOP_REQUESTED = "STOP_REQUESTED";
	
	
	@Autowired KafkaListenerEndpointRegistry registry;
	@Autowired KafkaTemplate<Object, Object> kafkaTemplate;
	@Autowired LagAnalyzerService lagAnalyzerService;
	
	@GetMapping(value = "/kafka/consumers", produces = "application/json")
	public ResponseEntity<List<String>> consumers(){
		return ResponseEntity.ok(registry
				.getAllListenerContainers()
				.stream()
				.map(c -> c.getListenerId() + ":" + (c.isContainerPaused() ? PAUSED : (c.isPauseRequested() ? "PAUSE_REQUESTD" : "RUNNING")))
				.collect(Collectors.toList()));
	}
	
	@PostMapping(value = "/kafka/pause", produces = "application/json")
	public ResponseEntity<List<PauseResponseBody>> pause(@RequestBody List<PauseRequestBody> body){
		if(body == null)
			return ResponseEntity.badRequest().build();
		List<PauseResponseBody> responses = new ArrayList<>(body.size());
		for (PauseRequestBody pause : body) {
			MessageListenerContainer container = registry.getListenerContainer(pause.getId());
			if(container == null) {
				responses.add(new PauseResponseBody(pause.getId(), NOT_FOUND));
				continue;
			};
			if(container.isContainerPaused())
				responses.add(new PauseResponseBody(pause.getId(), PAUSED));
			else if(container.isPauseRequested())
				responses.add(new PauseResponseBody(pause.getId(), PAUSE_REQUESTED));
			else {
				kafkaTemplate.send(CONSUMERS_CONTROLLER_TOPIC, RequestPauseConsumer.newBuilder()//
						.setConsumerId(pause.getId()) //
						.build());
				responses.add(new PauseResponseBody(pause.getId(), PAUSE_REQUESTED));
			}
		}
		return ResponseEntity.ok(responses);
	}
	
	@PostMapping(value = "/kafka/stop", produces = "application/json")
	public ResponseEntity<List<StopResponseBody>> stop(@RequestBody List<StopResponseBody> body){
		if(body == null)
			return ResponseEntity.badRequest().build();
		List<StopResponseBody> responses = new ArrayList<>(body.size());
		for (StopResponseBody stop : body) {
			MessageListenerContainer container = registry.getListenerContainer(stop.getId());
			if(container == null) {
				responses.add(new StopResponseBody(stop.getId(), NOT_FOUND));
				continue;
			};
			if(!container.isRunning())
				responses.add(new StopResponseBody(stop.getId(), STOPPED));
			else {
				kafkaTemplate.send(CONSUMERS_CONTROLLER_TOPIC, RequestStopConsumer.newBuilder()//
						.setConsumerId(stop.getId()) //
						.build());
				responses.add(new StopResponseBody(stop.getId(), STOP_REQUESTED));
			}
		}
		return ResponseEntity.ok(responses);
	}
	
	@PostMapping(value = "/kafka/start", produces = "application/json")
	public ResponseEntity<List<StartResponseBody>> start(@RequestBody List<StartRequestBody> body){
		if(body == null)
			return ResponseEntity.badRequest().build();
		List<StartResponseBody> responses = new ArrayList<>(body.size());
		
		for (StartRequestBody start : body) {
			MessageListenerContainer container = registry.getListenerContainer(start.getId());
			if(container == null) {
				responses.add(new StartResponseBody(start.getId(), NOT_FOUND));
				continue;
			}
			if(!container.isRunning()) {
				kafkaTemplate.send(CONSUMERS_CONTROLLER_TOPIC, RequestStartConsumer.newBuilder()//
						.setConsumerId(container.getListenerId()) //
						.build());
				
				responses.add(new StartResponseBody(container.getListenerId(), START_REQUESTED));
			} else {
				responses.add(new StartResponseBody(container.getListenerId(), "STARTED"));
			}
		}
		return ResponseEntity.ok(responses);
	}
	
	@PostMapping(value="kafka/consumer-lag", produces="application/json")
	public ResponseEntity<List<LaginResponseBody>> lagin(@RequestBody List<LaginRequestBody> body) throws InterruptedException, ExecutionException{
		if(body == null)
			return ResponseEntity.badRequest().build();
		List<LaginResponseBody> responses = new ArrayList<>(body.size());
		
		for (LaginRequestBody lagin : body) {
			MessageListenerContainer container = registry.getListenerContainer(lagin.getId());
			if(container == null) {
				responses.add(new LaginResponseBody(lagin.getId(), new HashMap<>()));
				continue;
			}
			
			responses.add(new LaginResponseBody(lagin.getId(), lagAnalyzerService.analyzeLag(lagin.getId())));
		}
		return ResponseEntity.ok(responses);
	}
	
	@PostMapping(value = "/kafka/pause/all", produces = "application/json")
	public ResponseEntity<List<PauseResponseBody>> pauseAll(){
		List<PauseResponseBody> responses = new ArrayList<>(registry.getAllListenerContainers().size());
		
		Collection<MessageListenerContainer> containers = registry.getAllListenerContainers();
		for (MessageListenerContainer container : containers) {
			if(container.isContainerPaused())
				responses.add(new PauseResponseBody(container.getListenerId(), PAUSED));
			else if(container.isPauseRequested())
				responses.add(new PauseResponseBody(container.getListenerId(), PAUSE_REQUESTED));
			else {
				kafkaTemplate.send(CONSUMERS_CONTROLLER_TOPIC, RequestPauseConsumer.newBuilder()//
						.setConsumerId(container.getListenerId()) //
						.build());
				responses.add(new PauseResponseBody(container.getListenerId(), PAUSE_REQUESTED));
			}
		}
		return ResponseEntity.ok(responses);
	}
	
	@PostMapping(value = "/kafka/resume", produces = "application/json")
	public ResponseEntity<List<ResumeResponseBody>> resume(@RequestBody List<ResumeRequestBody> body){
		if(body == null)
			return ResponseEntity.badRequest().build();
		List<ResumeResponseBody> responses = new ArrayList<>(body.size());
		
		for (ResumeRequestBody resume : body) {
			MessageListenerContainer container = registry.getListenerContainer(resume.getId());
			if(container == null) {
				responses.add(new ResumeResponseBody(resume.getId(), NOT_FOUND));
				continue;
			};
			if(container.isContainerPaused() || container.isPauseRequested()) {
				kafkaTemplate.send(CONSUMERS_CONTROLLER_TOPIC, RequestResumeConsumer.newBuilder()//
						.setConsumerId(container.getListenerId()) //
						.build());
				
				responses.add(new ResumeResponseBody(container.getListenerId(), RESUME_REQUESTED));
			} else {
				responses.add(new ResumeResponseBody(container.getListenerId(), "RUNNING"));
			}
			
		}
		return ResponseEntity.ok(responses);
	}
	
	@PostMapping(value = "/kafka/resume/all", produces = "application/json")
	public ResponseEntity<List<ResumeResponseBody>> resumeAll(){
		List<ResumeResponseBody> responses = new ArrayList<>(registry.getAllListenerContainers().size());
		
		Collection<MessageListenerContainer> containers = registry.getAllListenerContainers();
		for (MessageListenerContainer container : containers) {
			if(container.isContainerPaused() || container.isPauseRequested()) {
				kafkaTemplate.send(CONSUMERS_CONTROLLER_TOPIC, RequestResumeConsumer.newBuilder()//
						.setConsumerId(container.getListenerId()) //
						.build());
				
				responses.add(new ResumeResponseBody(container.getListenerId(), RESUME_REQUESTED));
			} else {
				responses.add(new ResumeResponseBody(container.getListenerId(), "RUNNING"));
			}
		}
		return ResponseEntity.ok(responses);
	}
	
}
