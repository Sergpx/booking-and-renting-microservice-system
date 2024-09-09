package org.sergp.bookingservice.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.sergp.bookingservice.kafka.listener.PaymentConfirmationEvent;
import org.sergp.bookingservice.kafka.listener.RefundSuccessMessage;
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

    @Bean("PaymentConfirmationEventFactory")
    public ConsumerFactory<String, PaymentConfirmationEvent> createPaymentConfirmationEventFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "org.sergp.bookingservice.kafka.listener.PaymentConfirmationEvent");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS,false);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(),
                new JsonDeserializer<>(PaymentConfirmationEvent.class));
    }

    @Bean("PaymentConfirmationEventContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, PaymentConfirmationEvent> createPaymentConfirmationEventContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PaymentConfirmationEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(createPaymentConfirmationEventFactory());

        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }


    @Bean("RefundSuccessMessageFactory")
    public ConsumerFactory<String, RefundSuccessMessage> createRefundSuccessMessageFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "org.sergp.bookingservice.kafka.listener.RefundSuccessMessage");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS,false);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(),
                new JsonDeserializer<>(RefundSuccessMessage.class));
    }

    @Bean("RefundSuccessMessageContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, RefundSuccessMessage> createRefundSuccessMessageContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, RefundSuccessMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(createRefundSuccessMessageFactory());

        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }



}
