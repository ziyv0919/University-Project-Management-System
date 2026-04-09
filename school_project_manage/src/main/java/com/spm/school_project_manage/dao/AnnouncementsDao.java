package com.spm.school_project_manage.dao;

import com.spm.school_project_manage.dto.AnnouncementsPageDto;
import java.util.List;
import java.util.Map;

public interface AnnouncementsDao {
    /**
     * 分页查询所有公告
     */
    List<AnnouncementsPageDto> findAll(int pageIndex, int pageSize, Map<String, Object> conditions);

    /**
     * 查询总记录数
     */
    long findAllCount(Map<String, Object> conditions);

    /**
     * 根据ID查询公告
     */
    AnnouncementsPageDto find(String aId);

    /**
     * 新增公告
     */
    Integer insert(String aTitle, String aContent, String uId, String aTarget);

    /**
     * 更新公告
     */
    Integer update(String aId, String aTitle, String aContent, String aTarget);

    /**
     * 删除公告
     */
    Integer delete(String aId);
}