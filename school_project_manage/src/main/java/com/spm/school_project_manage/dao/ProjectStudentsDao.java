package com.spm.school_project_manage.dao;

import com.spm.school_project_manage.dto.ProjectStudentsPageDto;
import java.util.List;
import java.util.Map;

public interface ProjectStudentsDao {
    /**
     * 分页查询所有课程选课结果
     */
    List<ProjectStudentsPageDto> findAll(int pageIndex, int pageSize, Map<String, Object> conditions);

    /**
     * 查询课程选课结果总数
     */
    long findAllCount(Map<String, Object> conditions);

    /**
     * 根据ID查询课程选课结果
     */
    ProjectStudentsPageDto find(String psId);

    /**
     * 新增课程选课结果
     */
    Integer insert(String uId, String pId);

    /**
     * 删除课程选课结果
     */
    Integer delete(String psId);

    ProjectStudentsPageDto findByUIdAndPId(String uId, String pId);
}