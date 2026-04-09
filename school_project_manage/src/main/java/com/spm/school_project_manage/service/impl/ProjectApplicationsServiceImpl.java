package com.spm.school_project_manage.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.spm.school_project_manage.dao.impl.ProjectApplicationsDaoImpl;
import com.spm.school_project_manage.dao.impl.ProjectStudentsDaoImpl;
import com.spm.school_project_manage.dao.impl.ProjectsDaoImpl;
import com.spm.school_project_manage.dto.ProjectApplicationsPageDto;
import com.spm.school_project_manage.dto.ProjectStudentsPageDto;
import com.spm.school_project_manage.dto.ProjectsPageDto;
import com.spm.school_project_manage.service.ProjectApplicationsService;
import com.spm.school_project_manage.utils.PageResult;
import com.spm.school_project_manage.utils.Result;
import com.spm.school_project_manage.vo.ProjectApplicationsPageVo;

public class ProjectApplicationsServiceImpl implements ProjectApplicationsService {

    ProjectApplicationsDaoImpl projectApplicationsDaoImpl = new ProjectApplicationsDaoImpl();
    ProjectsDaoImpl projectsDaoImpl = new ProjectsDaoImpl();
    ProjectStudentsDaoImpl projectStudentsDaoImpl = new ProjectStudentsDaoImpl();

    @Override
    public Result<Integer> insert(String uId, String pId) {
        // 检查学生是否已申请该课程
        ProjectStudentsPageDto projectStudentsPageDto =  projectStudentsDaoImpl.findByUIdAndPId(uId, pId);
        if (projectStudentsPageDto != null) {
            return Result.error("您已申请过该课程");
        }
        
        // 检查学生是否已经选上该课程（在选课结果表中查询）
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("ps.uId:=", uId);
        conditions.put("ps.pId:=", pId);
        long existingAssignment = projectStudentsDaoImpl.findAllCount(conditions);
        if (existingAssignment > 0) {
            return Result.error("您已经选上该课程");
        }

        // 检查课程是否存在
        ProjectsPageDto project = projectsDaoImpl.find(pId);
        if (project == null) {
            return Result.error("课程不存在");
        }

        Integer result = projectApplicationsDaoImpl.insert(uId, pId);
        if (result > 0) {
            // 获取项目对应的教师ID
            String teacherId = String.valueOf(project.getUId());

            // 创建通知给教师
            NotificationsServiceImpl notificationsService = new NotificationsServiceImpl();
            String nTitle = "新的课题申请通知";
            String nContent = "学生(ID: " + uId + ")已申请您的课题：" + project.getPTitle();
            String nType = "2"; // 通知类型为2，表示学生申请课题

            // 发送通知
            notificationsService.insert(teacherId, nTitle, nContent, nType);
            return Result.success(result);
        } else {
            return Result.error("申请失败");
        }
    }

    @Override
    public Result<Integer> update(String paId, String uId, String pId) {
        // 检查申请是否存在且状态为审核中
        ProjectApplicationsPageDto application = projectApplicationsDaoImpl.find(paId);
        if (application == null) {
            return Result.error("申请不存在");
        }

        if (application.getPaStatus() != 1) {
            return Result.error("只有审核中的申请才能编辑");
        }

        // 检查课程是否存在
        ProjectsPageDto project = projectsDaoImpl.find(pId);
        if (project == null) {
            return Result.error("课程不存在");
        }

        Integer result = projectApplicationsDaoImpl.update(paId, uId, pId);
        if (result > 0) {
            return Result.success(result);
        } else {
            return Result.error("更新失败");
        }
    }

    @Override
    public Result<Integer> delete(String paId) {
        // 检查申请是否存在且状态为审核中
        ProjectApplicationsPageDto application = projectApplicationsDaoImpl.find(paId);
        if (application == null) {
            return Result.error("申请不存在");
        }

        if (application.getPaStatus() != 1) {
            return Result.error("只有审核中的申请才能删除");
        }

        Integer result = projectApplicationsDaoImpl.delete(paId);
        if (result > 0) {
            return Result.success(result);
        } else {
            return Result.error("删除失败");
        }
    }

