package com.hmall.search.mq;

import com.hmall.search.service.ISearchService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ItemMq {

    @Autowired
    private ISearchService iSearchService;

    /**
     * 新增
     * @param id
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "up.queue"),
            exchange = @Exchange(name = "item.topic",type = ExchangeTypes.TOPIC),
            key = {"item.up"}
    ))
    public void mqItemUp(Long id){
        iSearchService.insertById(id);
    }

    /**
     * 删除
     * @param id
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "down.queue"),
            exchange = @Exchange(name = "item.topic",type = ExchangeTypes.TOPIC),
            key = {"item.down"}
    ))
    public void mqItemDown(Long id){
        iSearchService.deleteById(id);
    }
}
