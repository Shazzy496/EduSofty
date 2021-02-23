package com.sharon.edusoft.Video.VideoComments;

public class VideoComments {
    private String comment = null;
    private String comment_id = null;
    private long timestamp;
    private String comment_userId=null;
    private String user_id = null;
    private String video_id = null;
    private String name=null;
    private String photo=null;



    VideoComments() {

    }

    public VideoComments(String comment, String comment_id, long timestamp, String comment_userId, String user_id, String video_id, String name, String photo) {
        this.comment = comment;
        this.comment_id = comment_id;
        this.timestamp = timestamp;
        this.comment_userId = comment_userId;
        this.user_id = user_id;
        this.video_id = video_id;
        this.name = name;
        this.photo = photo;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getComment_userId() {
        return comment_userId;
    }

    public void setComment_userId(String comment_userId) {
        this.comment_userId = comment_userId;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}

