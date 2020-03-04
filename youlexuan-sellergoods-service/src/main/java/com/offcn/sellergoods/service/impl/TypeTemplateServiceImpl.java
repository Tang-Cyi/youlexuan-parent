package com.offcn.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.entity.PageResult;
import com.offcn.mapper.TbSpecificationOptionMapper;
import com.offcn.mapper.TbTypeTemplateMapper;
import com.offcn.pojo.*;
import com.offcn.pojo.TbTypeTemplateExample.Criteria;
import com.offcn.sellergoods.service.TypeTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Map;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class TypeTemplateServiceImpl implements TypeTemplateService {

    @Autowired
    private TbTypeTemplateMapper typeTemplateMapper;

    @Autowired
    private TbSpecificationOptionMapper tbSpecificationOptionMapper;

    @Override
    public List<Map> findSpecAndOption(Long typeTemplateId) {

        TbTypeTemplate typeTemplate = typeTemplateMapper.selectByPrimaryKey(typeTemplateId);

        String specIds = typeTemplate.getSpecIds();//[{"id":27,"text":"网络",options:[]},{"id":32,"text":"机身内存"}]
        //将字符串转成List<Map>
        List<Map> list = JSON.parseArray(specIds, Map.class);//规格
        for (Map map : list) {
            //查询规格选项
            TbSpecificationOptionExample example = new TbSpecificationOptionExample();
            TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
            criteria.andSpecIdEqualTo(Long.parseLong(map.get("id") + ""));
            List<TbSpecificationOption> options = tbSpecificationOptionMapper.selectByExample(example);
            map.put("options", options);
        }

        return list;
    }

    /**
     * 查询全部
     */
    @Override
    public List<TbTypeTemplate> findAll() {
        return typeTemplateMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbTypeTemplate> page = (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(TbTypeTemplate typeTemplate) {
        typeTemplateMapper.insert(typeTemplate);
    }


    /**
     * 修改
     */
    @Override
    public void update(TbTypeTemplate typeTemplate) {
        typeTemplateMapper.updateByPrimaryKey(typeTemplate);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbTypeTemplate findOne(Long id) {
        return typeTemplateMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            typeTemplateMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbTypeTemplate typeTemplate, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbTypeTemplateExample example = new TbTypeTemplateExample();
        Criteria criteria = example.createCriteria();

        if (typeTemplate != null) {
            if (typeTemplate.getName() != null && typeTemplate.getName().length() > 0) {
                criteria.andNameLike("%" + typeTemplate.getName() + "%");
            }
            if (typeTemplate.getSpecIds() != null && typeTemplate.getSpecIds().length() > 0) {
                criteria.andSpecIdsLike("%" + typeTemplate.getSpecIds() + "%");
            }
            if (typeTemplate.getBrandIds() != null && typeTemplate.getBrandIds().length() > 0) {
                criteria.andBrandIdsLike("%" + typeTemplate.getBrandIds() + "%");
            }
            if (typeTemplate.getCustomAttributeItems() != null && typeTemplate.getCustomAttributeItems().length() > 0) {
                criteria.andCustomAttributeItemsLike("%" + typeTemplate.getCustomAttributeItems() + "%");
            }
        }

        Page<TbTypeTemplate> page = (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(example);

        brandAndSpecToRedis();
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 将品牌和规格放在redis缓存
     */
    private void brandAndSpecToRedis() {
        List<TbTypeTemplate> list = findAll();
        for (TbTypeTemplate typeTemplate : list) {
            //{"id":1,"text":"联想"},
            List<Map> maps = JSON.parseArray(typeTemplate.getBrandIds(), Map.class);

            redisTemplate.boundHashOps("brandList").put(typeTemplate.getId(), maps);

            List<Map> specAndOption = findSpecAndOption(typeTemplate.getId());
            redisTemplate.boundHashOps("specList").put(typeTemplate.getId(), specAndOption);
        }
    }


    public static void main(String[] args) {
        String specIds = "[{\"id\":27,\"text\":\"网络\"},{\"id\":32,\"text\":\"机身内存\"}]";
        List<Map> maps = JSON.parseArray(specIds, Map.class);
        System.out.println(maps);
    }

}
