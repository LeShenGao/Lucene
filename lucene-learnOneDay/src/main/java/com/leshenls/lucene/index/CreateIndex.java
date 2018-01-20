package com.leshenls.lucene.index;


import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;

import static org.apache.lucene.document.Field.Store.YES;


/**
 * 创建索引
 *
 * @author gl
 * @create 2018-01-19-9:47
 */
public class CreateIndex {
    @Test
    public void test() throws IOException {
        IndexWriter indexWriter = getIndexWriter();
        //原始文档的路径D:\传智播客\01.课程\04.lucene\01.参考资料\searchsource
        File dir = new File("G:\\note\\javase笔记\\day01\\错误代码");
        for (File f : dir.listFiles()) {
            //文件名
            String fileName = f.getName();
            //文件内容
            String fileContent = FileUtils.readFileToString(f);
            //文件路径
            String filePath = f.getPath();
            //文件的大小
            long fileSize = FileUtils.sizeOf(f);
            //创建文件名域
            //第一个参数：域的名称
            //第二个参数：域的内容
            //第三个参数：是否存储
            Field fileNameField = new TextField("filename", fileName, YES);
            //文件内容域
            Field fileContentField = new TextField("content", fileContent, YES);
            //文件路径域（不分析、不索引、只存储）
            Field filePathField = new StoredField("path", filePath);
            //文件大小域
            Field fileSizeField = new LongField("size", fileSize, YES);

            //创建document对象
            Document document = new Document();
            document.add(fileNameField);
            document.add(fileContentField);
            document.add(filePathField);
            document.add(fileSizeField);
            //创建索引，并写入索引库
            indexWriter.addDocument(document);
        }
        //关闭indexwriter
        indexWriter.close();
    }

    /**
     * 查询索引中的数据
     *
     * @throws IOException
     */
    @Test
    public void test2() throws IOException {
        IndexSearcher indexSearcher = getIndexSearcher();
//        第四步：创建一个TermQuery对象，指定查询的域和查询的关键词。
        TermQuery termQuery = new TermQuery(new Term("filename", "helloworld"));
        printResult(indexSearcher, termQuery);
//        第七步：关闭IndexReader对象
        indexSearcher.getIndexReader().close();
    }

    private void printResult(IndexSearcher indexSearcher, Query query) throws IOException {
        //        第五步：执行查询。从头数前面两个文件进行搜索
        TopDocs topDocs = indexSearcher.search(query, 2);
        System.out.println(topDocs.totalHits);
//        第六步：返回查询结果。遍历查询结果并输出。
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
//            scoreDoc.doc就是document对象的id
//            根据document的id找到document对象
            Document document = indexSearcher.doc(scoreDoc.doc);
            System.out.println(document.get("filename"));
            System.out.println(document.get("content"));
            System.out.println(document.get("path"));
            System.out.println(document.get("size"));
        }
    }

    /**
     * 删除索引中指定域的数据
     */
    @Test
    public void test3() throws IOException {
        IndexWriter indexWriter = getIndexWriter();
        Query query = new TermQuery(new Term("filename", "helloworld"));
        indexWriter.deleteDocuments(query);
        indexWriter.close();
    }

    /**
     * 删除所有索引
     *
     * @throws IOException
     */
    @Test
    public void test4() throws IOException {
        IndexWriter indexWriter = getIndexWriter();
        indexWriter.deleteAll();
        indexWriter.close();
    }

    /**
     * 获取索引写入对象
     *
     * @return
     * @throws IOException
     */
    private IndexWriter getIndexWriter() throws IOException {
        //指定索引库存放的路径
        //D:\temp\0108\index
        Directory directory = FSDirectory.open(new File("G:\\32期视频2016年\\index"));
        //索引库还可以存放到内存中
        //Directory directory = new RAMDirectory();
        //创建一个标准分析器
        Analyzer analyzer = new IKAnalyzer();
        //创建indexwriterCofig对象
        //第一个参数： Lucene的版本信息，可以选择对应的lucene版本也可以使用LATEST
        //第二根参数：分析器对象
        IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);
        //创建indexwriter对象
        return new IndexWriter(directory, config);
    }

    /**
     * 获取索引搜索对象
     *
     * @return
     * @throws IOException
     */
    private IndexSearcher getIndexSearcher() throws IOException {
//        第一步：创建一个Directory对象，也就是索引库存放的位置。
        Directory directory = FSDirectory.open(new File("G:\\32期视频2016年\\index"));
//        第二步：创建一个indexReader对象，需要指定Directory对象。
        IndexReader indexReader = DirectoryReader.open(directory);
//        第三步：创建一个indexsearcher对象，需要指定IndexReader对象
        return new IndexSearcher(indexReader);
    }

    /**
     * 修改原索引中的数据
     * 原理:
     * 先删除后添加,也就是说原来的数据被删除后,再添加新的数据
     *
     * @throws IOException
     */
    @Test
    public void test5() throws IOException {
        IndexWriter indexWriter = getIndexWriter();
        Document document = new Document();
        document.add(new TextField("content", "文本内容", Field.Store.YES));
        document.add(new TextField("filename", "文本名称", Field.Store.YES));
        indexWriter.updateDocument(new Term("content", "main"), document);
        indexWriter.close();
    }

    /**
     * 查询所有数据
     *
     * @throws IOException
     */
    @Test
    public void test6() throws IOException {
        IndexSearcher indexSearcher = getIndexSearcher();
        Query query = new MatchAllDocsQuery();
//        执行查询
        printResult(indexSearcher, query);
    }

    @Test
    public void test7() throws IOException {
        IndexSearcher indexSearcher = getIndexSearcher();
        Query query = new TermQuery(new Term("filename","文本名称"));
//        打印结果
        printResult(indexSearcher,query);
    }
}