    @Override
    public Result<ProjectApplicationsPageVo> find(String paId) {
        ProjectApplicationsPageDto application = projectApplicationsDaoImpl.find(paId);
        if (application == null) {
            return Result.error("申请不存在");
        }

        return Result.success(application, ProjectApplicationsPageVo.class);
    }

    @Override
    public Result<PageResult<List<ProjectApplicationsPageVo>>> findAll(String pageIndex, String pageSize, String paId, String uId, String pId, String paStatus) {
        int pageIndexInt = Integer.parseInt(pageIndex);
        int pageSizeInt = Integer.parseInt(pageSize);

        Map<String, Object> conditions = new HashMap<>();
        if (paId != null && !paId.trim().isEmpty()) {
            conditions.put("pa.paId:=", paId);
        }
        if (uId != null && !uId.trim().isEmpty()) {
            conditions.put("pa.uId:=", uId);
        }
        if (pId != null && !pId.trim().isEmpty()) {
            conditions.put("pa.pId:=", pId);
        }
        if (paStatus != null && !paStatus.trim().isEmpty()) {
            conditions.put("pa.paStatus:=", paStatus);
        }

        List<ProjectApplicationsPageDto> applications = projectApplicationsDaoImpl.findAll(pageIndexInt, pageSizeInt, conditions);
        long count = projectApplicationsDaoImpl.findAllCount(conditions);

        if (applications != null) {
            PageResult pageResult = new PageResult(pageIndexInt, pageSizeInt, count, applications);
            Result<PageResult<List<ProjectApplicationsPageVo>>> result = Result.success(pageResult, ProjectApplicationsPageVo.class);
            return result;
        } else {
            Result result = Result.error("数据库执行错误");
            return result;
        }
    }

    @Override
    public Result<Integer> approve(String paId, String paStatus) {
        // 检查申请是否存在
        ProjectApplicationsPageDto application = projectApplicationsDaoImpl.find(paId);
        if (application == null) {
            return Result.error("申请不存在");
        }
        
        // 检查申请状态是否为待审核(1)
        if (application.getPaStatus() != 1) {
            return Result.error("只能审核待审核状态的申请");
        }

        // 如果是审核通过，需要检查课程人数是否已满
        if ("2".equals(paStatus)) {
            // 获取课程信息
            ProjectsPageDto project = projectsDaoImpl.find(String.valueOf(application.getPId()));
            if (project == null) {
                return Result.error("课程不存在");
            }

            // 从选课结果表中获取已选课学生数量
            Map<String, Object> conditions = new HashMap<>();
            conditions.put("ps.pId:=", String.valueOf(application.getPId()));
            long currentStudents = projectStudentsDaoImpl.findAllCount(conditions);
            
            // 检查是否超过最大学生数
            if (currentStudents >= project.getPMaxStudents()) {
                return Result.error("课程人数已满");
            }
        }

        // 更新申请状态
        Integer result = projectApplicationsDaoImpl.updateStatus(paId, paStatus);
        
        // 如果审核通过，向选课结果表中插入一条记录
        if (result > 0 && "2".equals(paStatus)) {
            Integer assignmentResult = projectStudentsDaoImpl.insert(
                String.valueOf(application.getUId()), 
                String.valueOf(application.getPId())
            );
            if (assignmentResult <= 0) {
                return Result.error("审核通过但插入选课结果记录失败");
            }
        }
        
        if (result > 0) {
            // 获取课程信息，用于通知内容
            ProjectsPageDto project = projectsDaoImpl.find(String.valueOf(application.getPId()));
            String projectTitle = project != null ? project.getPTitle() : "未知课题";
            
            // 创建通知给学生
            NotificationsServiceImpl notificationsService = new NotificationsServiceImpl();
            String nTitle = "课题申请审核结果通知";
            String nContent;
            String nType = "2"; // 通知类型为2，表示课题申请相关
            
            if ("2".equals(paStatus)) {
                nContent = "您申请的课题【" + projectTitle + "】已审核通过";
            } else {
                nContent = "您申请的课题【" + projectTitle + "】未通过审核";
            }
            
            // 发送通知给学生
            notificationsService.insert(String.valueOf(application.getUId()), nTitle, nContent, nType);
            
            return Result.success(result);
        } else {
            return Result.error("审核失败");
        }
    }
}