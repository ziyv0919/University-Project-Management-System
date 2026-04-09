package com.spm.school_project_manage.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.spm.school_project_manage.dao.impl.ProjectsDaoImpl;
import com.spm.school_project_manage.dao.impl.UsersDaoImpl;
import com.spm.school_project_manage.dto.ProjectsPageDto;
import com.spm.school_project_manage.dto.UsersPageDto;
import com.spm.school_project_manage.service.ProjectsService;
import com.spm.school_project_manage.utils.PageResult;
import com.spm.school_project_manage.utils.Result;
import com.spm.school_project_manage.vo.ProjectsPageVo;
import com.spm.school_project_manage.vo.TagsPageVo;

public class ProjectsServiceImpl implements ProjectsService {

    ProjectsDaoImpl projectsDaoImpl = new ProjectsDaoImpl();
    ProjectTagsServiceImpl projectTagsServiceImpl = new ProjectTagsServiceImpl();
    UsersDaoImpl usersDaoImpl = new UsersDaoImpl();
    NotificationsServiceImpl notificationsServiceImpl = new NotificationsServiceImpl();

    @Override
    public Result<Integer> insert(String pTitle, String pDescription, String pDirection, Integer pDifficulty, Integer uId, Integer pMaxStudents, List<String> tagIds) {
        Integer result = projectsDaoImpl.insert(pTitle, pDescription, pDirection, pDifficulty, uId, pMaxStudents);
        if (result > 0) {
            // 如果有标签，则批量分配标签
            if (tagIds != null && !tagIds.isEmpty()) {
                projectTagsServiceImpl.batchAssignTags(result.toString(), tagIds);
            }
            
            // 给所有管理员发送通知
            Map<String, Object> conditions = new HashMap<>();
            conditions.put("uRole:=", "1"); // 查询所有管理员用户(uRole=1)
            List<UsersPageDto> adminUsers = usersDaoImpl.findAll(1, 100, conditions);
            
            if (adminUsers != null && !adminUsers.isEmpty()) {
                String nTitle = "新课题创建通知";
                String nContent = "有新的课题被创建：" + pTitle;
                String nType = "1"; // 通知类型为1
                
                for (UsersPageDto admin : adminUsers) {
                    notificationsServiceImpl.insert(String.valueOf(admin.getUId()), nTitle, nContent, nType);
                }
            }
            
            return Result.success(result);
        } else {
            return Result.error("新增课题失败");
        }
    }

    @Override
    public Result<PageResult<List<ProjectsPageVo>>> findAll(String pageIndex, String pageSize, String pId, String pTitle, String pDirection, String pDifficulty, String uId) {
        int pageIndexInt = Integer.parseInt(pageIndex);
        int pageSizeInt = Integer.parseInt(pageSize);

        Map<String, Object> conditions = new HashMap<>();
        if (pId != null && !pId.trim().isEmpty()) {
            conditions.put("p.pId:=", pId);
        }
        if (pTitle != null && !pTitle.trim().isEmpty()) {
            conditions.put("p.pTitle:LIKE", pTitle);
        }
        if (pDirection != null && !pDirection.trim().isEmpty()) {
            conditions.put("p.pDirection:LIKE", pDirection);
        }
        if (pDifficulty != null && !pDifficulty.trim().isEmpty()) {
            conditions.put("p.pDifficulty:=", Integer.parseInt(pDifficulty));
        }
        if (uId != null && !uId.trim().isEmpty()) {
            conditions.put("p.uId:=", Integer.parseInt(uId));
        }

        List<ProjectsPageDto> projectsPageDtos = projectsDaoImpl.findAll(pageIndexInt, pageSizeInt, conditions);
        long count = projectsDaoImpl.findAllCount(conditions);

        if (projectsPageDtos != null) {
            // 转换为Vo对象并补充标签
            PageResult pageResult = new PageResult(pageIndexInt, pageSizeInt, count, projectsPageDtos);
            Result<PageResult<List<ProjectsPageVo>>> result = Result.success(pageResult, ProjectsPageVo.class);
            // 为每个Vo设置tags
            if(result.getData() != null && result.getData().getList() != null) {
                for (Object obj : result.getData().getList()) {
    ProjectsPageVo vo = (ProjectsPageVo) obj;
                    Result<List<TagsPageVo>> tagResult = projectTagsServiceImpl.findTagsByProjectId(String.valueOf(vo.getPId()));
                    if(tagResult.getData() != null) {
                        vo.setTags(tagResult.getData());
                    }
                }
            }
            return result;
        } else {
            Result result = Result.error("数据库执行错误");
            return result;
        }
    }

    @Override
    public Result<ProjectsPageVo> find(String pId) {
        ProjectsPageDto projectsPageDto = projectsDaoImpl.find(pId);
        if (projectsPageDto == null) {
            return Result.error("课题不存在");
        }
        Result<ProjectsPageVo> result = Result.success(projectsPageDto, ProjectsPageVo.class);
        if(result.getData() != null) {
            Result<List<TagsPageVo>> tagResult = projectTagsServiceImpl.findTagsByProjectId(pId);
            if(tagResult.getData() != null) {
                result.getData().setTags(tagResult.getData());
            }
        }
        return result;
    }

    @Override
    public Result<Integer> update(String pId, String pTitle, String pDescription, String pDirection, Integer pDifficulty, Integer uId, Integer pMaxStudents, List<String> tagIds) {
        ProjectsPageDto projectsPageDto = projectsDaoImpl.find(pId);
        if (projectsPageDto == null) {
            return Result.error("课题不存在");
        }

        Integer result = projectsDaoImpl.update(pId, pTitle, pDescription, pDirection, pDifficulty, uId, pMaxStudents);
        if (result > 0) {
            // 如果有标签，则批量分配标签
            if (tagIds != null && !tagIds.isEmpty()) {
                projectTagsServiceImpl.removeAllTags(pId);
                projectTagsServiceImpl.batchAssignTags(pId, tagIds);
            }
            return Result.success(result);
        } else {
            return Result.error("修改课题失败");
        }
    }

    @Override
    public Result<Integer> delete(String pId) {
        ProjectsPageDto projectsPageDto = projectsDaoImpl.find(pId);
        if (projectsPageDto == null) {
            return Result.error("课题不存在");
        }

        Integer result = projectsDaoImpl.delete(pId);
        if (result > 0) {
            return Result.success(result);
        } else {
            return Result.error("删除失败");
        }
    }

    @Override
    public Result<List<ProjectsPageVo>> remoteSelect(String searchText) {
        Map<String, Object> conditions = new HashMap<>();

        if (searchText != null && !searchText.isEmpty()) {
            conditions.put("p.pTitle:LIKE", searchText);
        }

        List<ProjectsPageDto> projectsPageDtos = projectsDaoImpl.findAll(1, 30, conditions);

        if (projectsPageDtos != null) {
            return Result.success(projectsPageDtos, ProjectsPageVo.class);
        }

        return Result.error("查询失败");
    }

    @Override
    public Result<Integer> updateCover(String pId, String pCover) {
        ProjectsPageDto projectsPageDto = projectsDaoImpl.find(pId);
        if (projectsPageDto == null) {
            return Result.error("课题不存在");
        }

        Integer result = projectsDaoImpl.updateCover(pId, pCover);
        if (result > 0) {
            return Result.success(result);
        } else {
            return Result.error("修改课题失败");
        }
    }
}