package com.example.sosmed.posting;

public class Post {

    private String postId;
    private String userId;
    private String content;
    private long timestamp;
    private int likesCount;
    private int commentsCount;

    public Post() {
    }

    public Post(String userId, String content, long timestamp, int likesCount, int commentsCount) {
        this.userId = userId;
        this.content = content;
        this.timestamp = timestamp;
        this.likesCount = likesCount;
        this.commentsCount = commentsCount;
    }

    public Post(String postId, String userId, String content, long timestamp) {
        this.postId = postId;
        this.userId = userId;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public String getContent() {
        return content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }
    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }



    public void setUserId(String userId) {
        this.userId = userId;
    }



    public void setContent(String content) {
        this.content = content;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
