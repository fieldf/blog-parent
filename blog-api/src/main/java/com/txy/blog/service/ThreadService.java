package com.txy.blog.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.txy.blog.dao.mapper.ArticleMapper;
import com.txy.blog.dao.pojo.Article;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ThreadService {
    // 期望此操作在线程池执行，不会影响原有的主线程
    @Async("taskExecutor")
    public void updateArticleViewCount(ArticleMapper articleMapper, Article article) {
        int viewCounts = article.getViewCounts();
        // 1个bug，创建对象，这个对象中值对应是基本类型，默认是0，mybatisplus把0值更新到数据库中.只要不为null，就更新
        // 我们要更新viewCount,但是把comment_counts,weight更新成0了。
        Article articleUpdate=new Article();

        articleUpdate.setViewCounts(viewCounts+1);
        LambdaUpdateWrapper<Article> updateWrapper=new LambdaUpdateWrapper<>();
        updateWrapper.eq(Article::getId,article.getId());
        //设置一个，为了在多线程的环境下线程安全
        updateWrapper.eq(Article::getViewCounts,viewCounts);
        // update article set view_count=100 where view_count =99 and id=***
        articleMapper.update(articleUpdate,updateWrapper);
        try {
            Thread.sleep(5000);
            System.out.println("更新完成了");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
