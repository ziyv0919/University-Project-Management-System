package com.spm.school_project_manage.dao;

import com.spm.school_project_manage.dto.ProgressReportsPageDto;
import java.util.List;
import java.util.Map;

public interface ProgressReportsDao {
    /**
     * 分页查询所有课程阶段性进展
     */
    List<ProgressReportsPageDto> findAll(int pageIndex, int pageSize, Map<String, Object> conditions);

    /**
     * 查询总记录数
     */
    long findAllCount(Map<String, Object> conditions);

    /**
     * 根据ID查询课程阶段性进展
     */
    ProgressReportsPageDto find(String prId);

    /**
     * 新增课程阶段性进展
     */
    Integer insert(String psId, String prTitle, String prContent);

    /**
     * 更新课程阶段性进展
     */
    Integer update(String prId, String prTitle, String prContent);

    /**
     * 教师评价课程阶段性进展
     */
    Integer evaluate(String prId, String prTeacherComment, int prScore);

    /**
     * 删除课程阶段性进展
     */
    Integer delete(String prId);
}