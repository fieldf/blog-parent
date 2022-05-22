package com.txy.blog.controller;

import com.txy.blog.service.CategoryService;
import com.txy.blog.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.management.relation.RelationSupport;

@RestController
@RequestMapping("categorys")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @GetMapping
    public Result categories() {
        return categoryService.findAll();
    }

    @GetMapping("detail")
    public Result categoriesDetails() {
        return categoryService.findAllDetails();
    }

    @GetMapping("detail/{id}")
    public Result categoryDetailById(@PathVariable("id")Long categoryId) {
        return categoryService.categoryDetailById(categoryId);
    }
}
