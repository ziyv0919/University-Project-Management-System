package com.spm.school_project_manage.dao;

import com.spm.school_project_manage.dto.TagsPageDto;
import java.util.List;
import java.util.Map;

public interface TagsDao {
    /**
     * 根据标签名查找标签
     * @param tName 标签名
     * @return 标签信息
     */
    TagsPageDto findByName(String tName);

    /**
     * 新增标签
     * @param tName 标签名
     * @return 影响行数
     */
    Integer insert(String tName);

    /**
     * 分页查询标签列表
     * @param pageIndex 页码
     * @param pageSize 每页大小
     * @param conditions 查询条件
     * @return 标签列表
     */
    List<TagsPageDto> findAll(int pageIndex, int pageSize, Map<String, Object> conditions);

    /**
     * 查询标签总数
     * @param conditions 查询条件
     * @return 标签总数
     */
    long findAllCount(Map<String, Object> conditions);

    /**
     * 根据ID查询标签
     * @param tId 标签ID
     * @return 标签信息
     */
    TagsPageDto find(String tId);

    /**
     * 更新标签
     * @param tId 标签ID
     * @param tName 标签名
     * @return 影响行数
     */
    Integer update(String tId, String tName);

    /**
     * 删除标签（逻辑删除）
     * @param tId 标签ID
     * @return 影响行数
     */
    Integer delete(String tId);

    List<TagsPageDto> select();
}