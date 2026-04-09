package com.spm.school_project_manage.service.impl;

import com.spm.school_project_manage.dao.impl.ProjectStudentsDaoImpl;
import com.spm.school_project_manage.dao.impl.ProjectsDaoImpl;
import com.spm.school_project_manage.dao.impl.UsersDaoImpl;
import com.spm.school_project_manage.dto.ProjectStudentsPageDto;
import com.spm.school_project_manage.dto.ProjectsPageDto;
import com.spm.school_project_manage.service.ProjectStudentsService;
import com.spm.school_project_manage.utils.PageResult;
import com.spm.school_project_manage.utils.Result;
import com.spm.school_project_manage.vo.ProjectStudentsPageVo;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectStudentsServiceImpl implements ProjectStudentsService {

    ProjectStudentsDaoImpl projectStudentsDaoImpl = new ProjectStudentsDaoImpl();

    @Override
    public Result<PageResult<List<ProjectStudentsPageVo>>> findAll(String pageIndex, String pageSize, String psId, String uId, String pId, String uName, String pTitle) {
        int pageIndexInt = Integer.parseInt(pageIndex);
        int pageSizeInt = Integer.parseInt(pageSize);

        Map<String, Object> conditions = new HashMap<>();
        if (psId != null && !psId.trim().isEmpty()) {
            conditions.put("ps.psId:=", psId);
        }
        if (uId != null && !uId.trim().isEmpty()) {
            conditions.put("ps.uId:=", uId);
        }
        if (pId != null && !pId.trim().isEmpty()) {
            conditions.put("ps.pId:=", pId);
        }
        if (uName != null && !uName.trim().isEmpty()) {
            conditions.put("u.uName:LIKE", uName);
        }
        if (pTitle != null && !pTitle.trim().isEmpty()) {
            conditions.put("p.pTitle:LIKE", pTitle);
        }

        List<ProjectStudentsPageDto> projectStudentsPageDtos = projectStudentsDaoImpl.findAll(pageIndexInt, pageSizeInt, conditions);
        long count = projectStudentsDaoImpl.findAllCount(conditions);

        if (projectStudentsPageDtos != null) {
            PageResult pageResult = new PageResult(pageIndexInt, pageSizeInt, count, projectStudentsPageDtos);
            Result<PageResult<List<ProjectStudentsPageVo>>> result = Result.success(pageResult, ProjectStudentsPageVo.class);
            return result;
        } else {
            Result result = Result.error("数据库执行错误");
            return result;
        }
    }

    @Override
    public Result<ProjectStudentsPageVo> find(String psId) {
        ProjectStudentsPageDto projectStudentsPageDto = projectStudentsDaoImpl.find(psId);
        if (projectStudentsPageDto == null) {
            return Result.error("选课记录不存在");
        }
        return Result.success(projectStudentsPageDto, ProjectStudentsPageVo.class);
    }

    @Override
    public Result<Integer> insert(String uId, String pId) {
        // 验证用户是否存在
        UsersDaoImpl usersDaoImpl = new UsersDaoImpl();
        if (usersDaoImpl.find(uId) == null) {
            return Result.error("用户不存在");
        }
        
        // 验证项目是否存在
        ProjectsDaoImpl projectsDaoImpl = new ProjectsDaoImpl();
        ProjectsPageDto project = projectsDaoImpl.find(pId);
        if (project == null) {
            return Result.error("项目不存在");
        }
        
        // 检查用户是否已经选择了该项目
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("ps.uId:=", uId);
        conditions.put("ps.pId:=", pId);
        long existingAssignment = projectStudentsDaoImpl.findAllCount(conditions);
        if (existingAssignment > 0) {
            return Result.error("您已经选择了该项目");
        }
        
        // 检查项目是否还有可用名额
        conditions.clear();
        conditions.put("ps.pId:=", pId);
        long currentStudents = projectStudentsDaoImpl.findAllCount(conditions);
        if (currentStudents >= project.getPMaxStudents()) {
            return Result.error("该项目已达到最大选课人数");
        }
        
        // 所有验证通过，执行插入操作
        Integer result = projectStudentsDaoImpl.insert(uId, pId);
        if (result > 0) {
            return Result.success(result);
        } else {
            return Result.error("选课失败");
        }
    }

    @Override
    public Result<Integer> delete(String psId) {
        ProjectStudentsPageDto projectStudentsPageDto = projectStudentsDaoImpl.find(psId);
        if (projectStudentsPageDto == null) {
            return Result.error("选课记录不存在");
        }

        Integer result = projectStudentsDaoImpl.delete(psId);
        if (result > 0) {
            return Result.success(result);
        } else {
            return Result.error("删除失败");
        }
    }
}