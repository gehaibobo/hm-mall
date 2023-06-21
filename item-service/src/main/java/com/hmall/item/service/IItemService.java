package com.hmall.item.service;

import com.github.pagehelper.Page;
import com.hmall.item.pojo.Item;
import com.hmall.item.pojo.ItemDTO;
import org.springframework.web.bind.annotation.RequestParam;

public interface IItemService {



    /**
     * 查询商品详情
     * @param id
     * @return
     */
    Item seleteOne(Long id);

    /**
     * 分页查询商品
     * @param pageDTO
     * @return
     */
    Page<Item> page(Integer page, Integer size);

    /**
     *新增商品
     * @param itemDTO
     */
    void insert(ItemDTO itemDTO);

    /**
     * 上下架
     * @param id
     * @param status
     */
    void status(Long id, Integer status);

    /**
     * 修改
     * @param itemDTO
     */
    void update(ItemDTO itemDTO);

    /**
     * 删除
     * @param id
     */
    void delete(Long id);

    /**
     * 扣减库存
     * @param id
     * @param num
     */
    void stock(Long id, Integer num);

}
