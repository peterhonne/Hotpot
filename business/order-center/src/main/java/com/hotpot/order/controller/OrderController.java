package com.hotpot.order.controller;

import com.hotpot.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Peter
 * @date 2023/3/24
 * @description
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/{id}")
    public Object getById(@PathVariable Integer id) {
        return null;
    }

    @PostMapping
    public Object createOrder(Object order) {
        orderService.createOrder();
        return "Done";
    }



}
