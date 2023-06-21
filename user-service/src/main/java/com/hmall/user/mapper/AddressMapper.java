package com.hmall.user.mapper;

import com.hmall.user.pojo.Address;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AddressMapper{

    @Select("select * from tb_address where user_id = #{userId}")
    List<Address> uid(Long userId);

    @Select("select * from tb_address where id = #{id}")
    Address addressId(Long id);
}