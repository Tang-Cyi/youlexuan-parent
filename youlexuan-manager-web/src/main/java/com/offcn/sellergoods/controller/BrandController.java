package com.offcn.sellergoods.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.entity.PageResult;
import com.offcn.entity.Result;
import com.offcn.pojo.TbBrand;
import com.offcn.sellergoods.service.BrandService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand")
public class BrandController {
    @Reference
    private BrandService brandService;
    @RequestMapping("/findAll")
    public List<TbBrand> findAll(){
        return brandService.findAll();
    }
    @RequestMapping("/findPage")
    public PageResult findPage(@RequestParam(required = false,value = "pageNum",defaultValue = "1") int pageNum,
                               @RequestParam(required = false,value = "pageSize",defaultValue = "10") int pageSize){
        return brandService.findPage(pageNum, pageSize);
    }
    @RequestMapping("/add")
    public Result add(@RequestBody TbBrand tbBrand){
        return brandService.add(tbBrand);
    }

    @RequestMapping("/findOne")
    public TbBrand findById(Long id){
        return brandService.findById(id);
    }

    @RequestMapping("/update")
    public Result update(@RequestBody TbBrand brand){
        return brandService.update(brand);
    }
    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        return brandService.delete(ids);
    }
    @RequestMapping("/search")
    public PageResult search(@RequestBody TbBrand brand,
                             @RequestParam(required = false,value = "pageNum",defaultValue = "1") int pageNum,
                             @RequestParam(required = false,value = "pageSize",defaultValue = "10") int pageSize){
        return brandService.findPage(brand, pageNum, pageSize);
    }

}
