package com.txy.blog.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.txy.blog.admin.model.params.ArticleParam;
import com.txy.blog.admin.model.params.PageParam;
import com.txy.blog.admin.service.AdminArticleService;
import com.txy.blog.admin.vo.Result;
import com.txy.blog.dao.mapper.ArticleMapper;
import com.txy.blog.dao.pojo.Article;
import com.txy.blog.service.ArticleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin/article")
public class AdminArticleController {
    @Autowired
    private AdminArticleService articleService;
    @PostMapping("articleList")
    public Result articleList(@RequestBody PageParam pageParam) {
        return articleService.articleList(pageParam);
    }

    @PostMapping("update")
    public Result update(@RequestBody ArticleParam articleParam) {
        return articleService.update(articleParam);
    }

    @PostMapping("delete/{id}")
    public Result delete(@PathVariable Long id) {
        return articleService.delete(id);
    }

}
