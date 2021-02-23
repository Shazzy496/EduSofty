package com.sharon.edusoft.MyLibrary;

public class UserLikedVideos {
    String video_id;
    private long timestamp;
    String video_user_id;
    String liked_user_id;
    private String video;

    public UserLikedVideos() {
    }

    public UserLikedVideos(String video_id, long timestamp, String video_user_id, String liked_user_id, String video) {
        this.video_id = video_id;
        this.timestamp = timestamp;
        this.video_user_id = video_user_id;
        this.liked_user_id = liked_user_id;
        this.video = video;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getVideo_user_id() {
        return video_user_id;
    }

    public void setVideo_user_id(String video_user_id) {
        this.video_user_id = video_user_id;
    }

    public String getLiked_user_id() {
        return liked_user_id;
    }

    public void setLiked_user_id(String liked_user_id) {
        this.liked_user_id = liked_user_id;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }
}
