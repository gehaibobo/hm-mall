package com.hmall.item.web;

import com.github.pagehelper.Page;
import com.hmall.item.pojo.Item;
import com.hmall.item.pojo.ItemDTO;
import com.hmall.item.pojo.PageVO;
import com.hmall.item.service.IItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/item")
public class ItemController {

    @Autowired
    private IItemService iItemService;

    /**
     * 分页查询商品
     *
     * @param
     * @return
     */
    @GetMapping("/list")
    public PageVO<Item> page(@RequestParam("page") Integer page, @RequestParam("size") Integer size) {
        Page<Item> itemPage = iItemService.page(page, size);
        return new PageVO<>(itemPage.getTotal(), itemPage.getResult());
    }

    /**
     * 查询商品详情
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Item seleteOne(@PathVariable("id") Long id) {
        Item item = iItemService.seleteOne(id);
        return item;
    }

    /**
     * 新增商品
     * @param itemDTO
     */
    @PostMapping
    public void insert(@RequestBody ItemDTO itemDTO) {
        iItemService.insert(itemDTO);
    }

    /**
     * 上下架
     * @param id
     * @param status
     */
    @PutMapping("/status/{id}/{status}")
    public void status(@PathVariable("id") Long id, @PathVariable("status") Integer status){
        iItemService.status(id, status);
    }

    /**
     * 修改
     * @param itemDTO
     */
    @PutMapping
    public void update(@RequestBody ItemDTO itemDTO){
        iItemService.update(itemDTO);
    }

    /**
     * 删除
     * @param id
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id){
        iItemService.delete(id);
    }

    /**
     * 扣减库存
     * @param id
     * @param num
     */
    @GetMapping("/stock/{itemId}/{num}")
    public void stock(@PathVariable("itemId") Long id,
                      @PathVariable("num") Integer num){
        iItemService.stock(id, num);
    }
}
