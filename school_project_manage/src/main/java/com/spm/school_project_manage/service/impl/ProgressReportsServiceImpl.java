package com.spm.school_project_manage.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.spm.school_project_manage.dao.impl.ProgressReportsDaoImpl;
import com.spm.school_project_manage.dao.impl.ProjectStudentsDaoImpl;
import com.spm.school_project_manage.dao.impl.ProjectsDaoImpl;
import com.spm.school_project_manage.dto.ProgressReportsPageDto;
import com.spm.school_project_manage.dto.ProjectStudentsPageDto;
import com.spm.school_project_manage.dto.ProjectsPageDto;
import com.spm.school_project_manage.service.ProgressReportsService;
import com.spm.school_project_manage.utils.PageResult;
import com.spm.school_project_manage.utils.Result;
import com.spm.school_project_manage.vo.ProgressReportsPageVo;

public class ProgressReportsServiceImpl implements ProgressReportsService {

    ProgressReportsDaoImpl progressReportsDaoImpl = new ProgressReportsDaoImpl();

    ProjectStudentsDaoImpl projectStudentsDaoImpl = new ProjectStudentsDaoImpl();

    ProjectsDaoImpl projectsDaoImpl = new ProjectsDaoImpl();

    @Override
    public Result<PageResult<List<ProgressReportsPageVo>>> findAll(String pageIndex, String pageSize, String prId, String psId, String prTitle, String uName, String pTitle, String pId) {
        int pageIndexInt = Integer.parseInt(pageIndex);
        int pageSizeInt = Integer.parseInt(pageSize);

        Map<String, Object> conditions = new HashMap<>();
        if (prId != null && !prId.trim().isEmpty()) {
            conditions.put("pr.prId:=", prId);
        }
        if (psId != null && !psId.trim().isEmpty()) {
            conditions.put("pr.psId:=", psId);
        }
        if (prTitle != null && !prTitle.trim().isEmpty()) {
            conditions.put("pr.prTitle:LIKE", prTitle);
        }
        if (uName != null && !uName.trim().isEmpty()) {
            conditions.put("u.uName:LIKE", uName);
        }
        if (pTitle != null && !pTitle.trim().isEmpty()) {
            conditions.put("p.pTitle:LIKE", pTitle);
        }
        if (pId != null && !pId.trim().isEmpty()) {
            conditions.put("ps.pId:=", pId);
        }

        List<ProgressReportsPageDto> progressReportsPageDtos = progressReportsDaoImpl.findAll(pageIndexInt, pageSizeInt, conditions);
        long count = progressReportsDaoImpl.findAllCount(conditions);

        if (progressReportsPageDtos != null) {
            PageResult pageResult = new PageResult(pageIndexInt, pageSizeInt, count, progressReportsPageDtos);
            Result<PageResult<List<ProgressReportsPageVo>>> result = Result.success(pageResult, ProgressReportsPageVo.class);
            return result;
        } else {
            Result result = Result.error("数据库执行错误");
            return result;
        }
    }

    @Override
    public Result<ProgressReportsPageVo> find(String prId) {
        ProgressReportsPageDto progressReportsPageDto = progressReportsDaoImpl.find(prId);
        if (progressReportsPageDto == null) {
            return Result.error("课程阶段性进展不存在");
        }
        return Result.success(progressReportsPageDto, ProgressReportsPageVo.class);
    }

    @Override
    public Result<Integer> insert(String psId, String prTitle, String prContent) {
        Integer result = progressReportsDaoImpl.insert(psId, prTitle, prContent);
        if (result > 0) {
            ProjectStudentsPageDto projectStudentsPageDto = projectStudentsDaoImpl.find(psId);

            // 获取项目ID和学生姓名
            String pId = String.valueOf(projectStudentsPageDto.getPId());
            String studentName = projectStudentsPageDto.getuName();

            // 获取项目信息，找到教师ID
            ProjectsDaoImpl projectsDaoImpl = new ProjectsDaoImpl();
            ProjectsPageDto project = projectsDaoImpl.find(pId);
            if (project != null) {
                // 获取教师ID
                String teacherId = String.valueOf(project.getUId());

                // 发送通知给教师
                NotificationsServiceImpl notificationsServiceImpl = new NotificationsServiceImpl();
                String notificationTitle = "学生提交了新的阶段性进展报告";
                String notificationContent = "学生" + studentName + "提交了新的阶段性进展报告：" + prTitle;
                notificationsServiceImpl.insert(teacherId, notificationTitle, notificationContent, "3");
            }
            return Result.success(result);
        } else {
            return Result.error("新增失败");
        }
    }

    @Override
    public Result<Integer> update(String prId, String prTitle, String prContent) {
        // 检查记录是否存在
        ProgressReportsPageDto progressReportsPageDto = progressReportsDaoImpl.find(prId);
        if (progressReportsPageDto == null) {
            return Result.error("课程阶段性进展不存在");
        }

        Integer result = progressReportsDaoImpl.update(prId, prTitle, prContent);
        if (result > 0) {
            return Result.success(result);
        } else {
            return Result.error("更新失败");
        }
    }

