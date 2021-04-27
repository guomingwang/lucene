package com.example.lucene;

import lombok.Builder;
import lombok.Data;

/**
 * 定义Article的数据结构
 *
 * @author wgm
 * @since 2021/4/27
 */
@Data
@Builder
public class Article {

    private Long id;
    private String title;
    private String content;
    private String author;
    private String url;

}
