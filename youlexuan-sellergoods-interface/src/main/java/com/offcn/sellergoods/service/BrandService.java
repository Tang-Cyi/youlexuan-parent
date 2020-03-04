package com.offcn.sellergoods.service;

import com.offcn.entity.PageResult;
import com.offcn.entity.Result;
import com.offcn.pojo.TbBrand;

import java.util.List;
import java.util.Map;

public interface BrandService {
    List<TbBrand> findAll();

    /**
     *  分页查询
     * @param pageNum 查询的页数
     * @param pageSize  每页显示的条数
     * @return
     */
    PageResult findPage(int pageNum,int pageSize);
    PageResult findPage(TbBrand brand, int pageNum,int pageSize);

    /**
     * 新增品牌
     * @param tbBrand
     * @return
     */
    Result add(TbBrand tbBrand);

    /**
     * 根据id来查询
     * @param id
     * @return
     */
    TbBrand findById( Long id);

    /**
     * 修改品牌
     * @param tbBrand
     * @return
     */
    Result update(TbBrand tbBrand);

    /**
     * 批量删除品牌
     * @param ids
     * @return
     */
    Result delete(Long[] ids);

    /**
     * 品牌下拉框数据
     */
    List<Map> selectOptionList();

}
