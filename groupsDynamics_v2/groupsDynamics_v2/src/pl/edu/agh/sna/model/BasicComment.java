package pl.edu.agh.sna.model;

import java.util.Date;

public class BasicComment {
	private Integer id;
	private Integer parentCommentId;
	private Integer parentCommentAuthorId;
	private Integer postId;
	private Integer postAuthorId;
	private Integer authorId;
	private Date date;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getPostId() {
		return postId;
	}

	public void setPostId(Integer postId) {
		this.postId = postId;
	}

	public Integer getAuthorId() {
		return authorId;
	}

	public void setAuthorId(Integer authorId) {
		this.authorId = authorId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Integer getParentCommentId() {
		return parentCommentId;
	}

	public void setParentCommentId(Integer parentCommentId) {
		this.parentCommentId = parentCommentId;
	}

	public Integer getParentCommentAuthorId() {
		return parentCommentAuthorId;
	}

	public void setParentCommentAuthorId(Integer parentCommentAuthorId) {
		this.parentCommentAuthorId = parentCommentAuthorId;
	}

	public Integer getPostAuthorId() {
		return postAuthorId;
	}

	public void setPostAuthorId(Integer postAuthorId) {
		this.postAuthorId = postAuthorId;
	}

}
