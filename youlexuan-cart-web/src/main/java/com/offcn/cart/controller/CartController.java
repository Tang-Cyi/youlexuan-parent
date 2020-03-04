package com.offcn.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.offcn.cart.service.CartService;
import com.offcn.entity.Cart;
import com.offcn.entity.Result;
import com.offcn.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Reference(timeout = 60000)
    private CartService cartService;

    @Autowired
    private HttpServletRequest request;

    @Resource
    private HttpServletResponse response;

    @RequestMapping("/findCartList")
    public List<Cart> findCartList() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("用户名：" + username);

        //判断有没有登录，没有登陆从cookie中读取
        String cartList = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
        if (cartList == null || cartList.equals("")) {
            //没有购物车
            cartList = "[]";
        }
        List<Cart> carts_cookie = JSON.parseArray(cartList, Cart.class);

        if ("anonymousUser".equals(username)) {
            return carts_cookie;
        } else {
            //判断有没有登录，有登陆从redis中读取
            List<Cart> carts_redis = cartService.findCartListFromRedis(username);
            if (carts_redis==null) carts_redis=carts_cookie;
            //判断cookie是否有购物车
            if (carts_cookie.size() > 0) {
                //登陆成功，判断cookie是否有购物车

                carts_redis = cartService.mergeCartList(carts_redis, carts_cookie);
                CookieUtil.deleteCookie(request, response, "cartList");
                cartService.saveCartListToRedis(username, carts_redis);
            }
            return carts_redis;
        }


    }

    @RequestMapping("/addGoodsToCartList")
    @CrossOrigin(origins="http://localhost:9105",allowCredentials="true") //注解配置跨域访问
    public Result addGoodsToCartList(Long itemId, Integer num) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //加上* 号所有都可以跨域访问
       /* response.setHeader("Access-Control-Allow-Origin", "http://localhost:9105");
        response.setHeader("Access-Control-Allow-Credentials", "true");*/

        try {
            List<Cart> cartList = findCartList();
            cartList = cartService.addGoodsToCartList(cartList, itemId, num);
            if ("anonymousUser".equals(username)) {
                //判断有没有登录，没有登陆从cookie中存储
                CookieUtil.setCookie(request, response, "cartList", JSON.toJSONString(cartList), 3600 * 4, "UTF-8");
            } else {
                cartService.saveCartListToRedis(username, cartList);
            }
            return new Result(true, "添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加失败");
        }

    }


}
