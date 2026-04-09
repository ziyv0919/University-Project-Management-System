package com.spm.school_project_manage.dao;

import java.util.List;
import java.util.Map;

import com.spm.school_project_manage.dto.ProjectApplicationsPageDto;

public interface ProjectApplicationsDao {
    /**
     * 新增课程申请
     */
    Integer insert(String uId, String pId);

    /**
     * 更新课程申请
     */
    Integer update(String paId, String uId, String pId);

    /**
     * 删除课程申请（逻辑删除）
     */
    Integer delete(String paId);

    /**
     * 查询单个课程申请
     */
    ProjectApplicationsPageDto find(String paId);

    /**
     * 分页查询课程申请
     */
    List<ProjectApplicationsPageDto> findAll(int pageIndex, int pageSize, Map<String, Object> conditions);

    /**
     * 查询课程申请总数
     */
    long findAllCount(Map<String, Object> conditions);

    /**
     * 审核课程申请
     */
    Integer updateStatus(String paId, String paStatus);

    /**
     * 查询已通过申请的学生数量
     */
    long countApprovedStudents(String pId);

    /**
     * 查询学生是否已申请该课程
     */
    ProjectApplicationsPageDto findByStudentAndProject(String uId, String pId);
}