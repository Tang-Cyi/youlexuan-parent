package com.offcn.solrutil;

import com.alibaba.fastjson.JSON;
import com.offcn.mapper.TbItemMapper;
import com.offcn.pojo.TbItem;
import com.offcn.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SolrUtil {
    @Autowired
    private TbItemMapper tbItemMapper;
    @Autowired
    private SolrTemplate solrTemplate;

    /**
     * 导入slor
     */
    public void importItemData(){
        List<TbItem> tbItems = tbItemMapper.selectByExample(null);
        for (TbItem tbItem : tbItems) {
            String spec = tbItem.getSpec();
            Map map=JSON.parseObject(spec);
            tbItem.setSpecMap(map);
        }
        solrTemplate.saveBeans(tbItems); //集合 saveBeans
        solrTemplate.commit();
    }

    public void clearSolrIndex(){
        SimpleQuery simpleQuery = new SimpleQuery("*:*");
        solrTemplate.delete(simpleQuery);
        solrTemplate.commit();
    }



    /*public void queryItemm(){
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        List<TbItem> tbItems = tbItemMapper.selectByExample(example);
    }*/

    public static void main(String[] args) {
        ApplicationContext context=new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        SolrUtil solrUtil=  (SolrUtil) context.getBean("solrUtil");
        solrUtil.clearSolrIndex();
//        solrUtil.importItemData();

    }

}
