package com.hmall.search.service;

import com.hmall.common.dto.PageVO;
import com.hmall.search.jopo.ItemDoc;
import com.hmall.search.jopo.RequestParams;

import java.util.List;
import java.util.Map;

public interface ISearchService {
    /**
     * 自动补全
     * @param key
     * @return
     */
    List<String> suggestion(String prefix);

    /**
     * 过滤聚合
     * @param requestParams
     * @return
     */
    Map<String, List<String>> filters(RequestParams requestParams);

    /**
     * 搜索结果
     * @param requestParams
     * @return
     */
    PageVO<ItemDoc> list(RequestParams requestParams);

    /**
     * 新增
     * @param id
     */
    void insertById(Long id);

    /**
     * 删除
     * @param id
     */
    void deleteById(Long id);
}
