package com.hmall.search;

import com.alibaba.fastjson.JSON;
import com.hmall.common.client.ItemClient;
import com.hmall.common.dto.Item;
import com.hmall.common.dto.PageVO;
import com.hmall.search.jopo.ItemDoc;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FeignTest {

    @Autowired
    private ItemClient itemClient;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 导入数据
     * @throws Exception
     */
    @Test
    public void testFeign() throws Exception {
        Integer page = 1;
        while (true){
            // 酒店数据
            PageVO<Item> pageVO = itemClient.page(page, 300);
            List<Item> list = pageVO.getList();
            if (list.size() == 0){
                break;
            }
            // 创建request
            BulkRequest request = new BulkRequest();
            // 准备参数，添加多个request
            for (Item item : list) {
                // 转化文档类型为Doc
                ItemDoc itemDoc = new ItemDoc(item);
                // 创建新增文档的request对象
                request.add(new IndexRequest("item")
                        .id(itemDoc.getId().toString())
                        .source(JSON.toJSONString(itemDoc), XContentType.JSON));
            }
            restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
            page++;
        }
    }
}
