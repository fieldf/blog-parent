package com.txy.blog.admin.controller;

import com.txy.blog.admin.model.params.PageParam;
import com.txy.blog.admin.pojo.Category;
import com.txy.blog.admin.service.CategoryService;
import com.txy.blog.admin.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin/category")
public class AdminCategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping("categoryList")
    public Result listCategory(@RequestBody PageParam pageParam) {
        return categoryService.listCategory(pageParam);
    }

    @PostMapping("add")
    public Result add(@RequestBody Category category) {
        return categoryService.add(category);
    }

    @PostMapping("update")
    public Result update(@RequestBody Category category) {
        return categoryService.update(category);
    }

    @PostMapping("delete/{id}")
    public Result delete(@PathVariable Long id) {
        return categoryService.delete(id);
    }

    @GetMapping("all")
    public Result allTags() {
        return categoryService.findAll();
    }
}
