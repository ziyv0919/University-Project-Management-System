package com.spm.school_project_manage.service;

import com.spm.school_project_manage.utils.PageResult;
import com.spm.school_project_manage.utils.Result;
import com.spm.school_project_manage.vo.AnnouncementsPageVo;
import java.util.List;

public interface AnnouncementsService {
    /**
     * 分页查询所有公告
     */
    Result<PageResult<List<AnnouncementsPageVo>>> findAll(String pageIndex, String pageSize, String aId, String aTitle, String uId, String aTarget, String uName);

    /**
     * 根据ID查询公告
     */
    Result<AnnouncementsPageVo> find(String aId);

    /**
     * 新增公告
     */
    Result<Integer> insert(String aTitle, String aContent, String uId, String aTarget);

    /**
     * 更新公告
     */
    Result<Integer> update(String aId, String aTitle, String aContent, String aTarget);

    /**
     * 删除公告
     */
    Result<Integer> delete(String aId);
}