package com.txy.blog.controller;

import com.txy.blog.service.TagService;
import com.txy.blog.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("tags")
public class TagsController {
    @Autowired
    private TagService tagService;

    // /tags/hot
    @GetMapping("hot")
    public Result hot() {
        int limit = 6;
        return tagService.hots(limit);
    }


    // /tags
    @GetMapping
    public Result findAll() {
        return tagService.findAll();
    }


    // /tags
    @GetMapping("detail")
    public Result findAllDetails() {
        return tagService.findAllDetails();
    }

    // /tags
    @GetMapping("detail/{id}")
    public Result findDetailById(@PathVariable("id") Long id) {
        return tagService.findDetailById(id);
    }

}
