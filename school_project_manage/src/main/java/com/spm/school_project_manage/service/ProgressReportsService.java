package com.spm.school_project_manage.service;

import com.spm.school_project_manage.utils.PageResult;
import com.spm.school_project_manage.utils.Result;
import com.spm.school_project_manage.vo.ProgressReportsPageVo;
import java.util.List;
import java.util.Map;

public interface ProgressReportsService {
    /**
     * 分页查询所有课程阶段性进展
     */
    Result<PageResult<List<ProgressReportsPageVo>>> findAll(String pageIndex, String pageSize, String prId, String psId, String prTitle, String uName, String pTitle, String pId);

    /**
     * 根据ID查询课程阶段性进展
     */
    Result<ProgressReportsPageVo> find(String prId);

    /**
     * 新增课程阶段性进展
     */
    Result<Integer> insert(String psId, String prTitle, String prContent);

    /**
     * 更新课程阶段性进展
     */
    Result<Integer> update(String prId, String prTitle, String prContent);

    /**
     * 教师评价课程阶段性进展
     */
    Result<Integer> evaluate(String prId, String prTeacherComment, String prScore);

    /**
     * 删除课程阶段性进展
     */
    Result<Integer> delete(String prId);
    
    /**
     * 批量新增课程阶段性进展
     */
    Result<Integer> batchInsert(List<Map<String, String>> reportsList);
}