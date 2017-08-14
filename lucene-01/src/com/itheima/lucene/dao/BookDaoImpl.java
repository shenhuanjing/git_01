package com.itheima.lucene.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.itheima.lucene.book.Book;

/**
 * 原数据
 * @author lx
 *
 */
public class BookDaoImpl implements BookDao {

	
	public List<Book> queryBookList() {
		// 数据库链接
		Connection connection = null;
		// 预编译statement
		PreparedStatement preparedStatement = null;
		// 结果集
		ResultSet resultSet = null;
		// 图书列表
		List<Book> list = new ArrayList<Book>();

		try {
			// 加载数据库驱动
			Class.forName("com.mysql.jdbc.Driver");
			// 连接数据库
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/lucene55", "root", "root");

			// SQL语句
			String sql = "SELECT * FROM book";
			// 创建preparedStatement
			preparedStatement = connection.prepareStatement(sql);
			// 获取结果集
			resultSet = preparedStatement.executeQuery();
			// 结果集解析
			while (resultSet.next()) {
				Book book = new Book();
				book.setId(resultSet.getInt("id"));
				book.setName(resultSet.getString("name"));
				book.setPrice(resultSet.getFloat("price"));
				book.setPic(resultSet.getString("pic"));
				book.setDesc(resultSet.getString("description"));
				
				
				list.add(book);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}

}
