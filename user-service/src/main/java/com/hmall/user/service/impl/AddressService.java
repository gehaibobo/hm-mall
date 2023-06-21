package com.hmall.user.service.impl;

import com.hmall.user.mapper.AddressMapper;
import com.hmall.user.pojo.Address;
import com.hmall.user.service.IAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService implements IAddressService{

    @Autowired
    private AddressMapper addressMapper;

    /**
     * 根据用户id查询地址列表
     * @param userId
     * @return
     */
    public List<Address> uid(Long userId) {
        return addressMapper.uid(userId);
    }

    /**
     * 根据addressId查询Address
     * @param id
     * @return
     */
    public Address addressId(Long id) {
        return addressMapper.addressId(id);
    }
}