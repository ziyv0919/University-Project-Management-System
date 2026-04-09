package com.spm.school_project_manage.service;

import com.spm.school_project_manage.utils.PageResult;
import com.spm.school_project_manage.utils.Result;
import com.spm.school_project_manage.vo.TagsPageVo;
import java.util.List;

public interface TagsService {
    /**
     * 新增标签
     * @param tName 标签名
     * @return 操作结果
     */
    Result<Integer> insert(String tName);

    /**
     * 分页查询标签列表
     * @param pageIndex 页码
     * @param pageSize 每页大小
     * @param tId 标签ID
     * @param tName 标签名
     * @return 标签列表
     */
    Result<PageResult<List<TagsPageVo>>> findAll(String pageIndex, String pageSize, String tId, String tName);

    /**
     * 根据ID查询标签
     * @param tId 标签ID
     * @return 标签信息
     */
    Result<TagsPageVo> find(String tId);

    /**
     * 更新标签
     * @param tId 标签ID
     * @param tName 标签名
     * @return 操作结果
     */
    Result<Integer> update(String tId, String tName);

    /**
     * 删除标签
     * @param tId 标签ID
     * @return 操作结果
     */
    Result<Integer> delete(String tId);

    /**
     * 远程检索标签
     * @param searchText 搜索文本
     * @return 标签列表
     */
    Result<List<TagsPageVo>> remoteSelect(String searchText);

    Result<List<TagsPageVo>> select();
}