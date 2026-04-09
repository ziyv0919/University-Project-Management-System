package com.spm.school_project_manage.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.spm.school_project_manage.dao.impl.ProjectTagsDaoImpl;
import com.spm.school_project_manage.dao.impl.ProjectsDaoImpl;
import com.spm.school_project_manage.dao.impl.TagsDaoImpl;
import com.spm.school_project_manage.dto.ProjectTagsPageDto;
import com.spm.school_project_manage.dto.ProjectsPageDto;
import com.spm.school_project_manage.dto.TagsPageDto;
import com.spm.school_project_manage.service.ProjectTagsService;
import com.spm.school_project_manage.utils.PageResult;
import com.spm.school_project_manage.utils.Result;
import com.spm.school_project_manage.vo.ProjectsPageVo;
import com.spm.school_project_manage.vo.TagsPageVo;

public class ProjectTagsServiceImpl implements ProjectTagsService {

    ProjectTagsDaoImpl projectTagsDaoImpl = new ProjectTagsDaoImpl();
    ProjectsDaoImpl projectsDaoImpl = new ProjectsDaoImpl();
    TagsDaoImpl tagsDaoImpl = new TagsDaoImpl();

    @Override
    public Result<Integer> assignTag(String pId, String tId) {
        // 检查课题是否存在
        ProjectsPageDto project = projectsDaoImpl.find(pId);
        if (project == null) {
            return Result.error("课题不存在");
        }

        // 检查标签是否存在
        TagsPageDto tag = tagsDaoImpl.find(tId);
        if (tag == null) {
            return Result.error("标签不存在");
        }

        // 检查是否已经分配过该标签
        ProjectTagsPageDto existingRelation = projectTagsDaoImpl.findByProjectAndTag(pId, tId);
        if (existingRelation != null) {
            return Result.error("该课题已分配此标签");
        }

        // 分配标签
        Integer result = projectTagsDaoImpl.insert(pId, tId);
        if (result > 0) {
            return Result.success(result);
        } else {
            return Result.error("分配标签失败");
        }
    }

    @Override
    public Result<Integer> batchAssignTags(String pId, List<String> tagIds) {
        // 检查课题是否存在
        ProjectsPageDto project = projectsDaoImpl.find(pId);
        if (project == null) {
            return Result.error("课题不存在");
        }

        // 检查标签列表是否为空
        if (tagIds == null || tagIds.isEmpty()) {
            return Result.error("标签列表不能为空");
        }
        
        // 过滤掉已经分配的标签和不存在的标签
        List<String> newTagIds = new ArrayList<>();
        List<String> invalidTagIds = new ArrayList<>();
        
        for (String tId : tagIds) {
            // 检查标签是否存在
            TagsPageDto tag = tagsDaoImpl.find(tId);
            if (tag == null) {
                invalidTagIds.add(tId);
                continue;
            }
            
            // 检查是否已经分配过该标签
            ProjectTagsPageDto existingRelation = projectTagsDaoImpl.findByProjectAndTag(pId, tId);
            if (existingRelation == null) {
                newTagIds.add(tId);
            }
        }
        
        // 如果有无效标签，返回错误信息
        if (!invalidTagIds.isEmpty()) {
            return Result.error("以下标签ID不存在: " + String.join(", ", invalidTagIds));
        }
        
        // 如果所有标签都已分配，返回提示信息
        if (newTagIds.isEmpty()) {
            return Result.error("所有标签已分配给该课题");
        }

        // 批量分配标签
        Integer result = projectTagsDaoImpl.batchInsert(pId, newTagIds);
        if (result > 0) {
            return Result.success(result);
        } else {
            return Result.error("分配标签失败");
        }
    }

    @Override
    public Result<Integer> removeTag(String ptId) {
        // 删除标签关联
        Integer result = projectTagsDaoImpl.delete(ptId);
        if (result > 0) {
            return Result.success(result);
        } else {
            return Result.error("删除标签关联失败");
        }
    }

    @Override
    public Result<Integer> removeAllTags(String pId) {
        // 检查课题是否存在
        ProjectsPageDto project = projectsDaoImpl.find(pId);
        if (project == null) {
            return Result.error("课题不存在");
        }

        // 删除课题的所有标签
        Integer result = projectTagsDaoImpl.deleteByProjectId(pId);
        if (result > 0) {
            return Result.success(result);
        } else {
            return Result.error("删除标签关联失败");
        }
    }

    @Override
    public Result<List<TagsPageVo>> findTagsByProjectId(String pId) {
        // 检查课题是否存在
        ProjectsPageDto project = projectsDaoImpl.find(pId);
        if (project == null) {
            return Result.error("课题不存在");
        }

        // 查询课题的标签
        List<ProjectTagsPageDto> projectTags = projectTagsDaoImpl.findByProjectId(pId);
        
        // 获取标签信息
        List<TagsPageDto> tagsList = new ArrayList<>();
        for (ProjectTagsPageDto projectTag : projectTags) {
            TagsPageDto tag = tagsDaoImpl.find(String.valueOf(projectTag.getTId()));
            if (tag != null) {
                tagsList.add(tag);
            }
        }

        if (tagsList.isEmpty()) {
            return Result.success(new ArrayList<>(), TagsPageVo.class);
        }

        return Result.success(tagsList, TagsPageVo.class);
    }

    @Override
    public Result<PageResult<List<ProjectsPageVo>>> findProjectsByTagId(String tId, String pageIndex, String pageSize) {
        // 检查标签是否存在
        TagsPageDto tag = tagsDaoImpl.find(tId);
        if (tag == null) {
            return Result.error("标签不存在");
        }

        int pageIndexInt = Integer.parseInt(pageIndex);
        int pageSizeInt = Integer.parseInt(pageSize);

        // 查询标签关联的课题
        List<ProjectsPageDto> projects = projectTagsDaoImpl.findProjectsByTagId(tId, pageIndexInt, pageSizeInt);
        long count = projectTagsDaoImpl.findProjectsCountByTagId(tId);

        if (projects != null) {
            PageResult pageResult = new PageResult(pageIndexInt, pageSizeInt, count, projects);
            Result<PageResult<List<ProjectsPageVo>>> result = Result.success(pageResult, ProjectsPageVo.class);
            return result;
        } else {
            Result result = Result.error("数据库执行错误");
            return result;
        }
    }
}