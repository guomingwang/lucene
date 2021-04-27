package com.example.lucene;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author wgm
 * @since 2021/4/27
 */
@RestController
@RequestMapping("article")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping("createIndex")
    public void createIndex() {
        articleService.createIndex();
    }

    @GetMapping("searchIndex")
    public List<Article> searchIndex() {
        return articleService.searchIndex();
    }
}
