package com.sharon.edusoft.Video.VideoComments;

public class VideoReplies {
    private String reply = null;
    private String reply_id= null;
    private long timestamp;
    private String user_id = null;
    private String video_id = null;
    private  String commentId=null;
    private String name=null;
    private String photo= null;



    public VideoReplies() {
    }

    public VideoReplies(String reply, String reply_id, long timestamp, String user_id, String video_id,String commentId, String name, String photo) {
        this.reply = reply;
        this.reply_id = reply_id;
        this.timestamp = timestamp;
        this.user_id = user_id;
        this.video_id = video_id;
        this.commentId=commentId;
        this.name = name;
        this.photo = photo;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getReply_id() {
        return reply_id;
    }

    public void setReply_id(String reply_id) {
        this.reply_id = reply_id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
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

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
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
