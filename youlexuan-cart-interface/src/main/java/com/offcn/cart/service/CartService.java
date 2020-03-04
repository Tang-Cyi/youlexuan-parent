package com.offcn.cart.service;

import com.offcn.entity.Cart;

import java.util.List;

/**
 * 购物车服务接口
 */
public interface CartService {
    /**
     *  将cookie 和redis合并
     * @param cartList1
     * @param cartList2
     * @return
     */
    List<Cart> mergeCartList(List<Cart> cartList1,List<Cart> cartList2);

    /**
     *  添加商品数据
     * @param cartList 原购物车 Cart购物项
     * @param itemId  商品id
     * @param num 数量
     * @return 返回新的购物车
     */
    List<Cart> addGoodsToCartList(List<Cart> cartList,Long itemId,Integer num);

    /**
     * 从redis中查询购物车
     * @param username
     * @return
     */
    List<Cart> findCartListFromRedis(String username);

    /**
     * 将购物车保存到redis
     * @param username
     * @param cartList
     */
    void saveCartListToRedis(String username,List<Cart> cartList);



}
