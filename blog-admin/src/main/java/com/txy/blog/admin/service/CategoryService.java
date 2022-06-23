package com.txy.blog.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.txy.blog.admin.config.QiniuConfig;
import com.txy.blog.admin.mapper.AdminCategoryMapper;
import com.txy.blog.admin.model.params.PageParam;
import com.txy.blog.admin.pojo.Category;
import com.txy.blog.admin.utils.QiniuUtils;
import com.txy.blog.admin.vo.CategoryVo;
import com.txy.blog.admin.vo.PageResult;
import com.txy.blog.admin.vo.Result;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {
    @Autowired
    private AdminCategoryMapper adminCategoryMapper;

    @Autowired
    private QiniuConfig qiniuConfig;

    public Result listCategory(PageParam pageParam) {
        /**
         * 分页数据，
         * 查询所有分类
         */
        Page<Category> page=new Page<>(pageParam.getCurrentPage(),pageParam.getPageSize());
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(pageParam.getQueryString())) {
            queryWrapper.eq(Category::getCategoryName,pageParam.getQueryString());
        }
        Page<Category> page1= adminCategoryMapper.selectPage(page,queryWrapper);
        PageResult<Category> pageResult=new PageResult<>();
        pageResult.setList(page1.getRecords());
        pageResult.setTotal(page1.getTotal());
        return Result.success(pageResult);
    }

    public Result add(Category category) {
        this.adminCategoryMapper.insert(category);
        return Result.success(null);
    }

    public Result update(Category category) {
        this.adminCategoryMapper.updateById(category);
        return Result.success(null);
    }

    public Result delete(Long id) {
        this.adminCategoryMapper.deleteById(id);
        return Result.success(null);
    }

    public Result findAll() {
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.select(Category::getId, Category::getCategoryName);
        List<Category> categories = adminCategoryMapper.selectList(queryWrapper);
        // 页面交互的对象
        return Result.success(copyList(categories));
    }

    public CategoryVo copy(Category category){
        CategoryVo categoryVo = new CategoryVo();
        BeanUtils.copyProperties(category,categoryVo);
        categoryVo.setId(String.valueOf(category.getId()));
        return categoryVo;
    }
    public List<CategoryVo> copyList(List<Category> categoryList){
        List<CategoryVo> categoryVoList = new ArrayList<>();
        for (Category category : categoryList) {
            categoryVoList.add(copy(category));
        }
        return categoryVoList;
    }

    public CategoryVo findCategoryById(Long categoryId) {
        Category category = adminCategoryMapper.selectById(categoryId);
        CategoryVo categoryVo=new CategoryVo();
        if (category==null) return categoryVo;
        BeanUtils.copyProperties(category,categoryVo);
        categoryVo.setId(String.valueOf(category.getId()));
        return categoryVo;
    }


    public Result upload(MultipartFile file) {
        String accessKey = qiniuConfig.getAccessKey();
        String accessSecret = qiniuConfig.getAccessSecret();
        String bucket = qiniuConfig.getBucket();
        String fileServerUrl = qiniuConfig.getFileServerUrl();
        String originalFilename = file.getOriginalFilename();
        DateTime dateTime=new DateTime();
        String fileName="category/"+
                dateTime.toString("yyyy")+"/" +
                dateTime.toString("MM")+"/"+
                UUID.randomUUID().toString()+"."+
                StringUtils.substringAfterLast(originalFilename,".");
        try {
            boolean upload = QiniuUtils.upload(accessKey, accessSecret, bucket, file.getBytes(), fileName);
            if (upload) {
                return Result.success(fileServerUrl+fileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.fail(-999,"图片上传失败");
    }
}
