package com.offcn.page.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.offcn.mapper.TbGoodsDescMapper;
import com.offcn.mapper.TbGoodsMapper;
import com.offcn.mapper.TbItemCatMapper;
import com.offcn.mapper.TbItemMapper;
import com.offcn.page.service.ItemPageService;
import com.offcn.pojo.TbGoods;
import com.offcn.pojo.TbGoodsDesc;
import com.offcn.pojo.TbItem;
import com.offcn.pojo.TbItemExample;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemPageServiceImpl implements ItemPageService {
    @Value("${pagedir}")
    private String pagedir;
    @Autowired
    private FreeMarkerConfig freeMarkerConfig;
    @Autowired
    private TbGoodsMapper tbGoodsMapper;
    @Autowired
    private TbGoodsDescMapper tbGoodsDescMapper;
    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Autowired
    private TbItemMapper tbItemMapper;
    /**
     * 根据spu生成商品详情页
     * @param goodsId
     * @return
     */
    @Override
    public boolean genItemHtml(Long goodsId) {
        try {
            Configuration configuration = freeMarkerConfig.getConfiguration();
            Template template = configuration.getTemplate("item.ftl");
            Map map=new HashMap();
            TbGoods tbGoods = tbGoodsMapper.selectByPrimaryKey(goodsId);
            TbGoodsDesc tbGoodsDesc = tbGoodsDescMapper.selectByPrimaryKey(tbGoods.getId());
            map.put("goods",tbGoods);
            map.put("goodsDesc",tbGoodsDesc);
            map.put("category1Name",itemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id()).getName());
            map.put("category2Name",itemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id()).getName());
            map.put("category3Name",itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id()).getName());
            //查询当前sku列表
            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andGoodsIdEqualTo(goodsId);
            List<TbItem> itemList = tbItemMapper.selectByExample(example);
            map.put("itemList",itemList);
            //生成html路径
            FileWriter fileWriter = new FileWriter(pagedir + goodsId + ".html");
            template.process(map,fileWriter);
            fileWriter.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteItemHtml(Long[] goodsId) {
        try {
            for (Long id : goodsId) {
                new File(pagedir+id+".html").delete();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
}
