package com.offcn.sellergoods.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.entity.Goods;
import com.offcn.entity.PageResult;
import com.offcn.entity.Result;
import com.offcn.page.service.ItemPageService;
import com.offcn.pojo.TbGoods;
import com.offcn.pojo.TbItem;
import com.offcn.pojo.TbItemExample;
import com.offcn.search.service.ItemSearchService;
import com.offcn.sellergoods.service.GoodsService;
import com.offcn.sellergoods.service.ItemService;
import com.offcn.util.SerializeUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference(timeout = 60000)
	private GoodsService goodsService;
	/*@Reference(timeout = 60000)
	private ItemSearchService searchService;*/
	@Reference(timeout = 60000)
	private ItemService itemService;
	/*@Reference(timeout = 60000)
	private ItemPageService itemPageService;*/

	@Autowired
	private AmqpTemplate amqpTemplate;

	private Map messageData(String flag,Object data){
		Map map=new HashMap<>();
		map.put("flag",flag);
		map.put("data",data);
		return map;
	}


	/**
	 * 更新状态
	 * @param ids
	 * @param auditStatus
	 */
	@RequestMapping("/updateStatus")
	public Result updateAuditStatus(Long[] ids, String auditStatus){
		try {
			goodsService.updateAuditStatus(ids, auditStatus);

			if (auditStatus.equals("1")){
				//sku集合
				List<TbItem> itemList = itemService.getItemList(ids);


				//序列化
				byte[] itemListSerialize = SerializeUtils.serialize(messageData("搜索数据存储",itemList));
				byte[] idsSerialize = SerializeUtils.serialize(messageData("商品详情页生成",ids));
				amqpTemplate.convertAndSend("searchQueueKey",itemListSerialize);
				amqpTemplate.convertAndSend("pageQueueKey",idsSerialize);

				/*searchService.importItemList(itemList);

				for (Long id : ids) {
					itemPageService.genItemHtml(id);
				}*/
			}

			return new Result(true, "成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "失败");
		}
	}


	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult findPage(int page, int rows){
		return goodsService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody Goods goods){
		try {
			goodsService.add(goods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbGoods goods){
		try {
			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public TbGoods findOne(Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			goodsService.delete(ids);

			/*searchService.removeItemList(ids);
			//删除页面
			itemPageService.deleteItemHtml(ids);*/

			//序列化
			byte[] idsSerialize1 = SerializeUtils.serialize(messageData("商品详情页删除",ids));
			byte[] idsSerialize2 = SerializeUtils.serialize(messageData("搜索数据删除",ids));

			amqpTemplate.convertAndSend("pageQueueKey",idsSerialize1);
			amqpTemplate.convertAndSend("searchQueueKey",idsSerialize2);
			return new Result(true, "删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param goods
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){
		return goodsService.findPage(goods, page, rows);		
	}
	/*@RequestMapping("/genHtml")
	public void  genItemHtml(Long goodsId){
		itemPageService.genItemHtml(goodsId);
	}*/
	
}
