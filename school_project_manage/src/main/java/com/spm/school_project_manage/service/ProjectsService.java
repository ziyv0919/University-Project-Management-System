package com.spm.school_project_manage.service;

import java.util.List;

import com.spm.school_project_manage.utils.PageResult;
import com.spm.school_project_manage.utils.Result;
import com.spm.school_project_manage.vo.ProjectsPageVo;

public interface ProjectsService {
    Result<Integer> insert(String pTitle, String pDescription, String pDirection, Integer pDifficulty, Integer uId, Integer pMaxStudents, List<String> tagIds);
    
    Result<PageResult<List<ProjectsPageVo>>> findAll(String pageIndex, String pageSize, String pId, String pTitle, String pDirection, String pDifficulty, String uId);
    
    Result<ProjectsPageVo> find(String pId);
    
    Result<Integer> update(String pId, String pTitle, String pDescription, String pDirection, Integer pDifficulty, Integer uId, Integer pMaxStudents, List<String> tagIds);
    
    Result<Integer> delete(String pId);
    
    Result<List<ProjectsPageVo>> remoteSelect(String searchText);

    Result<Integer> updateCover(String pId, String pCover);
}