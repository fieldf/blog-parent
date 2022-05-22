package com.txy.blog.service;

import com.txy.blog.vo.Result;
import com.txy.blog.vo.TagVo;

import java.util.List;

public interface TagService {
    List<TagVo> findTagsByArticleId(Long articleId);

    Result hots(int limit);

    /**
     * 查询所有的文章标签
     * @return
     */
    Result findAll();

    Result findAllDetails();

    Result findDetailById(Long id);
}
