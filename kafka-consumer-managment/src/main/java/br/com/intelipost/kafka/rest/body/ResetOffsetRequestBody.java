package br.com.intelipost.kafka.rest.body;

import java.time.Instant;

import lombok.Data;

@Data
public class ResetOffsetRequestBody {
	
	private String id;
	private Instant timestamp;
}