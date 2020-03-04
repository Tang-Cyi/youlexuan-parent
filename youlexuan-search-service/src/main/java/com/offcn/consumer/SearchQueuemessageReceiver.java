package com.offcn.consumer;

import com.offcn.pojo.TbItem;
import com.offcn.search.service.ItemSearchService;
import com.offcn.util.SerializeUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public class SearchQueuemessageReceiver implements MessageListener {
    @Autowired
    ItemSearchService searchService;
    @Override
    public void onMessage(Message message) {
        byte[] bytes = message.getBody();
        Map map = (Map) SerializeUtils.unserialize(bytes);
        String flag = map.get("flag") + "";
        if (flag.equals("搜索数据存储")){
            List<TbItem> itemList = (List<TbItem>)  map.get("data");
            searchService.importItemList(itemList);
        }else if (flag.equals("搜索数据删除")){
            Long[] ids = (Long[]) map.get("data");
            searchService.removeItemList(ids);
        }else {

        }
        /*List<TbItem> itemList = (List<TbItem>) SerializeUtils.unserialize(bytes);
        searchService.importItemList(itemList);*/

    }
}

