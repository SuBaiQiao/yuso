package com.yupi.springbootinit.datasource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 数据源接口（新接入数据源必须实现）
 * @param <T>
 */
public interface DataSource <T> {
    /**
     * 搜索
     * @param searchText 搜索关键词
     * @param pageNum 页码
     * @param pageSize 单页数量
     * @return 查询到的分页数据
     */
    Page<T> doSearch(String searchText, long pageNum, long pageSize);
}
