package com.txy.blog.admin.controller;

import com.txy.blog.admin.model.params.PageParam;
import com.txy.blog.admin.pojo.Category;
import com.txy.blog.admin.pojo.Tag;
import com.txy.blog.admin.service.TagsService;
import com.txy.blog.admin.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin/tags")
public class AdminTagsController {
    @Autowired
    private TagsService tagsService;

    @PostMapping("listTags")
    public Result listTags(@RequestBody PageParam pageParam) {
        return tagsService.listTags(pageParam);
    }

    @PostMapping("add")
    public Result add(@RequestBody Tag tag) {
        return tagsService.add(tag);
    }

    @PostMapping("update")
    public Result update(@RequestBody Tag tag) {
        return tagsService.update(tag);
    }

    @PostMapping("delete/{id}")
    public Result delete(@PathVariable Long id) {
        return tagsService.delete(id);
    }

    @GetMapping("all")
    public Result tagsAll() {
        return tagsService.tagsAll();
    }

}
