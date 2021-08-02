package br.com.intelipost.kafka.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;

import br.com.intelipost.kafka.consumer.KafkaService;
import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.subject.RecordNameStrategy;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroDeserializer;

@Configuration
public class KafkaConfiguration {

	@Value("${spring.kafka.properties.schema.registry.url}")
	String endPoint;
	@Value("${spring.kafka.properties.auto.register.schemas}")
	String autoRegisterSchemas;

	@Value("${spring.kafka.properties.bootstrap.servers}")
	String bootstrapServers;
	@Value("${spring.kafka.properties.sasl.mechanism}")
	String saslMechanism;
	@Value("${spring.kafka.properties.sasl.jaas.config}")
	String saslJaasConfig;
	@Value("${spring.kafka.properties.security.protocol}")
	String securityProtocol;

	@Bean
	public List<KafkaService> services() {
		return new ArrayList<KafkaService>();
	}
	
	@Bean ErrorHandler error() {
		return new ErrorHandler() {
			
			@Override
			public void handle(Exception thrownException, ConsumerRecord<?, ?> data) {
				new Throwable("Error", thrownException);
				
			}
		};
	}
	
	@Bean
	public SchemaRegistryClient schemaRegistryClient() {
		return new CachedSchemaRegistryClient(endPoint, 100);
	}

	@Bean
	public ProducerFactory<Object, Object> producerFactory() {
		return new DefaultKafkaProducerFactory<>(producerConfigs());
	}

	@Bean
	public Map<String, Object> producerConfigs() {
		Map<String, Object> props = new HashMap<>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
		props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
		props.put("sasl.mechanism", saslMechanism);
		props.put("sasl.jaas.config", saslJaasConfig);
		props.put("security.protocol", securityProtocol);
		props.put(KafkaAvroDeserializerConfig.AUTO_REGISTER_SCHEMAS, autoRegisterSchemas);
		props.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, endPoint);
		props.put("key.subject.name.strategy", RecordNameStrategy.class.getName());
		props.put("value.subject.name.strategy", RecordNameStrategy.class.getName());
		return props;
	}

	@Bean
	public KafkaTemplate<Object, Object> kafkaTemplate() {
		return new KafkaTemplate<Object, Object>(producerFactory());
	}

	@Bean
	KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, Object>> kafkaListenerContainerFactory(
			ErrorHandler noBlockingRetryHandler) {
		ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory());
		factory.setConcurrency(3);
		factory.getContainerProperties().setPollTimeout(1000);
		factory.setErrorHandler(noBlockingRetryHandler);
		return factory;
	}

	@Bean
	public ConsumerFactory<Object, Object> consumerFactory() {
		return new DefaultKafkaConsumerFactory<Object, Object>(consumerConfigs());
	}

	@Bean
	public Map<String, Object> consumerConfigs() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
		props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, SpecificAvroDeserializer.class);
		props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, SpecificAvroDeserializer.class);
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
		props.put("sasl.mechanism", saslMechanism);
		props.put("sasl.jaas.config", saslJaasConfig);
		props.put("security.protocol", securityProtocol);
		props.put(KafkaAvroDeserializerConfig.AUTO_REGISTER_SCHEMAS, false);
		props.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, endPoint);
		props.put("key.subject.name.strategy", RecordNameStrategy.class.getName());
		props.put("value.subject.name.strategy", RecordNameStrategy.class.getName());
		return props;
	}

	@Bean
	public KafkaAvroSerializer keySerializer(SchemaRegistryClient schemaRegistryClient) {
		final KafkaAvroSerializer kafkaAvroSerializer = new KafkaAvroSerializer(schemaRegistryClient,
				producerConfigs());
		kafkaAvroSerializer.configure(producerConfigs(), true);
		return kafkaAvroSerializer;
	}

	@Bean
	public KafkaAvroSerializer valueSerializer(SchemaRegistryClient schemaRegistryClient) {
		final KafkaAvroSerializer kafkaAvroSerializer = new KafkaAvroSerializer(schemaRegistryClient,
				producerConfigs());
		kafkaAvroSerializer.configure(producerConfigs(), false);
		return kafkaAvroSerializer;
	}

	@Bean
	public KafkaAvroDeserializer keyDeserializer(SchemaRegistryClient schemaRegistryClient) {
		final KafkaAvroDeserializer kafkaAvroSerializer = new KafkaAvroDeserializer(schemaRegistryClient,
				consumerConfigs());
		kafkaAvroSerializer.configure(consumerConfigs(), true);
		return kafkaAvroSerializer;
	}
	

	@Bean
	public KafkaAvroDeserializer valueDeserializer(SchemaRegistryClient schemaRegistryClient) {
		final KafkaAvroDeserializer kafkaAvroSerializer = new KafkaAvroDeserializer(schemaRegistryClient,
				consumerConfigs());
		kafkaAvroSerializer.configure(consumerConfigs(), false);
		return kafkaAvroSerializer;
	}
}
