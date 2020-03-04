package com.offcn.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.pojo.TbSeller;
import com.offcn.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


import java.util.ArrayList;
import java.util.List;

/**
 * 认证类
 *
 * @author Administrator
 */
public class UserDetailsServiceImpl implements UserDetailsService {


    @Reference
    private SellerService sellerService; //远程调用商家商品服务

    /*通过spring配置bean方式*/
    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //得到商家对象，这个username就是商家表中的seller_id
        TbSeller seller = sellerService.findOne(username);
        if (seller != null) {
            if (seller.getStatus().equals("1")) {  //商家状态为1代表已审核
                return new User(username, seller.getPassword(), getGrantedByUserName(username));
            }
        }
        return null;
    }

    private List<GrantedAuthority> getGrantedByUserName(String username) {
        //构建角色列表（角色数据也是可以根据用户名从数据库中查询的）
        List<GrantedAuthority> grantAuths = new ArrayList();
        grantAuths.add(new SimpleGrantedAuthority("ROLE_SELLER"));
        return grantAuths;
    }
}

