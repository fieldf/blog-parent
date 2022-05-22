package com.txy.blog.controller;

import com.txy.blog.service.CommentsService;
import com.txy.blog.vo.Result;
import com.txy.blog.vo.params.CommentParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("comments")
public class CommentsController {
    @Autowired
    private CommentsService commentsService;
    // comments/article/{id}

    /**
     * 显示评论
     * @param id
     * @return
     */
    @GetMapping("article/{id}")
    public Result comments(@PathVariable("id") Long id) {
        return commentsService.commentsByArticleId(id);
    }

    /**
     * 评论
     * @param commentParam
     * @return
     */
    @PostMapping("create/change")
    public Result comment(@RequestBody CommentParam commentParam) {
        return commentsService.comment(commentParam);
    }
}
