package br.com.intelipost.kafka.rest.body;

import java.util.Map;

import org.apache.kafka.common.TopicPartition;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LaginResponseBody {
	
	private String id;
	private Map<TopicPartition, Long> lagIn;
}
