package br.com.intelipost.kafka.rest.body;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StopResponseBody {

	private String id;
	private String message;
}
