package com.hmall.item.mapper;


import com.github.pagehelper.Page;
import com.hmall.item.pojo.Item;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ItemMapper{
    /**
     * 分页
     * @param pageDTO
     * @return
     */
    Page<Item> page();

    /**
     *  查询商品详情
     * @param id
     * @return
     */
    Item seleteOne(Long id);

    /**
     * 新增
     * @param item
     */
    void insert(Item item);

    /**
     * 上下架
     * @param item
     */
    void update(Item item);

    /**删除
     *
     * @param id
     */
    void delete(Long id);

}
