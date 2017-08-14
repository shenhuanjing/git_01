package com.itheima.solrj;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

/**
 * SOlrJ的
 * 添加
 * 修改
 * 删除
 * 查询
 * @author lx
 *
 */
public class SolrJDemo {

	
	//添加
	@Test
	public void testAdd() throws Exception {
		String baseURL = "http://localhost:8080/solr";
		SolrServer solrServer = new HttpSolrServer(baseURL);
		
		
		SolrInputDocument doc = new SolrInputDocument();
		doc.setField("id", 9);
		doc.setField("name", "赵丽颖");
		//添加
		solrServer.add(doc);
		solrServer.commit();
		
	}
	//删除
	@Test
	public void testDelete() throws Exception {
		String baseURL = "http://localhost:8080/solr";
		SolrServer solrServer = new HttpSolrServer(baseURL);

//		solrServer.deleteById("10");
		solrServer.deleteByQuery("name:我是林更新");
		solrServer.commit();
		
	}
	//查询  简单    
	@Test
	public void testQuery() throws Exception {
		String baseURL = "http://localhost:8080/solr";
		SolrServer solrServer = new HttpSolrServer(baseURL);
		//条件对象
		SolrQuery params = new SolrQuery();
		
		//条件  关键词 查询所有 
		params.set("q", "*:*");
		//执行查询
		QueryResponse response = solrServer.query(params);
		
		//结果集
		SolrDocumentList docs = response.getResults();
//		总条数
		long numFound = docs.getNumFound();
		System.out.println("总条数：" + numFound);
		
		for (SolrDocument doc : docs) {
			System.out.println("id:" + doc.get("id"));
			System.out.println("name:" + doc.get("name"));
		}
	}
	//复杂查询  
	// 关键词查询   过滤条件  排序  分页  查询指定域  默认查询的域  高亮
	@Test
	public void testSolrSearcher() throws Exception {
		String baseURL = "http://localhost:8080/solr";
		SolrServer solrServer = new HttpSolrServer(baseURL);
		//子类查询
		SolrQuery params = new SolrQuery();
		
//		关键词查询
//		params.set("q", "product_name:钻石");
		params.setQuery("钻石");
		// 过滤条件
		params.set("fq", "product_price:[7 TO 10}");
//		排序
		params.setSort("product_price", ORDER.desc);
		//分页
		params.setStart(0);
		params.setRows(10);
//		查询指定域
		params.set("fl", "id,product_name");
		//默认查询的域
		params.set("df", "product_name");
		//1：打开高亮开关
		params.setHighlight(true);
		//2: 需要高亮的域
		params.addHighlightField("product_name");
		//3:设置高亮的前缀
		params.setHighlightSimplePre("<span style='color:red'>");
		//4:设置高亮的后缀
		params.setHighlightSimplePost("</span>");
		
		//执行查询
		QueryResponse response = solrServer.query(params);
		//取高亮
		Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
		//结果集
		SolrDocumentList docs = response.getResults();
		//总条数
		long numFound = docs.getNumFound();
		System.out.println("总条数：" + numFound);
		for (SolrDocument doc : docs) {
			System.out.println("Id:" + doc.get("id"));
			System.out.println("名称:" + doc.get("product_name"));
			
			Map<String, List<String>> map = highlighting.get(doc.get("id"));
			List<String> list = map.get("product_name");
			System.out.println("高亮的名称：" + list.get(0));
		}
	}
}
