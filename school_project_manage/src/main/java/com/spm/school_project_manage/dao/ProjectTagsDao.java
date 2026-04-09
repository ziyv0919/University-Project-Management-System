package com.spm.school_project_manage.dao;

import java.util.List;

import com.spm.school_project_manage.dto.ProjectTagsPageDto;
import com.spm.school_project_manage.dto.ProjectsPageDto;

public interface ProjectTagsDao {
    /**
     * 为课题分配标签
     * @param pId 课题ID
     * @param tId 标签ID
     * @return 影响行数
     */
    Integer insert(String pId, String tId);
    
    /**
     * 批量为课题分配标签
     * @param pId 课题ID
     * @param tagIds 标签ID列表
     * @return 影响行数
     */
    Integer batchInsert(String pId, List<String> tagIds);

    /**
     * 删除课题标签关联
     * @param ptId 关联ID
     * @return 影响行数
     */
    Integer delete(String ptId);
    
    /**
     * 删除课题的所有标签
     * @param pId 课题ID
     * @return 影响行数
     */
    Integer deleteByProjectId(String pId);

    /**
     * 根据课题ID查询标签
     * @param pId 课题ID
     * @return 标签列表
     */
    List<ProjectTagsPageDto> findByProjectId(String pId);

    /**
     * 根据标签ID查询课题
     * @param tId 标签ID
     * @param pageIndex 页码
     * @param pageSize 每页大小
     * @return 课题列表
     */
    List<ProjectsPageDto> findProjectsByTagId(String tId, int pageIndex, int pageSize);
    
    /**
     * 查询标签关联的课题总数
     * @param tId 标签ID
     * @return 课题总数
     */
    long findProjectsCountByTagId(String tId);
    
    /**
     * 查询课题与标签关联是否存在
     * @param pId 课题ID
     * @param tId 标签ID
     * @return 关联信息
     */
    ProjectTagsPageDto findByProjectAndTag(String pId, String tId);
}