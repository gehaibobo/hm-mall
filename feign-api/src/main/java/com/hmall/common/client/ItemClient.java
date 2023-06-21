package com.hmall.common.client;

import com.hmall.common.config.FeignConfig;
import com.hmall.common.dto.Item;

import com.hmall.common.dto.PageVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("itemservice")
public interface ItemClient {
    @GetMapping("/item/list")
    public PageVO<Item> page(@RequestParam("page") Integer page, @RequestParam("size") Integer size);

    @GetMapping( "/item/{id}")
    public Item seleteOne(@PathVariable("id") Long id);

    @GetMapping("/item/stock/{itemId}/{num}")
    public void stock(@PathVariable("itemId") Long id,
                      @PathVariable("num") Integer num);

}
