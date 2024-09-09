package org.sergp.paymentservice.kafka;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.sergp.paymentservice.kafka.message.PaymentLinkEmailNotification;
import org.sergp.paymentservice.kafka.message.PaymentConfirmationEvent;
import org.sergp.paymentservice.kafka.message.RefundSuccessMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public ProducerFactory<String, PaymentConfirmationEvent> paymentConfirmationEventProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);  // Сериализация объекта в JSON

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean(name = "PaymentConfirmationEventTemplate")
    public KafkaTemplate<String, PaymentConfirmationEvent> paymentConfirmationEventTemplate() {
        return new KafkaTemplate<>(paymentConfirmationEventProducerFactory());
    }

    @Bean
    public KafkaSender<String, PaymentLinkEmailNotification> paymentLinkEmailNotificationKafkaSender() {
        Map<String, Object> producerProps = Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class,
                ProducerConfig.ACKS_CONFIG, "all"
        );

        SenderOptions<String, PaymentLinkEmailNotification> senderOptions = SenderOptions.create(producerProps);
        return KafkaSender.create(senderOptions);
    }

    @Bean
    public KafkaSender<String, RefundSuccessMessage> refundSuccessMessageKafkaSender() {
        Map<String, Object> producerProps = Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class,
                ProducerConfig.ACKS_CONFIG, "all"
        );

        SenderOptions<String, RefundSuccessMessage> senderOptions = SenderOptions.create(producerProps);
        return KafkaSender.create(senderOptions);
    }
}
