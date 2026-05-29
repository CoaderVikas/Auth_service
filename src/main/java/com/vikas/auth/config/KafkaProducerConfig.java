package com.vikas.auth.config;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

/**
 * Class      : KafkaProducerConfig
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Mar 7, 2026
 * Version    : 1.0
 */

@Configuration
public class KafkaProducerConfig {

	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServers;

	@Value("${spring.kafka.properties.sasl.jaas.config}")
	private String jaasConfig;

	@Bean
	public ProducerFactory<String, Object> producerFactory() {
		Map<String, Object> config = new HashMap<>();

		config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		config.put("security.protocol", "SASL_SSL");
		config.put("sasl.mechanism", "SCRAM-SHA-256");
		config.put("sasl.jaas.config", jaasConfig);
		config.put("ssl.truststore.type", "PEM");
		config.put("ssl.truststore.location", getCertFilePath());

		return new DefaultKafkaProducerFactory<>(config);
	}

	@Bean
	public KafkaTemplate<String, Object> kafkaTemplate() {
		return new KafkaTemplate<>(producerFactory());
	}

	/**
	 * Private method to read ca.pem from resources and create a temporary file.
	 * This ensures the file is readable even when running from a JAR on Render.
	 */
	private String getCertFilePath() {
		try {
			InputStream inputStream = getClass().getClassLoader().getResourceAsStream("ca.pem");
			if (inputStream == null) {
				throw new RuntimeException("Certificate file 'ca.pem' not found in resources folder");
			}

			File tempFile = File.createTempFile("kafka-ca", ".pem");
			tempFile.deleteOnExit();

			Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

			return tempFile.getAbsolutePath();
		} catch (Exception e) {
			throw new RuntimeException("Failed to load Kafka SSL certificate", e);
		}
	}
}