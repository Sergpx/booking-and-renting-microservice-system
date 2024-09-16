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
        configProps.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "init-payment-transaction-id");
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");  // Гарантирует, что все реплики получили запись
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);  // Отключает дублирование сообщений

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean(name = "InitiatePaymentCommandTemplate")
    public KafkaTemplate<String, InitiatePaymentCommand> initiatePaymentCommandTemplate() {
        KafkaTemplate<String, InitiatePaymentCommand> kafkaTemplate = new KafkaTemplate<>(initiatePaymentProducerFactory());
        kafkaTemplate.setTransactionIdPrefix("init-payment-");
        return kafkaTemplate;
    }

    @Bean
    public KafkaTransactionManager<String, InitiatePaymentCommand> InitiatePaymentCommandKafkaTransactionManager(
            ProducerFactory<String, InitiatePaymentCommand> producerFactory){
        return new KafkaTransactionManager<>(producerFactory);
    }


    @Bean
    public ProducerFactory<String, CanceledPaymentEvent> canceledPaymentEventProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);  // Сериализация объекта в JSON
        configProps.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "canceled-payment-transaction-id");
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");  // Гарантирует, что все реплики получили запись
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);  // Отключает дублирование сообщений

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean(name = "CanceledPaymentEventTemplate")
    public KafkaTemplate<String, CanceledPaymentEvent> canceledPaymentEventTemplate() {
        KafkaTemplate<String, CanceledPaymentEvent> kafkaTemplate = new KafkaTemplate<>(canceledPaymentEventProducerFactory());
        kafkaTemplate.setTransactionIdPrefix("canceled-payment-");
        return kafkaTemplate;
    }

    @Bean(name = "CanceledPaymentEventKafkaTransactionManager")
    public KafkaTransactionManager<String, CanceledPaymentEvent> CanceledPaymentEventKafkaTransactionManager(
            ProducerFactory<String, CanceledPaymentEvent> producerFactory){
        return new KafkaTransactionManager<>(producerFactory);
    }



    @Bean
    public ProducerFactory<String, RefundEmailNotification> canceledRefundEmailNotificationProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);  // Сериализация объекта в JSON
        configProps.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "refund-email-notification-transactional-id");
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");  // Гарантирует, что все реплики получили запись
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);  // Отключает дублирование сообщений

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean(name = "RefundEmailNotificationTemplate")
    public KafkaTemplate<String, RefundEmailNotification> refundEmailNotificationTemplate() {
        KafkaTemplate<String, RefundEmailNotification> kafkaTemplate = new KafkaTemplate<>(canceledRefundEmailNotificationProducerFactory());
        kafkaTemplate.setTransactionIdPrefix("refund-email-");
        return kafkaTemplate;
    }

    @Bean(name = "RefundEmailNotificationKafkaTransactionManager")
    public KafkaTransactionManager<String, RefundEmailNotification> RefundEmailNotificationKafkaTransactionManager(
        ProducerFactory<String, RefundEmailNotification> producerFactory){
        return new KafkaTransactionManager<>(producerFactory);
    }

}
