package com.hmall.search.jopo;

import com.hmall.common.dto.Item;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDoc {
    private Long id;//商品id
    private String name;//商品名称
    private Long price;//价格（分）
    private String image;//商品图片
    private String category;//分类名称
    private String brand;//品牌名称
    private Integer sold;//销量
    private Integer commentCount;//评论数
    private Boolean isAD;//廣告
    private List<String> suggestion = new ArrayList<>(2);

    public ItemDoc(Item item) {
        BeanUtils.copyProperties(item, this);
        suggestion.add(item.getBrand());
        suggestion.add(item.getCategory());
    }
}
