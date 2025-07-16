package com.hotpot.order.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;

/**
 * @author Peter
 * @date 2023/3/24
 * @description
 */
@Slf4j
@Service
public class OrderService {

    @Autowired
    private KafkaTemplate<String, OrderDTO> kafkaTemplate;
    private record FoodDTO(String name, Map<String, List<String>> options){}
    private record OrderedFoodDTO(List<Map<String, Object>> orderedFoods){}
    private record OrderDTO(String orderId, Date orderDate, List<OrderedFoodDTO> foods){}

    public void createOrder() {
        Map<String, Object> data = Maps.newHashMap();
        data.put("food", new FoodDTO("pho", Maps.newHashMap()));
        data.put("remarks", "spicy");
        data.put("num", 1);
        List<Map<String, Object>> orderedFoods = Lists.newArrayList(data);
        OrderDTO orderDTO = new OrderDTO(UUID.randomUUID().toString(), new Date(), Lists.newArrayList(new OrderedFoodDTO(orderedFoods)));
        log.info("user: {} ordered : {}", SecurityContextHolder.getContext().getAuthentication().getName(), orderDTO);
        CompletableFuture<SendResult<String, OrderDTO>> order = kafkaTemplate.send("createOrder", orderDTO);
        order.whenComplete((stringObjectSendResult, throwable) -> {
            ProducerRecord<String, OrderDTO> producerRecord = stringObjectSendResult.getProducerRecord();
            log.info("producerRecord: {}", producerRecord);
            log.info("success");
            if (null != throwable) {
                log.error(throwable.getMessage());
            }
        });
    }


}
