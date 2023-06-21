package com.hmall.order.web;

import com.hmall.order.pojo.Order;
import com.hmall.order.pojo.OrderDTO;
import com.hmall.order.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("order")
public class OrderController {

    @Autowired
    private IOrderService iOrderService;

    /**
     * 下单
     * @param orderDTO
     * @return
     */
    @PostMapping
    public String order(@RequestBody OrderDTO orderDTO){
        return iOrderService.order(orderDTO);
    }

}
