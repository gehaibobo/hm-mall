package com.hmall.item.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hmall.item.mapper.ItemMapper;
import com.hmall.item.pojo.*;
import com.hmall.item.service.IItemService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ItemService implements IItemService{
    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     *  查询商品详情
     * @param id
     * @return
     */
    public Item seleteOne(Long id) {
        Item item = itemMapper.seleteOne(id);
        return item;
    }

    /**
     * 分页查询商品
     * @param page
     * @param size
     * @return
     */
    public Page<Item> page(Integer page, Integer size) {
        // 分离参数
        int i = page == null ? 1 : page;
        int x = size == null ? 5 : size;
        PageHelper.startPage(i, x);
        Page<Item> itemPage = itemMapper.page();
        return itemPage;
    }

    /**
     * 新增商品
     * @param itemDTO
     */
    public void insert(ItemDTO itemDTO) {
        Item item = new Item();
        BeanUtils.copyProperties(itemDTO, item);
        item.setId(SnowFlake.nextId());
        item.setSold(0);
        item.setCommentCount(0);
        item.setStatus(2);
        item.setCreateTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        itemMapper.insert(item);
    }

    /**
     * 上下架
     * @param id
     * @param status
     */
    @Transactional
    public void status(Long id, Integer status) {
        Item item = Item.builder()
                .status(status)
                .id(id)
                .updateTime(LocalDateTime.now())
                .build();
        itemMapper.update(item);
        // 根据上下架判断key
        String key = status == 1 ? "item.up" : "item.down";
        // 发送消息
        rabbitTemplate.convertAndSend("item.topic", key, id);
    }

    /**
     * 修改
     * @param itemDTO
     */
    public void update(ItemDTO itemDTO) {
        Item item = new Item();
        BeanUtils.copyProperties(itemDTO, item);
        item.setUpdateTime(LocalDateTime.now());
        itemMapper.update(item);
    }

    /**
     * 删除
     * @param id
     */
    public void delete(Long id) {
        itemMapper.delete(id);
    }

    /**
     * 扣减库存
     * @param id
     * @param num
     */
    public void stock(Long id, Integer num) {
        Item itemDO = itemMapper.seleteOne(id);
        if (itemDO.getStock() < num){
            throw new RuntimeException("库存不足");
        }
        Item item = Item.builder()
                .id(id)
                .stock(itemDO.getStock() - num)
                .updateTime(LocalDateTime.now())
                .build();
        itemMapper.update(item);
    }
}
