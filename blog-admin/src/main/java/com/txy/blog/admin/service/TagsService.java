package com.txy.blog.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.txy.blog.admin.config.QiniuConfig;
import com.txy.blog.admin.mapper.AdminArticleTagMapper;
import com.txy.blog.admin.mapper.TagsMapper;
import com.txy.blog.admin.model.params.PageParam;
import com.txy.blog.admin.pojo.ArticleTag;
import com.txy.blog.admin.pojo.Tag;
import com.txy.blog.admin.utils.QiniuUtils;
import com.txy.blog.admin.vo.PageResult;
import com.txy.blog.admin.vo.Result;
import com.txy.blog.admin.vo.TagVo;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.invoke.LambdaMetafactory;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TagsService {
    @Autowired
    private TagsMapper tagsMapper;

    @Autowired
    private AdminArticleTagMapper adminArticleTagMapper;

    @Autowired
    private QiniuConfig qiniuConfig;

    /**
     * 分页显示所有标签
     * @param pageParam
     * @return
     */
    public Result listTags(PageParam pageParam) {
        Page<Tag> page=new Page<>(pageParam.getCurrentPage(),pageParam.getPageSize());
        LambdaQueryWrapper<Tag> queryWrapper=new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(pageParam.getQueryString())) {
            // 查询条件是标签名称
            queryWrapper.eq(Tag::getTagName,pageParam.getQueryString());
        }
        Page<Tag> tagPage = tagsMapper.selectPage(page, queryWrapper);
        PageResult<Tag> pageResult=new PageResult<>();
        pageResult.setList(tagPage.getRecords());
        pageResult.setTotal(tagPage.getTotal());
        return Result.success(pageResult);
    }

    public Result add(Tag tag) {
        /**
         * 添加一个tag
         */
        tagsMapper.insert(tag);
        return Result.success(null);
    }

    public Result update(Tag tag) {
        tagsMapper.updateById(tag);
        return Result.success(null);
    }

    public Result delete(Long id) {
        /**
         * 删除时既要删除tag
         * 也要删除article-tag的表
         */
        tagsMapper.deleteById(id);
        LambdaQueryWrapper<ArticleTag> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleTag::getTagId,id);
        adminArticleTagMapper.delete(queryWrapper);
        return Result.success(null);
    }

    public Result tagsAll() {
        LambdaQueryWrapper<Tag> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.select(Tag::getId,Tag::getTagName);
        List<Tag> tags = tagsMapper.selectList(queryWrapper);
        return Result.success(copyList(tags));
    }

    public TagVo copy(Tag tag){
        TagVo tagVo = new TagVo();
        BeanUtils.copyProperties(tag,tagVo);
        tagVo.setId(String.valueOf(tag.getId()));
        return tagVo;
    }
    public List<TagVo> copyList(List<Tag> tagList){
        List<TagVo> tagVoList = new ArrayList<>();
        for (Tag tag : tagList) {
            tagVoList.add(copy(tag));
        }
        return tagVoList;
    }

    public List<String> findTagsByArticleId(Long id) {
        LambdaQueryWrapper<ArticleTag> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleTag::getArticleId, id);
        List<ArticleTag> tags = adminArticleTagMapper.selectList(queryWrapper);
        List<String> list=new ArrayList<>();
        for (ArticleTag tag:tags) {
            list.add(String.valueOf(tag.getTagId()));
        }
        return list;
    }

    public Result upload(MultipartFile file) {
        String accessKey = qiniuConfig.getAccessKey();
        String accessSecret = qiniuConfig.getAccessSecret();
        String bucket = qiniuConfig.getBucket();
        String fileServerUrl = qiniuConfig.getFileServerUrl();
        String originalFilename = file.getOriginalFilename();
        DateTime dateTime=new DateTime();
        String fileName="tags/"+
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
