package com.example.lucene;

import lombok.SneakyThrows;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Service;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author wgm
 * @since 2021/4/27
 */
@Service
public class ArticleService {

    private static final String INDEX_PATH = "D:\\project\\lucene\\src\\main\\resources\\index";

    /**
     * 定义Index的查询方法
     *
     * @throws Exception
     */
    @SneakyThrows
    public List<Article> searchIndex() {
        File file = new File(INDEX_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }

        DirectoryReader directoryReader = DirectoryReader.open(FSDirectory.open(Paths.get(INDEX_PATH)));
        IndexSearcher indexSearcher = new IndexSearcher(directoryReader);

        String fieldName = "content";
        Analyzer ikAnalyzer = new IKAnalyzer(true);
        QueryParser queryParser = new QueryParser(fieldName, ikAnalyzer);

        String fieldValue = "lucene";
        int limit = 10;
        Query query = queryParser.parse(fieldValue);
        TopDocs topDocs = indexSearcher.search(query, limit);
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;

        ArrayList<Article> articleList = new ArrayList<>();
        for (ScoreDoc scoreDoc : scoreDocs) {
            int docId = scoreDoc.doc;
            Document document = indexSearcher.doc(docId);
            Article article = documentToArticle(document);
            articleList.add(article);
        }
        directoryReader.close();
        return articleList;
    }

    /**
     * 定义Index的创建方法
     *
     * @throws IOException
     */
    @SneakyThrows
    public void createIndex() {
        File file = new File(INDEX_PATH);
        if (file.exists()) {
            delete(file);
        }

        Article article = Article.builder()
                .id(108L)
                .author("王磊")
                .title("Offer来了之Lucene学习")
                .content("This is a simple lucene demo")
                .url("https://blog.csdn.net/m0_57488641/article/details/116182742")
                .build();
        Document document = articleToDocument(article);

        FSDirectory fsDirectory = FSDirectory.open(Paths.get(INDEX_PATH));
        Analyzer ikAnalyzer = new IKAnalyzer(true);
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(ikAnalyzer);
        IndexWriter indexWriter = new IndexWriter(fsDirectory, indexWriterConfig);
        indexWriter.addDocument(document);
        indexWriter.close();
    }

    /**
     * 递归删除文件或目录
     *
     * @param file
     */
    private void delete(File file) {
        if (file.isDirectory()) {
            Arrays.stream(file.listFiles()).forEach(iFile -> delete(iFile));
        }
        file.delete();
    }

    /**
     * 将接收的原始数据结构转换为Lucene对应的Document格式
     *
     * @param article
     * @return
     */
    private Document articleToDocument(Article article) {
        Document document = new Document();
        document.add(new LongPoint("id", article.getId())); // LongPoint：文档的id
        document.add(new StoredField("id", article.getId())); // StoredField：不分词、不索引、存储
        document.add(new TextField("title", article.getTitle(), Field.Store.YES)); // TextField：分词、索引
        document.add(new TextField("content", article.getContent(), Field.Store.YES));
        document.add(new StringField("author", article.getAuthor(), Field.Store.YES)); // StringField：分词、索引
        document.add(new StoredField("url", article.getUrl()));
        return document;
    }

    /**
     * 将查询的Document转换为Article
     *
     * @param document
     * @return
     */
    private static Article documentToArticle(Document document) {
        return Article.builder()
                .id(Long.parseLong(document.get("id")))
                .title(document.get("title"))
                .content(document.get("content"))
                .author(document.get("author"))
                .url(document.get("url"))
                .build();
    }
}
