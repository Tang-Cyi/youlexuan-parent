package com.offcn.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.offcn.cart.service.CartService;
import com.offcn.entity.Cart;
import com.offcn.mapper.TbItemMapper;
import com.offcn.pojo.TbItem;
import com.offcn.pojo.TbOrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
        if (cartList1==null||cartList1.size()==0) return cartList2;
        if (cartList2==null||cartList2.size()==0) return cartList1;
        //两个集合合并数据
        for (Cart cart : cartList2) {
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            for (TbOrderItem orderItem : orderItemList) {
                Integer num = orderItem.getNum();
                Long itemId = orderItem.getItemId();
                cartList1 =addGoodsToCartList(cartList1,itemId,num);
            }

        }
        return cartList1;
    }

    //从cookie或者redis中读取出来的 cartList
    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //查询商品信息
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        //获取商家id
        String sellerId = item.getSellerId();
        //根据sellerId 查询 购物车 里面有没有相关的购物项Cart
        Cart cart = searchCartBySellerId(cartList, sellerId);

        if (cart == null) {
            //如果Cart为空
            //则新建一个当前商家的Cart
            cart = new Cart();
            cart.setSellerId(sellerId);
            cart.setSellerName(item.getSeller());

            TbOrderItem tbOrderItem = createOrderItem(item, num);
            List<TbOrderItem> orderItemsList = new ArrayList<>();
            orderItemsList.add(tbOrderItem);
            cart.setOrderItemList(orderItemsList);

            cartList.add(cart);
        } else {
            //否则说明购物车有对应的Cart
            //判断商品是否在Cart的orderItemList中
            TbOrderItem orderItem=searchOrderItemByItemId(cart.getOrderItemList(),itemId);
            if (orderItem==null){
                //如果为空，该商品没有添加过
                orderItem=createOrderItem(item,num);
                //把购物明细添加到原集合中
                cart.getOrderItemList().add(orderItem);
            }else {
                //否则购物明细已经存在，num相加，totalfee重新计算
                orderItem.setNum(orderItem.getNum()+num);
                orderItem.setTotalFee(new BigDecimal(orderItem.getNum()*orderItem.getPrice().doubleValue()));
                if (orderItem.getNum()<=0){
                    cart.getOrderItemList().remove(orderItem);
                }
                //如果cart集合的购物明细list小于等于0 即一条购物明细商品都没有；移除cart;
                if (cart.getOrderItemList().size()<=0){
                    cartList.remove(cart);
                }

            }
        }

        return cartList;
    }
    //从redis中查询购物车
    @Override
    public List<Cart> findCartListFromRedis(String username) {
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);

        System.out.println("从redis中查询购物车");
        return cartList;
    }
    //将购物车保存到redis
    @Override
    public void saveCartListToRedis(String username, List<Cart> cartList) {

        redisTemplate.boundHashOps("cartList").put(username,cartList);
        System.out.println("将购物车保存到redis");
    }

    /**
     *  查询商品明细
     * @param orderItemList
     * @param itemId
     * @return
     */
    private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList, Long itemId) {
        for (TbOrderItem tbOrderItem : orderItemList) {
            //
            if (tbOrderItem.getItemId().doubleValue()==itemId.longValue()){
                return tbOrderItem;
            }
        }
        return null;
    }

    /**
     *  创建商品明细
     * @param item
     * @param num
     * @return
     */
    private TbOrderItem createOrderItem(TbItem item, Integer num) {
        TbOrderItem orderItem = new TbOrderItem();//新建一个购物明细
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setNum(num);
        orderItem.setPrice(item.getPrice());
        orderItem.setPicPath(item.getImage());
        orderItem.setSellerId(item.getSellerId());
        orderItem.setTitle(item.getTitle());
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));
        return orderItem;
    }

    /**
     * 通过sellerId 查询Cart
     * @param cartList
     * @param sellerId
     * @return
     */
    private Cart searchCartBySellerId(List<Cart> cartList, String sellerId) {
        if (cartList==null ) return null;
        for (Cart cart : cartList) {
            if (cart.getSellerId().equals(sellerId)){
                return cart;
            }
        }
        return null;
    }
}
