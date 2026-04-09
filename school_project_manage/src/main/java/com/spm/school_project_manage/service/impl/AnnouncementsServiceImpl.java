package com.spm.school_project_manage.service.impl;

import com.spm.school_project_manage.dao.impl.AnnouncementsDaoImpl;
import com.spm.school_project_manage.dto.AnnouncementsPageDto;
import com.spm.school_project_manage.service.AnnouncementsService;
import com.spm.school_project_manage.utils.PageResult;
import com.spm.school_project_manage.utils.Result;
import com.spm.school_project_manage.vo.AnnouncementsPageVo;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnnouncementsServiceImpl implements AnnouncementsService {

    AnnouncementsDaoImpl announcementsDaoImpl = new AnnouncementsDaoImpl();

    @Override
    public Result<PageResult<List<AnnouncementsPageVo>>> findAll(String pageIndex, String pageSize, String aId, String aTitle, String uId, String aTarget, String uName) {
        int pageIndexInt = Integer.parseInt(pageIndex);
        int pageSizeInt = Integer.parseInt(pageSize);

        Map<String, Object> conditions = new HashMap<>();
        if (aId != null && !aId.trim().isEmpty()) {
            conditions.put("a.aId:=", aId);
        }
        if (aTitle != null && !aTitle.trim().isEmpty()) {
            conditions.put("a.aTitle:LIKE", aTitle);
        }
        if (uId != null && !uId.trim().isEmpty()) {
            conditions.put("a.uId:=", uId);
        }
        if (aTarget != null && !aTarget.trim().isEmpty()) {
            conditions.put("a.aTarget:=", aTarget);
        }
        if (uName != null && !uName.trim().isEmpty()) {
            conditions.put("u.uName:LIKE", uName);
        }

        List<AnnouncementsPageDto> announcementsPageDtos = announcementsDaoImpl.findAll(pageIndexInt, pageSizeInt, conditions);
        long count = announcementsDaoImpl.findAllCount(conditions);

        if (announcementsPageDtos != null) {
            PageResult pageResult = new PageResult(pageIndexInt, pageSizeInt, count, announcementsPageDtos);
            Result<PageResult<List<AnnouncementsPageVo>>> result = Result.success(pageResult, AnnouncementsPageVo.class);
            return result;
        } else {
            Result result = Result.error("数据库执行错误");
            return result;
        }
    }

    @Override
    public Result<AnnouncementsPageVo> find(String aId) {
        AnnouncementsPageDto announcementsPageDto = announcementsDaoImpl.find(aId);
        if (announcementsPageDto == null) {
            return Result.error("公告不存在");
        }
        return Result.success(announcementsPageDto, AnnouncementsPageVo.class);
    }

    @Override
    public Result<Integer> insert(String aTitle, String aContent, String uId, String aTarget) {
        Integer result = announcementsDaoImpl.insert(aTitle, aContent, uId, aTarget);
        if (result > 0) {
            return Result.success(result);
        } else {
            return Result.error("新增失败");
        }
    }

    @Override
    public Result<Integer> update(String aId, String aTitle, String aContent, String aTarget) {
        // 检查记录是否存在
        AnnouncementsPageDto announcementsPageDto = announcementsDaoImpl.find(aId);
        if (announcementsPageDto == null) {
            return Result.error("公告不存在");
        }

        Integer result = announcementsDaoImpl.update(aId, aTitle, aContent, aTarget);
        if (result > 0) {
            return Result.success(result);
        } else {
            return Result.error("更新失败");
        }
    }

    @Override
    public Result<Integer> delete(String aId) {
        // 检查记录是否存在
        AnnouncementsPageDto announcementsPageDto = announcementsDaoImpl.find(aId);
        if (announcementsPageDto == null) {
            return Result.error("公告不存在");
        }

        Integer result = announcementsDaoImpl.delete(aId);
        if (result > 0) {
            return Result.success(result);
        } else {
            return Result.error("删除失败");
        }
    }
}