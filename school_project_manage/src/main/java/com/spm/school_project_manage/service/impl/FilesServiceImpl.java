package com.spm.school_project_manage.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.spm.school_project_manage.dao.impl.FilesDaoImpl;
import com.spm.school_project_manage.dao.impl.ProjectStudentsDaoImpl;
import com.spm.school_project_manage.dao.impl.ProjectsDaoImpl;
import com.spm.school_project_manage.dao.impl.UsersDaoImpl;
import com.spm.school_project_manage.dto.FilesPageDto;
import com.spm.school_project_manage.dto.ProjectStudentsPageDto;
import com.spm.school_project_manage.dto.ProjectsPageDto;
import com.spm.school_project_manage.dto.UsersPageDto;
import com.spm.school_project_manage.service.FilesService;
import com.spm.school_project_manage.utils.PageResult;
import com.spm.school_project_manage.utils.Result;
import com.spm.school_project_manage.vo.FilesPageVo;

public class FilesServiceImpl implements FilesService {

    FilesDaoImpl filesDaoImpl = new FilesDaoImpl();
    ProjectsDaoImpl projectsDaoImpl = new ProjectsDaoImpl();
    ProjectStudentsDaoImpl projectStudentsDaoImpl = new ProjectStudentsDaoImpl();
    UsersDaoImpl usersDaoImpl = new UsersDaoImpl();
    NotificationsServiceImpl notificationsServiceImpl = new NotificationsServiceImpl();

    @Override
    public Result<Integer> batchInsert(List<Map<String, Object>> filesList) {
        if (filesList == null || filesList.isEmpty()) {
            return Result.error("文件列表不能为空");
        }

        Integer result = filesDaoImpl.batchInsert(filesList);
        if (result > 0) {
            // 获取课题ID，假设所有文件都属于同一个课题
            String pId = filesList.get(0).get("pId").toString();
            
            // 获取课题信息
            ProjectsPageDto project = projectsDaoImpl.find(pId);
            if (project != null) {
                String projectTitle = project.getPTitle();
                
                // 1. 通知选课学生
                Map<String, Object> studentConditions = new HashMap<>();
                studentConditions.put("ps.pId:=", pId);
                List<ProjectStudentsPageDto> students = projectStudentsDaoImpl.findAll(1, 100, studentConditions);
                
                if (students != null && !students.isEmpty()) {
                    String studentNotificationTitle = "课题资料更新通知";
                    String studentNotificationContent = "您选择的课题《" + projectTitle + "》有新的资料上传";
                    String notificationType = "4"; // 假设4表示资料更新通知
                    
                    for (ProjectStudentsPageDto student : students) {
                        notificationsServiceImpl.insert(String.valueOf(student.getUId()), 
                                                       studentNotificationTitle, 
                                                       studentNotificationContent, 
                                                       notificationType);
                    }
                }
                
                // 2. 通知课题对应教师
                String teacherId = String.valueOf(project.getUId());
                String teacherNotificationTitle = "课题资料更新通知";
                String teacherNotificationContent = "您的课题《" + projectTitle + "》有新的资料上传";
                notificationsServiceImpl.insert(teacherId, 
                                               teacherNotificationTitle, 
                                               teacherNotificationContent, 
                                               "4");
                
                // 3. 通知所有管理员
                Map<String, Object> adminConditions = new HashMap<>();
                adminConditions.put("uRole:=", "1"); // 查询所有管理员用户(uRole=1)
                List<UsersPageDto> adminUsers = usersDaoImpl.findAll(1, 100, adminConditions);
                
                if (adminUsers != null && !adminUsers.isEmpty()) {
                    String adminNotificationTitle = "课题资料更新通知";
                    String adminNotificationContent = "课题《" + projectTitle + "》有新的资料上传";
                    
                    for (UsersPageDto admin : adminUsers) {
                        notificationsServiceImpl.insert(String.valueOf(admin.getUId()), 
                                                       adminNotificationTitle, 
                                                       adminNotificationContent, 
                                                       "4");
                    }
                }
            }
            
            return Result.success(result);
        } else {
            return Result.error("批量上传文件失败");
        }
    }

    @Override
    public Result<Integer> delete(String fId) {
        FilesPageDto filesPageDto = filesDaoImpl.find(fId);
        if (filesPageDto == null) {
            return Result.error("文件不存在");
        }

        Integer result = filesDaoImpl.delete(fId);
        if (result > 0) {
            return Result.success(result);
        } else {
            return Result.error("删除文件失败");
        }
    }

    @Override
    public Result<Integer> batchDelete(List<String> fIds) {
        if (fIds == null || fIds.isEmpty()) {
            return Result.error("文件ID列表不能为空");
        }

        Integer result = filesDaoImpl.batchDelete(fIds);
        if (result > 0) {
            return Result.success(result);
        } else {
            return Result.error("批量删除文件失败");
        }
    }

    @Override
    public Result<List<FilesPageVo>> findByProjectId(String pId) {
        List<FilesPageDto> filesPageDtos = filesDaoImpl.findByProjectId(pId);
        if (filesPageDtos != null) {
            return Result.success(filesPageDtos, FilesPageVo.class);
        } else {
            return Result.error("查询文件失败");
        }
    }

    @Override
    public Result<PageResult<List<FilesPageVo>>> findAll(String pageIndex, String pageSize, String fId, String pId, String fOriginalName) {
        int pageIndexInt = Integer.parseInt(pageIndex);
        int pageSizeInt = Integer.parseInt(pageSize);

        Map<String, Object> conditions = new HashMap<>();
        if (fId != null && !fId.trim().isEmpty()) {
            conditions.put("f.fId:=", fId);
        }
        if (pId != null && !pId.trim().isEmpty()) {
            conditions.put("f.pId:=", pId);
        }
        if (fOriginalName != null && !fOriginalName.trim().isEmpty()) {
            conditions.put("f.fOriginalName:LIKE", fOriginalName);
        }

        List<FilesPageDto> filesPageDtos = filesDaoImpl.findAll(pageIndexInt, pageSizeInt, conditions);
        long count = filesDaoImpl.findAllCount(conditions);

        if (filesPageDtos != null) {
            PageResult pageResult = new PageResult(pageIndexInt, pageSizeInt, count, filesPageDtos);
            Result<PageResult<List<FilesPageVo>>> result = Result.success(pageResult, FilesPageVo.class);
            return result;
        } else {
            Result result = Result.error("数据库执行错误");
            return result;
        }
    }

    @Override
    public Result<FilesPageVo> find(String fId) {
        FilesPageDto filesPageDto = filesDaoImpl.find(fId);
        if (filesPageDto == null) {
            return Result.error("文件不存在");
        }
        return Result.success(filesPageDto, FilesPageVo.class);
    }
}