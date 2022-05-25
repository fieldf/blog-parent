package com.txy.blog.controller;

import com.txy.blog.common.aop.LogAnnotation;
import com.txy.blog.common.cache.Cache;
import com.txy.blog.service.ArticleService;
import com.txy.blog.vo.Result;
import com.txy.blog.vo.params.ArticleParam;
import com.txy.blog.vo.params.PageParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
// json数据交互
@RequestMapping("articles")
public class ArticleController {
    @Autowired
    private ArticleService articleService;
    /**
     * 首页文章列表
     * @param pageParams
     * @return
     */
    @PostMapping
    // 加上此注解，代表要对此接口记录日志
    @LogAnnotation(module="文章",operator="获取文章列表")
    @Cache(expire = 5*60*1000,name="listArticle")
    public Result listArticle(@RequestBody PageParams pageParams) {
        // test branch dev
        return articleService.listArticle(pageParams);
    }

    /**
     * 首页最热文章
     * @return
     */
    @PostMapping("hot")
    @Cache(expire = 5*60*1000,name="hot_article")
    public Result hotArticle() {
        int limit=5;
        return articleService.hotArticle(limit);
    }

    /**
     * 首页最新文章
     * @return
     */
    @PostMapping("new")
    @Cache(expire = 5*60*1000,name="news_article")
    public Result newArticles() {
        int limit=5;
        return articleService.newArticles(limit);
    }

    /**
     * 文章归档
     * @return
     */
    @PostMapping("listArchives")
    public Result listArchives() {
        return articleService.listArchives();
    }

    /**
     * 文章详情
     * @param articleId
     * @return
     */
    @PostMapping("view/{id}")
    public Result findArticleById(@PathVariable("id") Long articleId) {
        return articleService.findArticleById(articleId);
    }

    /**
     * 编辑文章显示详情
     * @param articleId
     * @return
     */
    @PostMapping("/{id}")
    public Result findArticleEditById(@PathVariable("id") Long articleId) {
        return articleService.findArticleEditById(articleId);
    }

//    接口url：/articles/publish

    /**
     * 发布文章
     * @param articleParam
     * @return
     */
    @PostMapping("publish")
    public Result publish(@RequestBody ArticleParam articleParam) {
        return articleService.publish(articleParam);
    }


    /**
     * 发布文章
     * @param articleParam
     * @return
     */
    @PostMapping("edit")
    public Result edit(@RequestBody ArticleParam articleParam) {
        return articleService.edit(articleParam);
    }
}
