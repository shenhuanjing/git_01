package com.itheima.lucene.first;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FloatField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.itheima.lucene.book.Book;
import com.itheima.lucene.dao.BookDao;
import com.itheima.lucene.dao.BookDaoImpl;

/**
 * Lucene创建索引  入门程序
 * @author lx
 *
 */
public class CreateIndexTest {

	
	//Lucene 创建索引
	@Test
	public void testAdd() throws Exception {
		//1：原始文档   Mysql数据
		//2:获取  Mysql数据      Dao bookDao  = new BookDaoImpl();BookDaoImpl jdbc连接Mysql数据库 查询5条数 
		BookDao bookDao = new BookDaoImpl();
		List<Book> books = bookDao.queryBookList();
		//4：分析文档
//		Analyzer analyzer = new StandardAnalyzer(); 
//		Analyzer analyzer = new SmartChineseAnalyzer(); 
		Analyzer analyzer = new IKAnalyzer(); 
		//5:创建索引到索引库中   
		Directory directory = FSDirectory.open(new File("D:\\temp\\index"));
//		Directory directory = new RAMDirectory();//索引保存在内存中 
		IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);
		IndexWriter indexWriter = new IndexWriter(directory, config);
		
		//5条件
		for (Book book : books) {
			
			//3:创建文档对象 Document 
			Document doc = new Document();
			//将Book对象中5个属性放到5个域中  再将域全放在文档对象中  不分  索引  可保可不保
			Field idField = new StoredField("id",String.valueOf(book.getId()));
			Field nameField = new TextField("name",String.valueOf(book.getName()),Store.YES);
			Field picField = new StoredField("pic",String.valueOf(book.getPic()));
			Field priceField = new FloatField("price",book.getPrice(),Store.YES);
			Field descField = new TextField("desc",String.valueOf(book.getDesc()),Store.YES);
			
			if(book.getId() == 2){
				//加权值
				nameField.setBoost(2f);
				descField.setBoost(2f);
			}
			doc.add(idField);
			doc.add(nameField);
			doc.add(picField);
			doc.add(priceField);
			doc.add(descField);
			
			
			//保存文档到索引库 同时 保存索引到索引库
			indexWriter.addDocument(doc);
		}
		indexWriter.close();
	}
	
	//搜索索引
	@Test
	public void testSearch() throws Exception {
		//读取的流
		Directory directory = FSDirectory.open(new File("D:\\temp\\index"));
		IndexReader indexReader = DirectoryReader.open(directory);
		//搜索
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		//执行搜索
		Query query = new TermQuery(new Term("name","java"));
		
		System.out.println(query);
		TopDocs topDocs = indexSearcher.search(query, 5);
		//文档的ID
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		for (ScoreDoc scoreDoc : scoreDocs) {
			Document doc = indexSearcher.doc(scoreDoc.doc);
			System.out.println("Id:" + doc.get("id"));
			System.out.println("name:" + doc.get("name"));
			System.out.println("price:" + doc.get("price"));
			System.out.println("pic:" + doc.get("pic"));
			System.out.println("desc:" + doc.get("desc"));
		}
		indexReader.close();
	}
	
}
