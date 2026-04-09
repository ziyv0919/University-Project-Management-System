package com.spm.school_project_manage.service;

import java.util.List;

import com.spm.school_project_manage.utils.PageResult;
import com.spm.school_project_manage.utils.Result;
import com.spm.school_project_manage.vo.ProjectApplicationsPageVo;

public interface ProjectApplicationsService {
    /**
     * 新增课程申请
     */
    Result<Integer> insert(String uId, String pId);

    /**
     * 更新课程申请（仅审核中状态可编辑）
     */
    Result<Integer> update(String paId, String uId, String pId);

    /**
     * 删除课程申请（仅审核中状态可删除）
     */
    Result<Integer> delete(String paId);

    /**
     * 查询单个课程申请
     */
    Result<ProjectApplicationsPageVo> find(String paId);

    /**
     * 分页查询课程申请
     */
    Result<PageResult<List<ProjectApplicationsPageVo>>> findAll(String pageIndex, String pageSize, String paId, String uId, String pId, String paStatus);

    /**
     * 审核课程申请
     * paStatus: 2-通过, 3-不通过
     */
    Result<Integer> approve(String paId, String paStatus);
}