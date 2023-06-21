package com.hmall.order.mapper;

import com.hmall.order.pojo.Order;
import com.hmall.order.pojo.OrderDetail;
import com.hmall.order.pojo.OrderLogistics;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OrderMapper{
    // 写入数据
    void insertOrder(@Param("order") Order order);

    void insertOrderDetail(OrderDetail orderDetail);

    void insertOrderLogistics(OrderLogistics orderLogistics);

    @Select("select * from tb_order where id = #{orderId}")
    Order geById(Long orderId);

    @Select("select * from tb_order_detail where order_id = #{orderId}")
    OrderDetail getDetailId(Long orderId);

    @Delete("delete from tb_order where id = #{orderId}")
    void removeOrder(Long orderId);

    @Delete("delete from tb_order_detail where order_id = #{orderId}")
    void removeOrderDetail(Long orderId);

    @Delete("delete from tb_order_logistics where order_id = #{orderId}")
    void removeOrderLogistics(Long orderId);
}
