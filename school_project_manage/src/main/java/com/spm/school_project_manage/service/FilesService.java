package com.spm.school_project_manage.service;

import java.util.List;
import java.util.Map;

import com.spm.school_project_manage.utils.PageResult;
import com.spm.school_project_manage.utils.Result;
import com.spm.school_project_manage.vo.FilesPageVo;

public interface FilesService {
    /**
     * 批量新增文件
     * @param filesList 文件列表
     * @return 操作结果
     */
    Result<Integer> batchInsert(List<Map<String, Object>> filesList);
    
    /**
     * 删除单个文件
     * @param fId 文件ID
     * @return 操作结果
     */
    Result<Integer> delete(String fId);
    
    /**
     * 批量删除文件
     * @param fIds 文件ID列表
     * @return 操作结果
     */
    Result<Integer> batchDelete(List<String> fIds);
    
    /**
     * 查询课题对应的所有附件
     * @param pId 课题ID
     * @return 文件列表
     */
    Result<List<FilesPageVo>> findByProjectId(String pId);
    
    /**
     * 分页查询文件
     * @param pageIndex 页码
     * @param pageSize 每页大小
     * @param fId 文件ID
     * @param pId 课题ID
     * @param fOriginalName 文件名称
     * @return 分页结果
     */
    Result<PageResult<List<FilesPageVo>>> findAll(String pageIndex, String pageSize, String fId, String pId, String fOriginalName);
    
    /**
     * 查询单个文件
     * @param fId 文件ID
     * @return 文件信息
     */
    Result<FilesPageVo> find(String fId);
}