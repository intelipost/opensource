package br.com.intelipost.kafka.model;

import java.time.Instant;

import lombok.Value;

@Value
public class SeekToTimestamp {

	String consumerId;
	Instant timestamp;

	public long getTimestamp() {
		return timestamp.toEpochMilli();
	}

}
