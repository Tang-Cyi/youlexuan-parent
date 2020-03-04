package com.offcn.search.service;

import com.offcn.pojo.TbItem;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {
    /**
     * 搜索
     * @param searchMap
     * @return
     */
    Map<String,Object> search(Map searchMap);

    /**
     *
     * @param items
     */
    void importItemList(List<TbItem> items);

    /**
     *
     * @param ids
     */
    void removeItemList(Long[] ids);
}
