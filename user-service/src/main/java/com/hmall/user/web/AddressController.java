package com.hmall.user.web;

import com.hmall.user.pojo.Address;
import com.hmall.user.service.IAddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/address")
public class AddressController {

    @Autowired
    private IAddressService iAddressService;

    /**
     * 根据用户id查询地址列表
     * @param userId
     * @return
     */
    @GetMapping("/uid/{userId}")
    public List<Address> uid(@PathVariable("userId") Long userId){
        return iAddressService.uid(userId);
    }

    /**
     * 根据addressId查询Address
     * @param id
     * @return
     */
    @GetMapping("/{addressId}")
    public Address addressId(@PathVariable("addressId") Long id){
        return iAddressService.addressId(id);
    }
}
