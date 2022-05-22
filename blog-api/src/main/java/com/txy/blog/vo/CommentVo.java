package com.txy.blog.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.List;

@Data
public class CommentVo  {
    // 防止前端精度损失，把id转为string
    // 显示评论时，分布式id比较长，传到前端会有精度损失。
    // 前端进行评论，拿到的id，也就是CommentParam中作为评论parent_id的值有了损失。
//    @JsonSerialize(using = ToStringSerializer.class)
    private String id;

    private UserVo author;

    private String content;

    private List<CommentVo> childrens;

    private String createDate;

    private Integer level;

    private UserVo toUser;
}
