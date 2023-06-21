package com.hmall.order.service;

import com.hmall.order.pojo.OrderDTO;

public interface IOrderService {
    /**
     * 下单
     * @param orderDTO
     * @return
     */
    String order(OrderDTO orderDTO);

    void removeById(Long orderId);
}
