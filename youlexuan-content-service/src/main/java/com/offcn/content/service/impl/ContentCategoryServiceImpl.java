package com.offcn.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.content.service.ContentCategoryService;
import com.offcn.entity.PageResult;
import com.offcn.mapper.TbContentCategoryMapper;
import com.offcn.mapper.TbContentMapper;
import com.offcn.pojo.TbContent;
import com.offcn.pojo.TbContentCategory;
import com.offcn.pojo.TbContentCategoryExample;
import com.offcn.pojo.TbContentCategoryExample.Criteria;
import com.offcn.pojo.TbContentExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * 内容分类服务实现层
 *
 * @author Administrator
 */
@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {

    @Autowired
    private TbContentCategoryMapper contentCategoryMapper;
    @Autowired
    private TbContentMapper tbContentMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 查询全部
     */
    @Override
    public List<TbContentCategory> findAll() {
        return contentCategoryMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbContentCategory> page = (Page<TbContentCategory>) contentCategoryMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(TbContentCategory contentCategory) {
        contentCategoryMapper.insert(contentCategory);
    }


    /**
     * 修改
     */
    @Override
    public void update(TbContentCategory contentCategory) {
        contentCategoryMapper.updateByPrimaryKey(contentCategory);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbContentCategory findOne(Long id) {
        return contentCategoryMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public List<Long> delete(Long[] ids) {
        List<Long> notAllowDelids = new ArrayList<>();
        for (Long id : ids) {
            TbContentExample example = new TbContentExample();
            TbContentExample.Criteria criteria = example.createCriteria();
            criteria.andCategoryIdEqualTo(id);
            List<TbContent> list = tbContentMapper.selectByExample(example);
            if (list == null || list.size() == 0) {
                contentCategoryMapper.deleteByPrimaryKey(id);
                //正常情况下不会有以下情况
				redisTemplate.boundHashOps("content").delete(id);
            } else {
                notAllowDelids.add(id);
            }
        }
        return notAllowDelids;
    }


    @Override
    public PageResult findPage(TbContentCategory contentCategory, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbContentCategoryExample example = new TbContentCategoryExample();
        Criteria criteria = example.createCriteria();

        if (contentCategory != null) {
            if (contentCategory.getName() != null && contentCategory.getName().length() > 0) {
                criteria.andNameLike("%" + contentCategory.getName() + "%");
            }
        }

        Page<TbContentCategory> page = (Page<TbContentCategory>) contentCategoryMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

}
