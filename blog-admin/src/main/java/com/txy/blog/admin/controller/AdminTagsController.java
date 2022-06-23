package com.txy.blog.admin.controller;

import com.txy.blog.admin.model.params.PageParam;
import com.txy.blog.admin.pojo.Category;
import com.txy.blog.admin.pojo.Tag;
import com.txy.blog.admin.service.TagsService;
import com.txy.blog.admin.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping(value = "upload")
    public Result upload(@RequestParam("imageFile") MultipartFile file){
        // 图片往 七牛云上传
        // 云存储，图片 用户访问的时候需要消耗带宽，带宽如果都被占用完了，我们的应用就无法访问了
        // 一张图片2M 10个人同时访问 20M，服务器带宽10M左右
        // 网络卡了，电脑需要用网络的应用 相当于卡了 如果图片的访问服务器的带宽资源占用完，代表应用不能访问了
        // 这是不可接受的，加大带宽，但是带宽的费用非常大，抖音一天的带宽费用 上亿，我们的应用不是所有的情况都有如此大的流量的
        // 间歇性的,这时候 云存储，按量付费，花费较少，云存储在各地都有服务器，可以针对性的对图片进行加速访问
        return tagsService.upload(file);
    }

}
