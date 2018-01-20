package com.gaole.solrj.test;

import org.apache.lucene.document.Document;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * SolrJ测试
 *
 * @author gl
 * @create 2018-01-19-19:50
 */
public class SolrjTest {

    @Test
    public void test() throws IOException, SolrServerException {
        SolrServer solrServer = getSolrServer();


//        创建一个文档对象
        SolrInputDocument document = new SolrInputDocument();

//        向文档中添加域(field)
//        第一个参数:域的名称,域的名称必须是在schema.xml中定义
//        第二个参数:域的值
        document.addField("id", "大胖");
        document.addField("title_ik", "大胖今晚要吃鸡");
        document.addField("content_ik", "今晚必须吃鸡");
        document.addField("product_name", "98K");

//        把document文档添加到索引库中
        solrServer.add(document);
//        提交修改
        solrServer.commit();
    }

    private SolrServer getSolrServer() {
        //        创建与Solr服务器的连接
        return new HttpSolrServer("http://localhost:8081/solr");
    }

    /**
     * 删除所有文档数据
     *
     * @throws IOException
     * @throws SolrServerException
     */
    @Test
    public void test2() throws IOException, SolrServerException {
        SolrServer solrServer = getSolrServer();
        solrServer.deleteByQuery("*:*", 1000);
        solrServer.commit();
    }

    /**
     * 根据条件删除文档
     *
     * @throws IOException
     * @throws SolrServerException
     */
    @Test
    public void test3() throws IOException, SolrServerException {
        SolrServer solrServer = getSolrServer();
        solrServer.deleteByQuery("product_name:幸福", 1000);
        solrServer.commit();
    }

    /**
     * 更新文档数据
     * 原理:
     * id一样,就是修改
     * id不一样,就是添加
     */
    @Test
    public void updateDocument() throws IOException, SolrServerException {
        SolrServer solrServer = getSolrServer();

//        创建文档对象
        SolrInputDocument document = new SolrInputDocument();
//        给文档对象中的域的名称设置对应的值
        document.addField("id", "大胖");
        document.addField("title_ik", "吃鸡次鸡");
        document.addField("content_ik", "狗日的就是吃不到");
        document.addField("product_name", "M4A1");
//        把document文档对象添加到索引库中
        solrServer.add(document);
//        提交修改
        solrServer.commit();
    }

    /**
     * 简单查询
     */
    @Test
    public void test4() throws SolrServerException {
        SolrServer solrServer = getSolrServer();
//        创建一个Query对象
        SolrQuery solrQuery = new SolrQuery();

//        设置查询条件
        solrQuery.setQuery("*:*");

//        执行查询
        QueryResponse queryResponse = solrServer.query(solrQuery);

//        获取查询结果
        SolrDocumentList solrDocumentList = queryResponse.getResults();

//        共查询到商品数量
        System.out.println("共查询到商品数量:" + solrDocumentList.getNumFound());

//        遍历查询的结果
        for (SolrDocument document : solrDocumentList) {
            System.out.println(document.get("id"));
            System.out.println(document.get("product_name"));
            System.out.println(document.get("product_price"));
            System.out.println(document.get("product_catalog_name"));
            System.out.println(document.get("product_picture"));
        }
    }

    /**
     * 文档之多条件查询
     */
    @Test
    public void test5() throws SolrServerException {
//        创建连接
        SolrServer solrServer = getSolrServer();
//        创建一个Query对象
        SolrQuery solrQuery = new SolrQuery();
//        设置查询条件
        solrQuery.setQuery("钻石");
//        过滤条件
        solrQuery.setFilterQueries("product_catalog_name:幽默杂货");
//        排序条件
        solrQuery.setSort("product_price", SolrQuery.ORDER.asc);
//        分页处理
        solrQuery.setStart(0);
        solrQuery.setRows(10);
//        结合域中的列表
        solrQuery.setFields("id", "product_name", "product_price", "product_catalog_name", "product_picture");
//        设置默认搜索域
        solrQuery.set("df", "product_name");
//        高亮显示
        solrQuery.setHighlight(true);
//        高亮显示的域
        solrQuery.addHighlightField("product_name");
//        高亮显示的前缀
        solrQuery.setHighlightSimplePre("<span style=\"color: red\">");
//        高亮显示的后缀
        solrQuery.setHighlightSimplePost("</span>");
//        执行查询
        QueryResponse queryResponse = solrServer.query(solrQuery);
//        获取查询结果
        SolrDocumentList solrDocumentList = queryResponse.getResults();
//        共查询到商品数量
        System.out.println("共查询到商品数量:" + solrDocumentList.getNumFound());
//        遍历查询的结果
        for (SolrDocument solrDocument : solrDocumentList) {
            System.out.println(solrDocument.get("id"));
//            获取高亮显示
            String productName = "";
            Map<String, Map<String, List<String>>> highlighting = queryResponse.getHighlighting();
            List<String> list = highlighting.get(solrDocument.get("id")).get("product_name");
//            判断是否有高亮内容
            if (list != null) {
//                有高亮内容
                productName = list.get(0);
            } else {
//                没有高亮
                productName = (String) solrDocument.get("product_name");
            }
//            打印查询到的数据
            System.out.println(productName);
            System.out.println(solrDocument.get("product_price"));
            System.out.println(solrDocument.get("product_catalog_name"));
            System.out.println(solrDocument.get("product_picture"));
        }
    }
}
