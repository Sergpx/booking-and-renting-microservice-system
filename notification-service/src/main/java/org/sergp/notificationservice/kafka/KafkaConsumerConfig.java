package org.sergp.notificationservice.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.sergp.notificationservice.kafka.listener.PaymentLinkEmailNotification;
import org.sergp.notificationservice.kafka.listener.RefundEmailNotification;
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

    @Bean("PaymentLinkEmailNotificationConsumerFactory")
    public ConsumerFactory<String, PaymentLinkEmailNotification> createPaymentLinkEmailNotificationConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "org.sergp.notificationservice.kafka.listener.PaymentLinkEmailNotification");// this my consumer event class
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS,false);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(),
                new JsonDeserializer<>(PaymentLinkEmailNotification.class));
    }

    @Bean("PaymentLinkEmailNotificationContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, PaymentLinkEmailNotification> createPaymentLinkEmailNotificationContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PaymentLinkEmailNotification> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(createPaymentLinkEmailNotificationConsumerFactory());

        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }

    @Bean("RefundEmailNotificationConsumerFactory")
    public ConsumerFactory<String, RefundEmailNotification> createRefundEmailNotificationConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "org.sergp.notificationservice.kafka.listener.RefundEmailNotification");// this my consumer event class
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS,false);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(),
                new JsonDeserializer<>(RefundEmailNotification.class));
    }

    @Bean("RefundEmailNotificationContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, RefundEmailNotification> createRefundEmailNotificationContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, RefundEmailNotification> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(createRefundEmailNotificationConsumerFactory());

        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }



}
