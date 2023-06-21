package com.hmall.order.service.impl;

import com.hmall.common.client.ItemClient;
import com.hmall.common.client.UserClient;
import com.hmall.common.dto.Address;
import com.hmall.common.dto.Item;
import com.hmall.order.mapper.OrderMapper;
import com.hmall.order.pojo.*;
import com.hmall.order.service.IOrderService;
import com.hmall.order.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class OrderService implements IOrderService {

    @Autowired
    private ItemClient itemClient;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserClient userClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;


    /**
     * 下单
     * @param orderDTO
     * @return
     */
    public String order(OrderDTO orderDTO) {
        // 订单id
        long id = SnowFlake.nextId();
        // 商品信息
        Item item = itemClient.seleteOne(orderDTO.getItemId());
        // 商品总价
        long totalFee = (orderDTO.getNum()) * (item.getPrice());
        // 封装对象到order
        Order order = new Order();
        order.setId(id);
        order.setTotalFee(totalFee);
        order.setPaymentType(orderDTO.getPaymentType());
        order.setUserId(UserHolder.getUser());
        order.setStatus(1);
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        // 写入order表
        orderMapper.insertOrder(order);
        // 封装对象到orderDetail
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setId(SnowFlake.nextId());
        orderDetail.setOrderId(id);
        orderDetail.setItemId(orderDTO.getItemId());
        orderDetail.setNum(orderDTO.getNum());
        orderDetail.setName(item.getName());
        orderDetail.setSpec(item.getSpec());
        orderDetail.setPrice(item.getPrice());
        orderDetail.setImage(item.getImage());
        orderDetail.setCreateTime(LocalDateTime.now());
        orderDetail.setUpdateTime(LocalDateTime.now());
        // 写入orderDetail表
        orderMapper.insertOrderDetail(orderDetail);
        // 地址信息
        Address address = userClient.addressId(orderDTO.getAddressId());
        // 封装对象到orderLogistics
        OrderLogistics orderLogistics = new OrderLogistics();
        orderLogistics.setOrderId(id);
        orderLogistics.setContact(address.getContact());
        orderLogistics.setMobile(address.getMobile());
        orderLogistics.setProvince(address.getProvince());
        orderLogistics.setCity(address.getCity());
        orderLogistics.setTown(address.getTown());
        orderLogistics.setStreet(address.getStreet());
        orderLogistics.setCreateTime(LocalDateTime.now());
        orderLogistics.setUpdateTime(LocalDateTime.now());
        // 写入orderLogistics表
        orderMapper.insertOrderLogistics(orderLogistics);
        // 扣减库存
        itemClient.stock(orderDTO.getItemId(), orderDTO.getNum());
        // 发送延时消息队列到时间去检查
        Message message = MessageBuilder.withBody(order.getId().toString().getBytes(StandardCharsets.UTF_8)).setHeader("x-delay", 5000).build();
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend("delay.direct", "delay", message, correlationData);
        log.info("消息發送成功");
        return order.getId().toString();
    }

    @Override
    public void removeById(Long orderId) {
        Order order = orderMapper.geById(orderId);
        if (order == null || order.getStatus() != 1) {
            return;
        }
        OrderDetail orderDetail = orderMapper.getDetailId(orderId);
        itemClient.stock(orderDetail.getItemId(), -(orderDetail.getNum()));
        orderMapper.removeOrder(orderId);
        orderMapper.removeOrderDetail(orderId);
        orderMapper.removeOrderLogistics(orderId);
    }
}
