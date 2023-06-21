package com.hmall.search.web;

import com.hmall.common.dto.PageVO;
import com.hmall.search.jopo.ItemDoc;
import com.hmall.search.jopo.RequestParams;
import com.hmall.search.service.ISearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private ISearchService iSearchService;

    /**
     * 自动补全
     * @param prefix
     * @return
     */
    @GetMapping("/suggestion")
    public List<String> suggestion(@RequestParam("key") String prefix){
        return iSearchService.suggestion(prefix);
    }

    /**
     * 过滤聚合
     * @param requestParams
     * @return
     */
    @PostMapping("/filters")
    public Map<String, List<String>> filters(@RequestBody RequestParams requestParams){
        return iSearchService.filters(requestParams);
    }

    /**
     * 搜索结果
     * @param requestParams
     * @return
     */
    @PostMapping("/list")
    public PageVO<ItemDoc> list(@RequestBody RequestParams requestParams){
        return iSearchService.list(requestParams);
    }
}
