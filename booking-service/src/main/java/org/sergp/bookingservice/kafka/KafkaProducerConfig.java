package org.sergp.bookingservice.kafka;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.sergp.bookingservice.kafka.message.CanceledPaymentEvent;
import org.sergp.bookingservice.kafka.message.InitiatePaymentCommand;
import org.sergp.bookingservice.kafka.message.RefundEmailNotification;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.transaction.KafkaTransactionManager;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public ProducerFactory<String, InitiatePaymentCommand> initiatePaymentProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);  // Сериализация объекта в JSON

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean(name = "InitiatePaymentCommandTemplate")
    public KafkaTemplate<String, InitiatePaymentCommand> initiatePaymentCommandTemplate() {
        return new KafkaTemplate<>(initiatePaymentProducerFactory());
    }

    @Bean
    public ProducerFactory<String, CanceledPaymentEvent> canceledPaymentEventProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);  // Сериализация объекта в JSON

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean(name = "CanceledPaymentEventTemplate")
    public KafkaTemplate<String, CanceledPaymentEvent> canceledPaymentEventTemplate() {
        return new KafkaTemplate<>(canceledPaymentEventProducerFactory());
    }

    @Bean
    public ProducerFactory<String, RefundEmailNotification> canceledRefundEmailNotificationProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);  // Сериализация объекта в JSON
        configProps.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "refund-email-notification-transactional-id");

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean(name = "RefundEmailNotificationTemplate")
    public KafkaTemplate<String, RefundEmailNotification> refundEmailNotificationTemplate() {
        KafkaTemplate<String, RefundEmailNotification> kafkaTemplate = new KafkaTemplate<>(canceledRefundEmailNotificationProducerFactory());
        kafkaTemplate.setTransactionIdPrefix("refund-email-");
        return kafkaTemplate;
    }

    @Bean
    public KafkaTransactionManager<String, RefundEmailNotification> RefundEmailNotificationKafkaTransactionManager(
        ProducerFactory<String, RefundEmailNotification> producerFactory){
        return new KafkaTransactionManager<>(producerFactory);
    }

}
