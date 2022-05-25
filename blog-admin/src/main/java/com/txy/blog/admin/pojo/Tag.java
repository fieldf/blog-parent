package com.txy.blog.admin.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Tag {
    @TableId(type = IdType.AUTO)
    Long id;
    String avatar;
    String tagName;
}
