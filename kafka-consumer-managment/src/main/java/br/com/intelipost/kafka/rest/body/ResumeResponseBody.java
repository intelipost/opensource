package br.com.intelipost.kafka.rest.body;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResumeResponseBody {
	
	private String id;
	private String message;
}