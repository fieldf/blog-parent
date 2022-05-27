package com.txy.blog.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.txy.blog.admin.model.params.ArticleParam;
import com.txy.blog.admin.model.params.PageParam;
import com.txy.blog.admin.pojo.Tag;
import com.txy.blog.admin.vo.ArticleVo;
import com.txy.blog.admin.vo.PageResult;
import com.txy.blog.admin.vo.Result;
import com.txy.blog.dao.mapper.ArticleMapper;
import com.txy.blog.dao.mapper.ArticleTagMapper;
import com.txy.blog.dao.pojo.Article;
import com.txy.blog.dao.pojo.ArticleTag;
import com.txy.blog.service.CategoryService;
import com.txy.blog.service.SysUserService;
import com.txy.blog.service.TagService;
import com.txy.blog.vo.TagVo;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class AdminArticleService {
    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private TagsService tagsService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ArticleTagMapper articleTagMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public Result articleList(PageParam pageParam) {
        // 不能直接用blog-api中的articleService，因为前端传来的参数差距很大，所以还是自己写service
        Page<Article> page=new Page<>(pageParam.getCurrentPage(),pageParam.getPageSize());
        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(pageParam.getQueryString())) {
            // 查询文章名称
            queryWrapper.like(Article::getTitle, pageParam.getQueryString());
        }
        // 按页查询
        Page<Article> page1 = articleMapper.selectPage(page, queryWrapper);
        List<Article> records = page1.getRecords();
        List<ArticleVo> articleVoList = copyList(records, true, true, true);
        PageResult<ArticleVo> pageResult=new PageResult<>();
        pageResult.setList(articleVoList);
        pageResult.setTotal(page1.getTotal());
        return Result.success(pageResult);
    }
    private List<ArticleVo> copyList(List<Article> records, boolean isTag, boolean isAuthor,  boolean isCategory) {
        List<ArticleVo> articleVoList=new ArrayList<>();
        for (Article record: records) {
            articleVoList.add(copy(record,isTag,isAuthor,isCategory));
        }
        return articleVoList;
    }


    private ArticleVo copy(Article article, boolean isTag, boolean isAuthor,boolean isCategory) {
        ArticleVo articleVo=new ArticleVo();
        articleVo.setId(String.valueOf(article.getId()));
        BeanUtils.copyProperties(article,articleVo);
        articleVo.setCreateDate(new DateTime(article.getCreateDate(), DateTimeZone.forOffsetHours(8)).toString("yyyy-MM-dd HH:mm"));
        // 并不是所有的接口，都需要标签，作者信息
        if (isTag) {
            Long id = article.getId();
            articleVo.setTags(tagsService.findTagsByArticleId(id));
        }
        if (isAuthor) {
            Long authorId = article.getAuthorId();
            articleVo.setAuthor(sysUserService.findUserById(authorId).getNickname());
        }
        if (isCategory) {
            Long categoryId = article.getCategoryId();
            articleVo.setCategory(categoryService.findCategoryById(categoryId));
        }
        return articleVo;
    }

    public Result update(ArticleParam articleParam) {
        /**
         * 1. 更新article表
         * 2. 更新tag
         */
//        Article article=new Article();
        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getId, articleParam.getId());
        Article article = articleMapper.selectOne(queryWrapper);
        BeanUtils.copyProperties(articleParam,article);
        article.setCategoryId(Long.parseLong(articleParam.getCategory().getId()));
        articleMapper.updateById(article);
        // 删除article-tag关联表，新增article-tag表
        LambdaQueryWrapper<ArticleTag> queryWrapper2=new LambdaQueryWrapper<>();
        queryWrapper2.eq(ArticleTag::getArticleId,articleParam.getId());
        articleTagMapper.delete(queryWrapper2);
        List<TagVo> tags = articleParam.getTags();
        for (TagVo tagVo:tags) {
            Long articleId=article.getId();
            ArticleTag articleTag=new ArticleTag();
            articleTag.setArticleId(articleId);
            articleTag.setTagId(Long.parseLong(tagVo.getId()));
            articleTagMapper.insert(articleTag);
        }
        deleteRedisArticles();
        return Result.success(null);
    }

    /**
     * 删除首页文章缓存
     * listArticle::ArticleController::listArticle::83dab9ea501defddc22656c0e4f2d80f
     * @return
     */
    private void deleteRedisArticles() {
//        String key="listArticle::ArticleController::listArticle::";
        String key="listArticle";
        Set<String> keys=redisTemplate.keys(key+"*");
        System.out.println("listArticle"+redisTemplate.delete(keys));
    }

    public Result delete(Long id) {
        /**
         * 1. 删除article
         * 2. 删除article-tag表
         */
        articleMapper.deleteById(id);
        LambdaQueryWrapper<ArticleTag> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleTag::getArticleId,id);
        articleTagMapper.delete(queryWrapper);
        deleteRedisArticles();
        return Result.success(null);
    }
}
