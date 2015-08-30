package pl.edu.agh.sna.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import pl.edu.agh.sna.model.BasicComment;
import pl.edu.agh.sna.model.BasicPost;

public class DbManager {
	private Connection connection;

	public DbManager(Dataset ds) throws Exception {
		Class.forName("org.postgresql.Driver");
		String url = null;

		switch (ds) {
		case HUFFINGTON:
			url = "jdbc:postgresql://localhost/huffington";
			break;
		case SALON24:
			url = "jdbc:postgresql://localhost/salon24db";
			break;
		}
		connection = DriverManager.getConnection(url, "sna_user", "sna_password");
	}

	public List<BasicPost> getBasicPostList(Date startDate, Date endDate) throws Exception {
		long begin = System.currentTimeMillis();
		String statement = "select id,author_id,date from posts where date >= ? and date <= ? ";
		PreparedStatement preparedStatement = connection.prepareStatement(statement);
		preparedStatement.setTimestamp(1, new java.sql.Timestamp(startDate.getTime()));
		preparedStatement.setTimestamp(2, new java.sql.Timestamp(endDate.getTime()));
		ResultSet rs = preparedStatement.executeQuery();
		List<BasicPost> list = new LinkedList<>();
		while (rs.next()) {
			Integer id = rs.getInt(1);
			Integer authorId = rs.getInt(2);
			Date date = rs.getTimestamp(3);

			BasicPost post = new BasicPost();
			post.setId(id);
			post.setAuthorId(authorId);
			post.setDate(date);
			list.add(post);
		}

		long end = System.currentTimeMillis();
		System.out.println("posts: " + (end - begin) / 1000 + " seconds");

		return list;
	}

	public List<BasicComment> getBasicCommentList(Date startDate, Date endDate) throws Exception {
		long begin = System.currentTimeMillis();
		String statement = "select c.id,c.parentcomment_id,c.post_id,c.author_id,c.date,p.author_id,c2.author_id from comments c "
				+ "inner join posts p on p.id=c.post_id left outer join comments c2 on c.parentcomment_id=c2.id where c.date >= ? and c.date <= ? ";
		PreparedStatement preparedStatement = connection.prepareStatement(statement);
		preparedStatement.setTimestamp(1, new java.sql.Timestamp(startDate.getTime()));
		preparedStatement.setTimestamp(2, new java.sql.Timestamp(endDate.getTime()));
		ResultSet rs = preparedStatement.executeQuery();
		List<BasicComment> list = new LinkedList<>();
		while (rs.next()) {
			Integer id = rs.getInt(1);
			Integer parentCommentId = rs.getInt(2);
			if (parentCommentId == 0) {
				parentCommentId = null;
			}
			Integer postId = rs.getInt(3);
			Integer authorId = rs.getInt(4);
			Date date = rs.getTimestamp(5);
			Integer postAuthorId = rs.getInt(6);
			Integer parentCommentAuthorId = rs.getInt(7);
			if (parentCommentAuthorId == 0) {
				parentCommentAuthorId = null;
			}

			BasicComment comment = new BasicComment();
			comment.setId(id);
			comment.setParentCommentId(parentCommentId);
			comment.setPostId(postId);
			comment.setAuthorId(authorId);
			comment.setDate(date);
			comment.setPostAuthorId(postAuthorId);
			comment.setParentCommentAuthorId(parentCommentAuthorId);
			list.add(comment);
		}

		long end = System.currentTimeMillis();
		System.out.println("comments: " + (end - begin) / 1000 + " seconds");

		return list;
	}

	public PreparedStatement getPreparedStatement(String query) {
		try {
			return connection.prepareStatement(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
