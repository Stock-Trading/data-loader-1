package com.stocktrading.dataloader1.remote.kafkaeventpublisher;

import com.stocktrading.dataloader1.domain.model.FinancialInstrumentPriceModel;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private Map<String, Object> getProducerConfiguration() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
        return properties;
    }

    private ProducerFactory<String, FinancialInstrumentPriceModel> producerFactory() {
        return new DefaultKafkaProducerFactory<>(getProducerConfiguration());
    }

    @Bean
    KafkaTemplate<String, FinancialInstrumentPriceModel> financialInstrumentPriceModelKafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

}
