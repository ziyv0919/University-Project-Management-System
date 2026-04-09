package com.spm.school_project_manage.service;

import com.spm.school_project_manage.utils.PageResult;
import com.spm.school_project_manage.utils.Result;
import com.spm.school_project_manage.vo.ProjectStudentsPageVo;
import java.util.List;

public interface ProjectStudentsService {
    /**
     * 分页查询所有课程选课结果
     */
    Result<PageResult<List<ProjectStudentsPageVo>>> findAll(String pageIndex, String pageSize, String psId, String uId, String pId, String uName, String pTitle);

    /**
     * 根据ID查询课程选课结果
     */
    Result<ProjectStudentsPageVo> find(String psId);

    /**
     * 新增课程选课结果
     */
    Result<Integer> insert(String uId, String pId);

    /**
     * 删除课程选课结果
     */
    Result<Integer> delete(String psId);
}