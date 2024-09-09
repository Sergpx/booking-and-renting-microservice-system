package org.sergp.paymentservice.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.sergp.paymentservice.kafka.listener.CanceledPaymentEvent;
import org.sergp.paymentservice.kafka.listener.InitiatePaymentCommand;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Bean("InitiatePaymentCommandConsumerFactory")
    public ConsumerFactory<String, InitiatePaymentCommand> createInitiatePaymentCommandConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "org.sergp.paymentservice.kafka.listener.InitiatePaymentCommand");// this my consumer event class
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS,false);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(),
                new JsonDeserializer<>(InitiatePaymentCommand.class));
    }
    @Bean("InitiatePaymentCommandContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, InitiatePaymentCommand> createInitiatePaymentCommandContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, InitiatePaymentCommand> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(createInitiatePaymentCommandConsumerFactory());

        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }


    @Bean("CanceledPaymentEventConsumerFactory")
    public ConsumerFactory<String, CanceledPaymentEvent> createCanceledPaymentEventConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "org.sergp.paymentservice.kafka.listener.CanceledPaymentEvent");// this my consumer event class
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS,false);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(),
                new JsonDeserializer<>(CanceledPaymentEvent.class));
    }
    @Bean("CanceledPaymentEventContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, CanceledPaymentEvent> createCanceledPaymentEventContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, CanceledPaymentEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(createCanceledPaymentEventConsumerFactory());

        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }



}
