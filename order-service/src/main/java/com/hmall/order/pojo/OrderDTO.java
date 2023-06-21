package com.hmall.order.pojo;

import lombok.Data;

@Data
public class OrderDTO {
    // 购买数量
    private Integer num;
    // 付款方式
    private Integer paymentType;
    // 收货人地址id
    private Long addressId;
    // 商品id
    private long itemId;
}
