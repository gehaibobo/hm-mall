package com.hmall.user.service;


import com.hmall.user.pojo.Address;

import java.util.List;

public interface IAddressService{
    /**
     * 根据用户id查询地址列表
     * @param userId
     * @return
     */
    List<Address> uid(Long userId);

    /**
     * 根据addressId查询Address
     * @param id
     * @return
     */
    Address addressId(Long id);
}
