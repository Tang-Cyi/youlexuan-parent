package com.offcn.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.entity.PageResult;
import com.offcn.entity.Result;
import com.offcn.pojo.TbSeller;
import com.offcn.sellergoods.service.SellerService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/seller")
public class SellerController {

	@Reference
	private SellerService sellerService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbSeller> findAll(){
		return sellerService.findAll();
	}


	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult findPage(int page, int rows){
		return sellerService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param seller
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbSeller seller){
		try {
			//密码加密
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			String password = passwordEncoder.encode(seller.getPassword());
			seller.setPassword(password);
			//设置为审核状态0
			seller.setStatus("0");
			seller.setCreateTime(new Date());
			sellerService.add(seller);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	/**
	 * 修改
	 * @param seller
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbSeller seller){
		try {
			sellerService.update(seller);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}
	/**
	 * 修改
	 * @param
	 * @return
	 */
	@RequestMapping("/updatePassword")
	public Result updatePassword(String oldPassword,String newPassword){
		String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
		try {
			//密码加密
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			int row=sellerService.updatePassword(sellerId,passwordEncoder.encode(oldPassword), passwordEncoder.encode(newPassword));
			if (row>0){
				return new Result(true, "修改成功");
			}
			return new Result(false, "密码错误");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}
	
	/**
	 * 获取实体
	 * @param sellerId
	 * @return
	 */
	@RequestMapping("/findOne")
	public TbSeller findOne(@RequestParam(defaultValue = "0",value = "sellerId",required = true) String sellerId){
		if ("0".equals(sellerId)) sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
		return sellerService.findOne(sellerId);		
	}
	
	/**
	 * 批量删除
	 * @param sellerIds
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(String [] sellerIds){
		try {
			sellerService.delete(sellerIds);
			return new Result(true, "删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param seller
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbSeller seller, int page, int rows  ){
		return sellerService.findPage(seller, page, rows);		
	}
	
}