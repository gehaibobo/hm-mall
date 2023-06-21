package com.hmall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.hmall.common.client.ItemClient;
import com.hmall.common.dto.Item;
import com.hmall.common.dto.PageVO;
import com.hmall.search.jopo.ItemDoc;
import com.hmall.search.jopo.RequestParams;
import com.hmall.search.service.ISearchService;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SearchService implements ISearchService {

    @Autowired
    private ItemClient itemClient;

    @Autowired
    private RestHighLevelClient restHighLevelClient;
    /**
     * 自动补全
     * @param prefix
     * @return
     */
    public List<String> suggestion(String prefix) {
        try {
            // 准备request
            SearchRequest request = new SearchRequest("item");
            // 准备DSL
            request.source().suggest(new SuggestBuilder().addSuggestion(
                    "suggestions",
                    SuggestBuilders.completionSuggestion("suggestion")
                            .prefix(prefix)
                            .skipDuplicates(true)
                            .size(10)
            ));
            // 发送请求
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            // 解析结果
            Suggest suggest = response.getSuggest();
            // 根据补全查询名称，获取补全结果
            CompletionSuggestion suggestions = suggest.getSuggestion("suggestions");
            // 获取options
            List<CompletionSuggestion.Entry.Option> options = suggestions.getOptions();
            // 遍历
            List<String> list = new ArrayList<>(options.size());
            for (CompletionSuggestion.Entry.Option option : options) {
                String text = option.getText().toString();
                list.add(text);
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 过滤聚合
     * @param requestParams
     * @return
     */
    public Map<String, List<String>> filters(RequestParams requestParams) {
        try {
            // 准备request
            SearchRequest request = new SearchRequest("item");
            // 准备DSL
            // query
            buildBasicQuery(requestParams, request);
            // 设置size
            request.source().size(0);
            // 聚合
            buildAggregation(request);
            // 发送请求
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            // 解析结果
            Map<String, List<String>> result = new HashMap<>(2);
            Aggregations aggregations = response.getAggregations();
            // 根据分类名称，获取分类结果
            Terms categoryAgg = aggregations.get("categoryAgg");
            List<String> categoryList = new ArrayList<>();
            for (Terms.Bucket bucket : categoryAgg.getBuckets()) {
                String key = bucket.getKeyAsString();
                categoryList.add(key);
            }
            result.put("category", categoryList);
            // 根据品牌名称，获取品牌结果
            Terms brandAgg = aggregations.get("brandAgg");
            List<String> brandList = new ArrayList<>();
            for (Terms.Bucket bucket : brandAgg.getBuckets()) {
                String key = bucket.getKeyAsString();
                brandList.add(key);
            }
            result.put("brand", brandList);
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 搜索结果
     * @param requestParams
     * @return
     */
    public PageVO<ItemDoc> list(RequestParams requestParams) {
        try {
            // 准备request
            SearchRequest request = new SearchRequest("item");
            // 准备DSL
            // query
            buildBasicQuery(requestParams, request);
            // 分页
            Integer page = requestParams.getPage();
            Integer size = requestParams.getSize();
            request.source().from((page - 1) * size).size(size);
            // 排序
            String sortBy = requestParams.getSortBy();
            if ("sold".equals(sortBy)){
                request.source().sort(sortBy, SortOrder.DESC);
            }else if ("price".equals(sortBy)){
                request.source().sort(sortBy, SortOrder.DESC);
            }
            // 高亮
            request.source().highlighter(new HighlightBuilder().field("name").requireFieldMatch(false));
            // 发送请求
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            // 解析响应
            SearchHits searchHits = response.getHits();
            // 获取total
            long total = searchHits.getTotalHits().value;
            // 获取数据
            SearchHit[] hits = searchHits.getHits();
            // 遍历
            List<ItemDoc> list = new ArrayList<>(hits.length);
            for (SearchHit hit : hits) {
                // 获取source
                String json = hit.getSourceAsString();
                // JSON转java
                ItemDoc itemDoc = JSON.parseObject(json, ItemDoc.class);
                // 获取高亮
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                if (highlightFields != null && highlightFields.size() > 0) {
                    HighlightField field = highlightFields.get("name");
                    String name = field.getFragments()[0].string();
                    itemDoc.setName(name);
                }
                list.add(itemDoc);
            }
            return new PageVO<>(total, list);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 新增
     * @param id
     */
    public void insertById(Long id) {
        try {
            // 获取商品数据
            Item item = itemClient.seleteOne(id);
            ItemDoc itemDoc = new ItemDoc(item);
            // 准备request
            IndexRequest request = new IndexRequest("item").id(id.toString());
            // DSL
            request.source(JSON.toJSONString(itemDoc), XContentType.JSON);
            // 发送请求
            restHighLevelClient.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 删除
     * @param id
     */
    public void deleteById(Long id) {
        try {
            // 准备request
            DeleteRequest request = new DeleteRequest("item", id.toString());
            // 发送请求
            restHighLevelClient.delete(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 条件过滤
     * @param requestParams
     * @param request
     */
    private void buildBasicQuery(RequestParams requestParams, SearchRequest request) {
        // 构建BooleanQuery
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        // 关键字搜索
        String key = requestParams.getKey();
        if (!StringUtils.isBlank(key)){
            boolQuery.must(QueryBuilders.matchQuery("all", key));
        } else {
            boolQuery.must(QueryBuilders.matchAllQuery());
        }
        // 品牌搜索
        String brand = requestParams.getBrand();
        if (!StringUtils.isBlank(brand)){
            boolQuery.filter(QueryBuilders.termQuery("brand", brand));
        }
        // 分类搜索
        String category = requestParams.getCategory();
        if (!StringUtils.isBlank(category)){
            boolQuery.filter(QueryBuilders.termQuery("category", category));
        }
        // 价格搜索
        Integer minPrice = requestParams.getMinPrice();
        Integer maxPrice = requestParams.getMaxPrice();
        if (minPrice != null && maxPrice != null){
            boolQuery.filter(QueryBuilders.rangeQuery("price").gte(minPrice * 100).lte(maxPrice * 100));
        }
        // 放入source
        request.source().query(boolQuery);
    }

    /**
     * 聚合
     * @param request
     */
    private void buildAggregation(SearchRequest request) {
        request.source().aggregation(AggregationBuilders
                .terms("categoryAgg")
                .field("category")
                .size(20)
        );
        request.source().aggregation(AggregationBuilders
                .terms("brandAgg")
                .field("brand")
                .size(20)
        );
    }

}
