package com.spm.school_project_manage.dao;

import java.util.List;
import java.util.Map;

import com.spm.school_project_manage.dto.FilesPageDto;

public interface FilesDao {
    /**
     * 批量新增文件
     * @param filesList 文件列表
     * @return 影响行数
     */
    Integer batchInsert(List<Map<String, Object>> filesList);
    
    /**
     * 删除单个文件
     * @param fId 文件ID
     * @return 影响行数
     */
    Integer delete(String fId);
    
    /**
     * 批量删除文件
     * @param fIds 文件ID列表
     * @return 影响行数
     */
    Integer batchDelete(List<String> fIds);
    
    /**
     * 查询课题对应的所有附件
     * @param pId 课题ID
     * @return 文件列表
     */
    List<FilesPageDto> findByProjectId(String pId);
    
    /**
     * 分页查询文件
     * @param pageIndex 页码
     * @param pageSize 每页大小
     * @param conditions 查询条件
     * @return 文件列表
     */
    List<FilesPageDto> findAll(int pageIndex, int pageSize, Map<String, Object> conditions);
    
    /**
     * 查询文件总数
     * @param conditions 查询条件
     * @return 文件总数
     */
    long findAllCount(Map<String, Object> conditions);
    
    /**
     * 查询单个文件
     * @param fId 文件ID
     * @return 文件信息
     */
    FilesPageDto find(String fId);
}