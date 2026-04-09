package com.spm.school_project_manage.service.impl;

import com.spm.school_project_manage.dao.impl.TagsDaoImpl;
import com.spm.school_project_manage.dto.TagsPageDto;
import com.spm.school_project_manage.service.TagsService;
import com.spm.school_project_manage.utils.PageResult;
import com.spm.school_project_manage.utils.Result;
import com.spm.school_project_manage.vo.TagsPageVo;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TagsServiceImpl implements TagsService {

    TagsDaoImpl tagsDaoImpl = new TagsDaoImpl();

    @Override
    public Result<Integer> insert(String tName) {
        // 检查标签名是否已存在
        TagsPageDto tagsPageDto = tagsDaoImpl.findByName(tName);
        if (tagsPageDto != null) {
            return Result.error("标签名已存在");
        }

        Integer result = tagsDaoImpl.insert(tName);
        if (result > 0) {
            return Result.success(result);
        } else {
            return Result.error("新增标签失败");
        }
    }

    @Override
    public Result<PageResult<List<TagsPageVo>>> findAll(String pageIndex, String pageSize, String tId, String tName) {
        int pageIndexInt = Integer.parseInt(pageIndex);
        int pageSizeInt = Integer.parseInt(pageSize);

        Map<String, Object> conditions = new HashMap<>();
        if (tId != null && !tId.trim().isEmpty()) {
            conditions.put("tId:=", tId);
        }
        if (tName != null && !tName.trim().isEmpty()) {
            conditions.put("tName:LIKE", tName);
        }

        List<TagsPageDto> tagsPageDtos = tagsDaoImpl.findAll(pageIndexInt, pageSizeInt, conditions);
        long count = tagsDaoImpl.findAllCount(conditions);

        if (tagsPageDtos != null) {
            PageResult pageResult = new PageResult(pageIndexInt, pageSizeInt, count, tagsPageDtos);
            Result<PageResult<List<TagsPageVo>>> result = Result.success(pageResult, TagsPageVo.class);
            return result;
        } else {
            Result result = Result.error("数据库执行错误");
            return result;
        }
    }

    @Override
    public Result<TagsPageVo> find(String tId) {
        TagsPageDto tagsPageDto = tagsDaoImpl.find(tId);
        if (tagsPageDto == null) {
            return Result.error("标签不存在");
        }
        return Result.success(tagsPageDto, TagsPageVo.class);
    }

    @Override
    public Result<Integer> update(String tId, String tName) {
        // 检查标签是否存在
        TagsPageDto tagsPageDto = tagsDaoImpl.find(tId);
        if (tagsPageDto == null) {
            return Result.error("标签不存在");
        }

        // 检查新标签名是否与其他标签重复
        TagsPageDto existingTag = tagsDaoImpl.findByName(tName);
        if (existingTag != null && existingTag.getTId() != Long.parseLong(tId)) {
            return Result.error("标签名已存在");
        }

        Integer result = tagsDaoImpl.update(tId, tName);
        if (result > 0) {
            return Result.success(result);
        } else {
            return Result.error("修改标签失败");
        }
    }

    @Override
    public Result<Integer> delete(String tId) {
        TagsPageDto tagsPageDto = tagsDaoImpl.find(tId);
        if (tagsPageDto == null) {
            return Result.error("标签不存在");
        }

        Integer result = tagsDaoImpl.delete(tId);
        if (result > 0) {
            return Result.success(result);
        } else {
            return Result.error("删除失败");
        }
    }

    @Override
    public Result<List<TagsPageVo>> remoteSelect(String searchText) {
        Map<String, Object> conditions = new HashMap<>();

        if (searchText != null && !searchText.isEmpty()) {
            conditions.put("tName:LIKE", searchText);
        }

        List<TagsPageDto> tagsPageDtos = tagsDaoImpl.findAll(1, 30, conditions);

        if (tagsPageDtos != null) {
            return Result.success(tagsPageDtos, TagsPageVo.class);
        }

        return Result.error("查询失败");
    }

    @Override
    public Result<List<TagsPageVo>> select() {
        List<TagsPageDto> tagsPageDtos = tagsDaoImpl.select();
        return Result.success(tagsPageDtos, TagsPageVo.class);
    }
}