    @Override
    public Result<Integer> evaluate(String prId, String prTeacherComment, String prScore) {
        // 检查记录是否存在
        ProgressReportsPageDto progressReportsPageDto = progressReportsDaoImpl.find(prId);
        if (progressReportsPageDto == null) {
            return Result.error("课程阶段性进展不存在");
        }

        Integer result = progressReportsDaoImpl.evaluate(prId, prTeacherComment, Integer.parseInt(prScore));
        if (result > 0) {
            // 获取学生ID和项目信息
            String psId = String.valueOf(progressReportsPageDto.getPsId());
            ProjectStudentsPageDto projectStudentsPageDto = projectStudentsDaoImpl.find(psId);
            
            if (projectStudentsPageDto != null) {
                // 获取学生ID和项目标题
                String studentId = String.valueOf(projectStudentsPageDto.getUId());
                String projectTitle = projectStudentsPageDto.getpTitle();
                String reportTitle = progressReportsPageDto.getPrTitle();
                
                // 发送通知给学生
                NotificationsServiceImpl notificationsServiceImpl = new NotificationsServiceImpl();
                String notificationTitle = "您的阶段性进展报告已被评价";
                String notificationContent = "您在《" + projectTitle + "》课题中提交的阶段性进展《" + reportTitle + "》得到了老师的评价";
                notificationsServiceImpl.insert(studentId, notificationTitle, notificationContent, "3");
            }
            
            return Result.success(result);
        } else {
            return Result.error("评价失败");
        }
    }

    @Override
    public Result<Integer> delete(String prId) {
        // 检查记录是否存在
        ProgressReportsPageDto progressReportsPageDto = progressReportsDaoImpl.find(prId);
        if (progressReportsPageDto == null) {
            return Result.error("课程阶段性进展不存在");
        }

        Integer result = progressReportsDaoImpl.delete(prId);
        if (result > 0) {
            ProjectStudentsPageDto projectStudentsPageDto = projectStudentsDaoImpl.find(String.valueOf(progressReportsPageDto.getPsId()));
            if (projectStudentsPageDto != null) {
                // 获取教师ID
                ProjectsPageDto projectsPageDto = projectsDaoImpl.find(String.valueOf(projectStudentsPageDto.getPId()));
                String teacherId = String.valueOf(projectsPageDto.getUId());
                String studentName = projectStudentsPageDto.getuName();
                String reportTitle = progressReportsPageDto.getPrTitle();
                
                // 发送通知给教师
                NotificationsServiceImpl notificationsServiceImpl = new NotificationsServiceImpl();
                String notificationTitle = "学生删除了阶段性进展报告";
                String notificationContent = "学生" + studentName + "删除了阶段性进展报告：" + reportTitle;
                notificationsServiceImpl.insert(teacherId, notificationTitle, notificationContent, "3");
            }
            return Result.success(result);
        } else {
            return Result.error("删除失败");
        }
    }
    
    @Override
    public Result<Integer> batchInsert(List<Map<String, String>> reportsList) {
        if (reportsList == null || reportsList.isEmpty()) {
            return Result.error("批量插入数据为空");
        }
        
        List<Integer> results = new ArrayList<>();
        int successCount = 0;
        
        for (Map<String, String> report : reportsList) {
            // 确保获取的值是String类型
            String psId = String.valueOf(report.get("psId"));
            String prTitle = String.valueOf(report.get("prTitle"));
            String prContent = String.valueOf(report.get("prContent"));
            
            Integer insertResult = progressReportsDaoImpl.insert(psId, prTitle, prContent);
            results.add(insertResult);
            
            if (insertResult > 0) {
                successCount++;
                
                // 获取进展报告相关信息，用于通知教师
                ProgressReportsPageDto progressReport = progressReportsDaoImpl.find(String.valueOf(insertResult));
                if (progressReport != null) {
                    // 获取项目ID和学生姓名
                    String pId = String.valueOf(progressReport.getpId());
                    String studentName = progressReport.getuName();
                    
                    // 获取项目信息，找到教师ID
                    ProjectsDaoImpl projectsDaoImpl = new ProjectsDaoImpl();
                    ProjectsPageDto project = projectsDaoImpl.find(pId);
                    if (project != null) {
                        // 获取教师ID
                        String teacherId = String.valueOf(project.getUId());
                        
                        // 发送通知给教师
                        NotificationsServiceImpl notificationsServiceImpl = new NotificationsServiceImpl();
                        String notificationTitle = "学生提交了新的阶段性进展报告";
                        String notificationContent = "学生" + studentName + "提交了新的阶段性进展报告：" + prTitle;
                        notificationsServiceImpl.insert(teacherId, notificationTitle, notificationContent, "3");
                    }
                }
            }
        }
        
        if (successCount == reportsList.size()) {
            return Result.success(successCount);
        } else if (successCount > 0) {
            return Result.success(successCount);
        } else {
            return Result.error("批量插入失败");
        }
    }
}