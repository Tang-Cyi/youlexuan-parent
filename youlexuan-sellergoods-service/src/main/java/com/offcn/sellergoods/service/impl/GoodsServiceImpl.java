package com.offcn.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.entity.Goods;
import com.offcn.entity.PageResult;
import com.offcn.mapper.*;
import com.offcn.pojo.*;
import com.offcn.pojo.TbGoodsExample.Criteria;
import com.offcn.sellergoods.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private TbGoodsMapper goodsMapper;
    @Autowired
    private TbGoodsDescMapper tbGoodsDescMapper;
    @Autowired
    private TbItemMapper tbItemMapper;
    @Autowired
    private TbItemCatMapper tbItemCatMapper;
    @Autowired
    private TbBrandMapper tbBrandMapper;
    @Autowired
    private TbSellerMapper tbSellerMapper;
    @Override
    public void updateMarketable(Long[] ids, String isMarketable) {

        /*for(Long id : ids){
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            tbGoods.setIsMarketable(isMarketable);
            goodsMapper.updateByPrimaryKey(tbGoods);
        }*/
        TbGoodsExample example=new TbGoodsExample();
        Criteria criteria = example.createCriteria();
        criteria.andIdIn(Arrays.asList(ids));
        TbGoods tbGoods = new TbGoods();
        tbGoods.setIsMarketable(isMarketable);
        goodsMapper.updateByExampleSelective(tbGoods,example);
    }

    @Override
    public void updateAuditStatus(Long[] ids, String status) {
        for(Long id:ids){
            TbGoods goods = goodsMapper.selectByPrimaryKey(id);
            goods.setAuditStatus(status);
            goodsMapper.updateByPrimaryKey(goods);
        }

    }

    /**
     * 查询全部
     */
    @Override
    public List<TbGoods> findAll() {
        return goodsMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     *
     * @Override public void add(TbGoods goods) {
     * goodsMapper.insert(goods);
     * }
     */
    @Override
    public void add(Goods goods) {
        TbGoods tbGoods = goods.getGoods();
        tbGoods.setAuditStatus("0");
        goodsMapper.insert(tbGoods);

        TbGoodsDesc goodsDesc = goods.getGoodsDesc();
        goodsDesc.setGoodsId(tbGoods.getId());
        tbGoodsDescMapper.insert(goodsDesc);

        //itemList
        for (TbItem item : goods.getItemList()) {
            //处理标题
            String tittle = tbGoods.getGoodsName();
            Map<String, Object> map = JSON.parseObject(item.getSpec());
            for (String key : map.keySet()) {
                tittle += " " + map.get(key);
            }
            item.setTitle(tittle);
            //图片地址（取spu的第一个图片）
            List<Map> list = JSON.parseArray(goodsDesc.getItemImages(), Map.class);
            if (list != null && list.size() > 0) {
                String url = list.get(0).get("url") + "";
                item.setImage(url);
            }
            //第三级分类的主键
            item.setCategoryid(tbGoods.getCategory3Id());

            item.setCreateTime(new Date());
            item.setUpdateTime(new Date());

            //goods id
            item.setGoodsId(tbGoods.getId());
            //seller id
            item.setSellerId(tbGoods.getSellerId());

            //category 第三级分类名称
            TbItemCat tbItemCat = tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id());
            item.setCategory(tbItemCat.getName());
            //品牌名称
            String brandName = tbBrandMapper.selectByPrimaryKey(tbGoods.getBrandId()).getName();
            item.setBrand(brandName);
            //seller 店铺名称
            TbSeller tbSeller = tbSellerMapper.selectByPrimaryKey(tbGoods.getSellerId());
            item.setSeller(tbSeller.getNickName());

            tbItemMapper.insert(item);
        }
    }


    /**
     * 修改
     */
    @Override
    public void update(TbGoods goods) {
        goodsMapper.updateByPrimaryKey(goods);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbGoods findOne(Long id) {
        return goodsMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
//            goodsMapper.deleteByPrimaryKey(id);
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            tbGoods.setIsDelete("1");
            goodsMapper.updateByPrimaryKey(tbGoods);
        }
    }


    @Override
    public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbGoodsExample example = new TbGoodsExample();
        Criteria criteria = example.createCriteria();
        criteria.andIsDeleteIsNull();//假删除
        if (goods != null) {
            if (goods.getSellerId() != null && goods.getSellerId().length() > 0) {
                criteria.andSellerIdEqualTo(goods.getSellerId());
                /*criteria.andSellerIdLike("%" + goods.getSellerId() + "%");*/
            }
            if (goods.getGoodsName() != null && goods.getGoodsName().length() > 0) {
                criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
            }
            if (goods.getAuditStatus() != null && goods.getAuditStatus().length() > 0) {
                criteria.andAuditStatusLike("%" + goods.getAuditStatus() + "%");
            }
            if (goods.getIsMarketable() != null && goods.getIsMarketable().length() > 0) {
                criteria.andIsMarketableLike("%" + goods.getIsMarketable() + "%");
            }
            if (goods.getCaption() != null && goods.getCaption().length() > 0) {
                criteria.andCaptionLike("%" + goods.getCaption() + "%");
            }
            if (goods.getSmallPic() != null && goods.getSmallPic().length() > 0) {
                criteria.andSmallPicLike("%" + goods.getSmallPic() + "%");
            }
            if (goods.getIsEnableSpec() != null && goods.getIsEnableSpec().length() > 0) {
                criteria.andIsEnableSpecLike("%" + goods.getIsEnableSpec() + "%");
            }
            if (goods.getIsDelete() != null && goods.getIsDelete().length() > 0) {
                criteria.andIsDeleteLike("%" + goods.getIsDelete() + "%");
            }
        }

        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

}
