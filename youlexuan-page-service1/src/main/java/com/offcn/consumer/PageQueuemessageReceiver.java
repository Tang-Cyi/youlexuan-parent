package com.offcn.consumer;

import com.offcn.page.service.ItemPageService;
import com.offcn.pojo.TbItem;
import com.offcn.util.SerializeUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;


public class PageQueuemessageReceiver implements MessageListener {
    @Autowired
    private ItemPageService pageService;

    @Override
    public void onMessage(Message message) {
        byte[] bytes = message.getBody();
        Map map = (Map) SerializeUtils.unserialize(bytes);
        String flag = map.get("flag") + "";
        Long[] ids = (Long[]) map.get("data");
        if (flag.equals("商品详情页生成")) {
            for (Long id : ids) {
                pageService.genItemHtml(id);
            }
        } else if (flag.equals("商品详情页删除")) {
            pageService.deleteItemHtml(ids);
        } else {

        }


        /*for (Long id : ids) {
            pageService.genItemHtml(id);
        }*/
    }
}
