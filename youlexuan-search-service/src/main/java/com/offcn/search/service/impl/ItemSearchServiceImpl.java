package com.offcn.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.offcn.pojo.TbItem;
import com.offcn.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(timeout = 5000)
public class ItemSearchServiceImpl implements ItemSearchService {
    @Autowired
   private SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public Map<String, Object> search(Map searchMap) {
        Map<String, Object> map=new HashMap<>();
/*        String keywords = searchMap.get("keywords") + "";
        SimpleQuery simpleQuery=new SimpleQuery();
//        SimpleQuery simpleQuery=new SimpleQuery("*:*");
        Criteria criteria = new Criteria("item_keywords").is(keywords);//按照关键词检索
        simpleQuery.addCriteria(criteria);
        ScoredPage<TbItem> scoredPage = solrTemplate.queryForPage(simpleQuery, TbItem.class);
        List<TbItem> list = scoredPage.getContent();
        map.put("row",list);*/
        //1.查询列表
        map.putAll( searchList(searchMap));

        //2.根据关键字查询商品分类
        List<String> categoryList = searchCategoryList(searchMap);
        map.put("categoryList", categoryList);
        if (!searchMap.get("category").equals("")){
            map.putAll(searchBrandAndSpec(searchMap.get("category")+""));
        }else {
            if(categoryList.size()>0){
                map.putAll(searchBrandAndSpec((String)categoryList.get(0)));
            }
        }
        return map;
    }



    private Map searchList(Map searchMap){
        Map map=new HashMap();

        //1、创建一个支持高亮查询器对象
        SimpleHighlightQuery query = new SimpleHighlightQuery();
        //2、创建高亮选项对象
        HighlightOptions highlightOptions = new HighlightOptions();
        //3、设定需要高亮处理字段
        highlightOptions.addField("item_title");
        //4、设置高亮前缀
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        //5、设置高亮后缀
        highlightOptions.setSimplePostfix("</em>");
        //6、关联高亮选项到高亮查询器对象
        query.setHighlightOptions(highlightOptions);

        //7、设定查询条件 根据关键字查询
        //创建查询条件对象
        //条件1 关键字过滤
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        //关联查询条件到查询器对象
        query.addCriteria(criteria);
        //条件2 商品分类 过滤查询
        if (!searchMap.get("category").equals("")){
            SimpleFilterQuery filterQuery=new SimpleFilterQuery();
            Criteria criteria1=new Criteria("item_category").is(searchMap.get("category"));
            filterQuery.addCriteria(criteria1);
            query.addFilterQuery(filterQuery);
        }
        //条件3 品牌过滤 过滤查询
        if (!searchMap.get("brand").equals("")){
            SimpleFilterQuery filterQuery=new SimpleFilterQuery();
            Criteria criteria1=new Criteria("item_brand").is(searchMap.get("brand"));
            filterQuery.addCriteria(criteria1);
            query.addFilterQuery(filterQuery);
        }
        //条件4 规格过滤 过滤查询
        if (!searchMap.get("spec").equals("")){
            Map<String,Object> spec = (Map<String, Object>) searchMap.get("spec");
            for (String key : spec.keySet()) {
                Criteria criteria1 = new Criteria("item_spec_" + key).is(spec.get(key));
                SimpleFilterQuery filterQuery=new SimpleFilterQuery(criteria1);
                query.addFilterQuery(filterQuery);
            }
        }
        //条件5 价格过滤 过滤查询
        if (!searchMap.get("price").equals("")){
            String[] prices = (searchMap.get("price") + "").split("-");
            if (!prices[0].equals("0")){
                Criteria criteria1 = new Criteria("item_price").greaterThan(prices[0]);
                SimpleFilterQuery filterQuery=new SimpleFilterQuery(criteria1);
                query.addFilterQuery(filterQuery);
            }
            if (!prices[1].equals("*")){
                Criteria criteria1 = new Criteria("item_price").lessThanEqual(prices[1]);
                SimpleFilterQuery filterQuery=new SimpleFilterQuery(criteria1);
                query.addFilterQuery(filterQuery);
            }
        }
        //排序过滤
        String sortValue = searchMap.get("sort") + "";//排序方向
        String sortField = searchMap.get("sortField") + "";//排序方向
        if (sortValue!=null && !sortValue.equals("")){
            //排序方向
            if (sortValue.equals("ASC")){
                Sort sort = new Sort(Sort.Direction.ASC, "item_" + sortField);
                query.addSort(sort);
            }
            if (sortValue.equals("DESC")){
                Sort sort = new Sort(Sort.Direction.DESC, "item_" + sortField);
                query.addSort(sort);
            }
        }


        //8、发出带高亮数据查询请求
        HighlightPage<TbItem> highlightPage = solrTemplate.queryForHighlightPage(query, TbItem.class);

        //9、获取查询结果记录集合
        List<TbItem> list = highlightPage.getContent();
        //10、循环集合对象
        for (TbItem item : list) {
            //获取到针对对象TbItem高亮集合
            List<HighlightEntry.Highlight> highlights = highlightPage.getHighlights(item);
            if(highlights!=null&&highlights.size()>0) {
                //获取第一个字段高亮对象
                List<String> highlightSnipplets = highlights.get(0).getSnipplets();
                System.out.println("高亮：" + highlightSnipplets.get(0));
                //使用高亮结果替换商品标题
                item.setTitle(highlightSnipplets.get(0));

            }
        }
        map.put("rows",highlightPage.getContent());

        return  map;
    }

    private  List<String> searchCategoryList(Map searchMap){
        List<String> list=new ArrayList();
        SimpleQuery query=new SimpleQuery();
        //按照关键字查询
        Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //设置分组选项  注意商品分类不能设置分词，要不然分组结果会失败
        GroupOptions groupOptions=new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupOptions);
        //得到分组页
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        //根据列得到分组结果集
        GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
        //得到分组结果入口页
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        //得到分组入口集合
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        for(GroupEntry<TbItem> entry:content){
            list.add(entry.getGroupValue());//将分组结果的名称封装到返回值中
        }
        return list;
    }

    /**
     * 查询品牌和规格列表
     * @param category 分类名称
     * @return
     */
    private Map searchBrandAndSpec(String category){
        Map map=new HashMap();
        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);//获取模板ID
        if(typeId!=null){
            //根据模板ID查询品牌列表
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
            map.put("brandList", brandList);//返回值添加品牌列表
            //根据模板ID查询规格列表
            List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);
            map.put("specList", specList);
        }
        return map;
    }
    @Override
    public  void importItemList(List<TbItem> items){
        solrTemplate.saveBeans(items);
        solrTemplate.commit();
    }

    @Override
    public void removeItemList(Long[] ids) {
        Criteria criteria = new Criteria("item_goodsid").in(ids);
        SimpleQuery query = new SimpleQuery(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }


}
