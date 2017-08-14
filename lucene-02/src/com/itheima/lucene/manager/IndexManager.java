package com.itheima.lucene.manager;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

/**
 * 索引维护 添加 入门程序写过了 删除 修改 查询
 * 
 * @author lx
 *
 */
public class IndexManager {

	//获取IndexWriter
	public IndexWriter getIndexWriter() throws IOException{
		Analyzer analyzer = new IKAnalyzer(); 
		//5:创建索引到索引库中   
		Directory directory = FSDirectory.open(new File("D:\\temp\\index"));
		IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);
		return  new IndexWriter(directory, config);
	}
	
	// 删除
	// 全删除
	@Test
	public void testDeleteAll() throws IOException {
		IndexWriter indexWriter = getIndexWriter();
		//删除 全  索引库中 文档部分 索引部分全删除
		indexWriter.deleteAll();
		indexWriter.close();
	}

	// 指定条件删除
	@Test
	public void testDeleteQuery() throws Exception {
		IndexWriter indexWriter = getIndexWriter();
		indexWriter.deleteDocuments(new Term("name","lucene"));
		indexWriter.close();
	}
	// 修改  
	@Test
	public void testUpdate() throws Exception {
		IndexWriter indexWriter = getIndexWriter();
		//修改  先删除  再添加
//		Term : 删除 的条件  
		// doc ： 添加文档
		Document doc = new Document();
		doc.add(new StoredField("ID", 9));
		doc.add(new TextField("NAME", "测试文档名称",Store.YES));
		doc.add(new TextField("CONTENT", "测试文档内容",Store.NO));
		
		indexWriter.updateDocument(new Term("name","mybatis"), doc);
		
		indexWriter.close();
	}
	
	//获取索引搜索类
	public IndexSearcher getIndexSearcher() throws Exception{
		//读取的流
		Directory directory = FSDirectory.open(new File("D:\\temp\\index"));
		IndexReader indexReader = DirectoryReader.open(directory);
		//搜索
		return new IndexSearcher(indexReader);
	}
	//打印结果
	public void printResult(IndexSearcher indexSearcher,Query query) throws Exception{
		TopDocs topDocs = indexSearcher.search(query, 5);
		//文档的ID
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		for (ScoreDoc scoreDoc : scoreDocs) {
			Document doc = indexSearcher.doc(scoreDoc.doc);
			System.out.println("Id:" + doc.get("id"));
			System.out.println("name:" + doc.get("name"));
			System.out.println("price:" + doc.get("price"));
			System.out.println("pic:" + doc.get("pic"));
//			System.out.println("desc:" + doc.get("desc"));
		}
	}
	//通过子类查询 
	//区间查询
//	NumericRangeQuery，指定数字范围查询.
	@Test
	public void testNumericRangeQuery() throws Exception {
		IndexSearcher indexSearcher = getIndexSearcher();
		//指定数字范围查询 
		Query query = NumericRangeQuery. 
				newFloatRange("price", 70f, 78f, true, false) ;
		System.out.println(query);
		//打印结果
		printResult(indexSearcher,query);
		//关流
		indexSearcher.getIndexReader().close();
	}
	
	//组合查询
	@Test
	public void testBooleanQuery() throws Exception {
		IndexSearcher indexSearcher = getIndexSearcher();
		//需求 ：  name 既是是 java 或者是  Luene    
		Query query1 = new TermQuery(new Term("name","java"));
		Query query2 = new TermQuery(new Term("name","lucene"));
		BooleanQuery query = new BooleanQuery();
		query.add(query1, Occur.MUST);
		query.add(query2, Occur.MUST_NOT);
		
		System.out.println(query);
		//打印结果
		printResult(indexSearcher,query);
		//关流
		indexSearcher.getIndexReader().close();
	}
	//条件解析查询   QueryParser
	@Test
	public void testQueryParser() throws Exception {
		IndexSearcher indexSearcher = getIndexSearcher();
		
		//条件 解析的方式
		//参数1：默认查询的域名
		QueryParser parser = new QueryParser("name",new IKAnalyzer());
//		Query query = parser.parse("name:java OR name:lucene");
//		Query query = parser.parse("price:[70.0 TO 78.0}");
		Query query = parser.parse("+name:java -name:lucene");
//		Query query = parser.parse("name:java name:lucene");
//		Query query = parser.parse("name:java NOT name:lucene");
		
		//打印结果
		printResult(indexSearcher,query);
		//关流
		indexSearcher.getIndexReader().close();
	}
	//条件解析查询   MultiFieldQueryParser
	@Test
	public void testMultiFieldQueryParser() throws Exception {
		IndexSearcher indexSearcher = getIndexSearcher();
		String[] fields = {"name","content"};
		MultiFieldQueryParser parser = new MultiFieldQueryParser(fields,new IKAnalyzer());
		Query query = parser.parse("java");
		//打印结果
		printResult(indexSearcher,query);
		//关流
		indexSearcher.getIndexReader().close();
		
	}
}
