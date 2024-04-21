package com.stocktrading.dataloader1.remote.kafkaEventPublisher;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    public Map<String, Object> getProducerConfiguration() {
        Map<String,Object> properties = new HashMap<>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return properties;
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(getProducerConfiguration());
    }

//    public Map<String, Object> getStockDataProducerConfiguration() {
//        Map<String,Object> properties = new HashMap<>();
//        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class); //TODO zobaczyć czy można protobuf
//        return properties;
//    }

//    @Bean
//    public ProducerFactory<String, TimeSeriesDaily> stockDataProducerFactory() {
//        return new DefaultKafkaProducerFactory<>(getStockDataProducerConfiguration());
//    }
//
    @Bean
    public KafkaTemplate<String, String> kafkaTemplateFactory(ProducerFactory<String, String> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
//
//    @Bean
//    public KafkaTemplate<String, TimeSeriesDaily> timeSeriesDailyKafkaTemplate() {
//        return new KafkaTemplate<>(stockDataProducerFactory());
//    }

}
