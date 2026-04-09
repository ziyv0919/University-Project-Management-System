package com.spm.school_project_manage.utils;

import java.util.List;

public class PageResult<T> {
    // 当前页码
    private int pageIndex;

    // 每页大小
    private int pageSize;

    // 总记录数
    private long totalRecords;

    // 总页数
    private int totalPages;

    // 当前页数据
    private List<T> list;

    // 无参构造方法
    public PageResult() {}

    // 全参构造方法
    public PageResult(int pageIndex, int pageSize, long totalRecords, List<T> list) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.totalRecords = totalRecords;
        this.list = list;
        this.totalPages = (int) Math.ceil((double) totalRecords / pageSize);
    }

    // Getter 和 Setter 方法
    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(long totalRecords) {
        this.totalRecords = totalRecords;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> data) {
        this.list = data;
    }

    // 静态工厂方法
    public static <T> PageResult<T> of(int pageIndex, int pageSize, long totalRecords, List<T> data) {
        return new PageResult<>(pageIndex, pageSize, totalRecords, data);
    }

    @Override
    public String toString() {
        return "PageResult{" +
                "pageIndex=" + pageIndex +
                ", pageSize=" + pageSize +
                ", totalRecords=" + totalRecords +
                ", totalPages=" + totalPages +
                ", list=" + list +
                '}';
    }
}

