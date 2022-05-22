package com.txy.blog.service;

import com.txy.blog.vo.CategoryVo;
import com.txy.blog.vo.Result;

import java.util.List;

public interface CategoryService {
    CategoryVo findCategoryById(Long categoryId);

    Result findAll();

    Result findAllDetails();

    Result categoryDetailById(Long categoryId);
}
