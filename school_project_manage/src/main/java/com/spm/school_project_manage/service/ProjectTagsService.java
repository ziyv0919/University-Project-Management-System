package com.spm.school_project_manage.service;

import java.util.List;

import com.spm.school_project_manage.utils.PageResult;
import com.spm.school_project_manage.utils.Result;
import com.spm.school_project_manage.vo.ProjectsPageVo;
import com.spm.school_project_manage.vo.TagsPageVo;

public interface ProjectTagsService {
    /**
     * 为课题分配标签
     * @param pId 课题ID
     * @param tId 标签ID
     * @return 操作结果
     */
    Result<Integer> assignTag(String pId, String tId);
    
    /**
     * 批量为课题分配标签
     * @param pId 课题ID
     * @param tagIds 标签ID列表
     * @return 操作结果
     */
    Result<Integer> batchAssignTags(String pId, List<String> tagIds);

    /**
     * 删除课题标签关联
     * @param ptId 关联ID
     * @return 操作结果
     */
    Result<Integer> removeTag(String ptId);
    
    /**
     * 删除课题的所有标签
     * @param pId 课题ID
     * @return 操作结果
     */
    Result<Integer> removeAllTags(String pId);

    /**
     * 根据课题ID查询标签
     * @param pId 课题ID
     * @return 标签列表
     */
    Result<List<TagsPageVo>> findTagsByProjectId(String pId);

    /**
     * 根据标签ID查询课题
     * @param tId 标签ID
     * @param pageIndex 页码
     * @param pageSize 每页大小
     * @return 课题列表
     */
    Result<PageResult<List<ProjectsPageVo>>> findProjectsByTagId(String tId, String pageIndex, String pageSize);
}