package com.txy.blog.admin.model.params;

import com.txy.blog.admin.vo.ArticleBodyVo;
import com.txy.blog.admin.vo.CategoryVo;
import com.txy.blog.admin.vo.TagVo;
import lombok.Data;

import java.util.List;

@Data
public class ArticleParam {

//    @JsonSerialize(using = ToStringSerializer.class)
    // 这里遇到一个问题，就是首页显示文章列表时，preview看见的文章内容id，和我后端传过去的不一致。
    // 可能是因为系统序列化时给精度省略了一些，按照弹幕所说加了上述注解好了。
    private String id;

    private String title;

    private String summary;

    private Integer commentCounts;

    private Integer viewCounts;

    private Integer weight;
    /**
     * 创建时间
     */
    private String createDate;

    private String author;

    private ArticleBodyVo body;

    private List<TagVo> tags;

    private CategoryVo category;

}
