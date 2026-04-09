package com.spm.school_project_manage.dao;

import java.util.List;
import java.util.Map;

import com.spm.school_project_manage.dto.ProjectsPageDto;

public interface ProjectsDao {
    Integer insert(String pTitle, String pDescription, String pDirection, Integer pDifficulty, Integer uId, Integer pMaxStudents);
    
    List<ProjectsPageDto> findAll(int pageIndex, int pageSize, Map<String, Object> conditions);
    
    long findAllCount(Map<String, Object> conditions);
    
    ProjectsPageDto find(String pId);
    
    Integer update(String pId, String pTitle, String pDescription, String pDirection, Integer pDifficulty, Integer uId, Integer pMaxStudents);
    
    Integer delete(String pId);

    Integer updateCover(String pId, String pCover);
}