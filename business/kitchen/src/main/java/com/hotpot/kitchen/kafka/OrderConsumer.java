package com.hotpot.kitchen.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author Peter
 * @date 2023/3/24
 * @description
 */
@Slf4j
@Component
public class OrderConsumer {

    @KafkaListener(topics = {"createOrder"}, groupId = "kitchenGroup")
    public void onOrder(ConsumerRecord<String , Object> consumerRecord) {
        log.info("consuming: {} - {} - {}", consumerRecord.topic(), consumerRecord.partition(), consumerRecord.value());
    }

}
