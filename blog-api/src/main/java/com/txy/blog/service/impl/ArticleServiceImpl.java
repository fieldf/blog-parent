package com.txy.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.txy.blog.dao.dos.Archives;
import com.txy.blog.dao.mapper.ArticleBodyMapper;
import com.txy.blog.dao.mapper.ArticleMapper;
import com.txy.blog.dao.mapper.ArticleTagMapper;
import com.txy.blog.dao.pojo.Article;
import com.txy.blog.dao.pojo.ArticleBody;
import com.txy.blog.dao.pojo.ArticleTag;
import com.txy.blog.dao.pojo.SysUser;
import com.txy.blog.service.*;
import com.txy.blog.utils.UserThreadLocal;
import com.txy.blog.vo.ArticleBodyVo;
import com.txy.blog.vo.ArticleVo;
import com.txy.blog.vo.Result;
import com.txy.blog.vo.TagVo;
import com.txy.blog.vo.params.ArticleBodyParam;
import com.txy.blog.vo.params.ArticleParam;
import com.txy.blog.vo.params.PageParams;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ArticleServiceImpl implements ArticleService {
    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private TagService tagService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private ArticleTagMapper articleTagMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


//    @Override
//    public Result listArticle(PageParams pageParams) {
//        /**
//         * 分页查询article数据库表
//          */
//        Page<Article> page=new Page<>(pageParams.getPage(),pageParams.getPageSize());
//        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper<>();
//        if (pageParams.getCategoryId()!=null) {
//            queryWrapper.eq(Article::getCategoryId,pageParams.getCategoryId());
//        }
//        List<Long> articleIdList=new ArrayList<>();
//        if (pageParams.getTagId()!=null) {
//            // 加入标签 条件查询
//            // article表中并没有tag字段 一篇文章多个标签
//            LambdaQueryWrapper<ArticleTag> queryWrapper1=new LambdaQueryWrapper<>();
//            queryWrapper1.eq(ArticleTag::getTagId, pageParams.getTagId());
//            List<ArticleTag> articleTags = articleTagMapper.selectList(queryWrapper1);
//            for (ArticleTag articleTag : articleTags) {
//                articleIdList.add(articleTag.getArticleId());
//            }
//            if (articleIdList.size()>0){
//                // and id in(1,2,3)
//                queryWrapper.in(Article::getId,articleIdList);
//            }
//        }
//        // 是否置顶进行排序
////        queryWrapper.orderByDesc();
//        // order by create_date desc
//        queryWrapper.orderByDesc(Article::getWeight,Article::getCreateDate);
//        Page<Article> articlePage = articleMapper.selectPage(page, queryWrapper);
//        List<Article> records = articlePage.getRecords();
//        // 能直接返回吗？很明显不能
//        List<ArticleVo> articleVoList=copyList(records,true,true);
//        return Result.success(articleVoList);
//    }

    @Override
    public Result listArticle(PageParams pageParams) {
        Page<Article> page=new Page<>(pageParams.getPage(),pageParams.getPageSize());
        IPage<Article> articleIPage = articleMapper.listArticle(page, pageParams.getCategoryId(),
                pageParams.getTagId(), pageParams.getYear(), pageParams.getMonth());
        List<Article> records = articleIPage.getRecords();
        return Result.success(copyList(records,true,true));
    }

    @Override
    public Result hotArticle(int limit) {
        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper<>(); // 查询条件
        queryWrapper.orderByDesc(Article::getViewCounts);
        queryWrapper.select(Article::getId,Article::getTitle);
        queryWrapper.last("limit "+limit);
        // select id,title from article from article order by view_count desc limit 5
        List<Article> articles = articleMapper.selectList(queryWrapper);

        return Result.success(copyList(articles,false,false));
    }

    @Override
    public Result newArticles(int limit) {
        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper<>(); // 查询条件
        queryWrapper.orderByDesc(Article::getCreateDate);
        queryWrapper.select(Article::getId,Article::getTitle);
        queryWrapper.last("limit "+limit);
        // select id,title from article from article order by create_date desc limit 5
        List<Article> articles = articleMapper.selectList(queryWrapper);

        return Result.success(copyList(articles,false,false));
    }

    @Override
    public Result listArchives() {
        // select year(from_unixtime(create_date/1000))
        List<Archives> archivesList = articleMapper.listArchives();
        return Result.success(archivesList);
    }

    @Autowired
    private ThreadService threadService;
    @Override
    public Result findArticleById(Long articleId) {
        /**
         * 1. 根据文章显示文章详情(文章->body，tag，分类categorid)
         * 2. 根据bodyId和categoryid去做关联查询
         */
        Article article = this.articleMapper.selectById(articleId);

        ArticleVo articleVo = copy(article, true, true,true,true);
        // 查看完文章了，新增阅读数，有没有问题呢？
        // 查看完文章之后，本应该直接返回数据了，这时候做了一个更新操作，更新时加写锁，阻塞其他读操作,性能会比较低
        // 更新 增加了此次接口的耗时，如果一旦更新出问题，不能影响查看文章的操作
        // 线程池，可以把更新操作扔到线程池中执行，和主线程就不相关了
        threadService.updateArticleViewCount(articleMapper,article);

        return Result.success(articleVo);
    }

    @Override
    public Result publish(ArticleParam articleParam) {
        /**
         * 1. 发布文章，构建我们的article对象
         * 2. 作者id，当前的登录用户
         * 3. 标签。把标签加入到关联列表中
         * 4. body 内容存储
         */
        // 此接口要加入到登录拦截当中
        SysUser sysUser = UserThreadLocal.get();
        Article article=new Article();
        article.setAuthorId(sysUser.getId());
        article.setWeight(Article.Article_Common);
        article.setViewCounts(0);
        article.setTitle(articleParam.getTitle());
        article.setSummary(articleParam.getSummary());
        article.setCommentCounts(0);
        article.setCreateDate(System.currentTimeMillis());
        article.setCategoryId(Long.parseLong(articleParam.getCategory().getId()));
        // 插入之后会生成一个文章id
        this.articleMapper.insert(article);
        // 标签
        List<TagVo> tags = articleParam.getTags();
        if (tags!=null) {
            for (TagVo tag: tags) {
                Long articleId = article.getId();
                ArticleTag articleTag=new ArticleTag();
                articleTag.setArticleId(articleId);
                articleTag.setTagId(Long.parseLong(tag.getId()));
                articleTagMapper.insert(articleTag);
            }
        }

        // body
        ArticleBody articleBody=new ArticleBody();
        articleBody.setArticleId(article.getId());
        articleBody.setContent(articleParam.getBody().getContent());
        articleBody.setContentHtml(articleParam.getBody().getContentHtml());
        articleBodyMapper.insert(articleBody);

        article.setBodyId(articleBody.getId());
        articleMapper.updateById(article);
        Map<String,String> map=new HashMap<>();
        map.put("id",article.getId().toString());

        // 删除文章缓存
        deleteRedisArticles();
        // 删除最新文章缓存
        deleteRedisNewsArticles();
        return Result.success(map);
    }

    /**
     * 显示文章编辑信息
     * @param articleId
     * @return
     */
    @Override
    public Result findArticleEditById(Long articleId) {
        Article article = this.articleMapper.selectById(articleId);

        ArticleVo articleVo = copy(article, true, true,true,true);
        return Result.success(articleVo);
    }

    /**
     * 编辑文章
     * @param articleParam
     * @return
     */
    @Override
    public Result edit(ArticleParam articleParam) {
        // 更新title
        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getId,articleParam.getId());
        Article article = articleMapper.selectOne(queryWrapper);
        article.setTitle(articleParam.getTitle());
        article.setSummary(articleParam.getSummary());
        article.setCategoryId(Long.valueOf(articleParam.getCategory().getId()));
        articleMapper.updateById(article);
        // 更新body
        ArticleBodyParam body = articleParam.getBody();
        LambdaQueryWrapper<ArticleBody> queryWrapper1=new LambdaQueryWrapper<>();
        queryWrapper1.eq(ArticleBody::getArticleId, articleParam.getId());
        ArticleBody articleBody = articleBodyMapper.selectOne(queryWrapper1);
        articleBody.setContent(body.getContent());
        articleBody.setContentHtml(body.getContentHtml());
        articleBodyMapper.updateById(articleBody);
        Map<String,String> map=new HashMap<>();
        map.put("id",article.getId().toString());
        // 标签
        List<TagVo> tags = articleParam.getTags();
        // 把旧的标签删除
        LambdaQueryWrapper<ArticleTag> queryWrapper2=new LambdaQueryWrapper<>();
        queryWrapper2.eq(ArticleTag::getArticleId, article.getId());
        articleTagMapper.delete(queryWrapper2);
        if (tags!=null) {
            for (TagVo tag: tags) {
                Long articleId = article.getId();
                ArticleTag articleTag=new ArticleTag();
                articleTag.setArticleId(articleId);
                articleTag.setTagId(Long.parseLong(tag.getId()));
                articleTagMapper.insert(articleTag);
            }
        }
        return Result.success(map);
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

    private void deleteRedisNewsArticles() {
//        String key="news_article::ArticleController::newArticles::";
        String key="news_article";
        Set<String> keys=redisTemplate.keys(key+"*");
        System.out.println("news_article"+redisTemplate.delete(keys));
    }

    private List<ArticleVo> copyList(List<Article> records,boolean isTag, boolean isAuthor) {
        List<ArticleVo> articleVoList=new ArrayList<>();
        for (Article record: records) {
            articleVoList.add(copy(record,isTag,isAuthor,false,false));
        }
        return articleVoList;
    }
    private List<ArticleVo> copyList(List<Article> records,boolean isTag, boolean isAuthor,boolean isBody,boolean isCategory) {
        List<ArticleVo> articleVoList=new ArrayList<>();
        for (Article record: records) {
            articleVoList.add(copy(record,isTag,isAuthor,isBody,isCategory));
        }
        return articleVoList;
    }

    @Autowired
    private CategoryService categoryService;
    private ArticleVo copy(Article article, boolean isTag, boolean isAuthor,boolean isBody,boolean isCategory) {
        ArticleVo articleVo=new ArticleVo();
        articleVo.setId(String.valueOf(article.getId()));
        BeanUtils.copyProperties(article,articleVo);
        articleVo.setCreateDate(new DateTime(article.getCreateDate(), DateTimeZone.forOffsetHours(8)).toString("yyyy-MM-dd HH:mm"));
        // 并不是所有的接口，都需要标签，作者信息
        if (isTag) {
            Long id = article.getId();
            articleVo.setTags(tagService.findTagsByArticleId(id));
        }
        if (isAuthor) {
            Long authorId = article.getAuthorId();
            articleVo.setAuthor(sysUserService.findUserById(authorId).getNickname());
        }
        if (isBody) {
            Long bodyId=article.getBodyId();
            articleVo.setBody(findArticleBodyById(bodyId));
        }
        if (isCategory) {
            Long categoryId = article.getCategoryId();
            articleVo.setCategory(categoryService.findCategoryById(categoryId));
        }
        return articleVo;
    }

    @Autowired
    private ArticleBodyMapper articleBodyMapper;
    private ArticleBodyVo findArticleBodyById(Long bodyId) {
        ArticleBody articleBody = articleBodyMapper.selectById(bodyId);
        ArticleBodyVo articleBodyVo=new ArticleBodyVo();
        articleBodyVo.setContent(articleBody.getContent());
        return articleBodyVo;
    }
}
