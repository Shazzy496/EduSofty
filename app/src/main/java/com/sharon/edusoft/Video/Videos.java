package com.sharon.edusoft.Video;

public class Videos {

    private String video_id;
    private String user_id;
    private String video;
    private String videoThumbnail;
    private String videoCategory;
    private String videoTitle;
    private String videoDescription;
    private String channelId;
    private int videoHeight;
    private int videoWidth;
    private long videoDuration;
    private long timestamp;

    Videos() {

    }

    public Videos(String video_id, String user_id, String video, String videoThumbnail, String videoCategory, String videoTitle, String videoDescription, int videoHeight, int videoWidth, long videoDuration, long timestamp, String channelId) {
        this.video_id = video_id;
        this.user_id = user_id;
        this.video = video;
        this.videoThumbnail = videoThumbnail;
        this.videoCategory = videoCategory;
        this.videoTitle = videoTitle;
        this.videoDescription = videoDescription;
        this.videoHeight = videoHeight;
        this.videoWidth = videoWidth;
        this.videoDuration = videoDuration;
        this.timestamp = timestamp;
        this.channelId = channelId;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getVideoThumbnail() {
        return videoThumbnail;
    }

    public void setVideoThumbnail(String videoThumbnail) {
        this.videoThumbnail = videoThumbnail;
    }

    public String getVideoCategory() {
        return videoCategory;
    }

    public void setVideoCategory(String videoCategory) {
        this.videoCategory = videoCategory;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getVideoDescription() {
        return videoDescription;
    }

    public void setVideoDescription(String videoDescription) {
        this.videoDescription = videoDescription;
    }

    public int getVideoHeight() {
        return videoHeight;
    }

    public void setVideoHeight(int videoHeight) {
        this.videoHeight = videoHeight;
    }

    public int getVideoWidth() {
        return videoWidth;
    }

    public void setVideoWidth(int videoWidth) {
        this.videoWidth = videoWidth;
    }

    public long getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(long videoDuration) {
        this.videoDuration = videoDuration;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }
}
