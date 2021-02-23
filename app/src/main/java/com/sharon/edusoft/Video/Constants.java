package com.sharon.edusoft.Video;

public class Constants {
    public static final String STORAGE_PATH_UPLOADS = "uploads/";
    public static final String DATABASE_PATH_UPLOADS = "uploads";
    public interface ACTION {
        String MAIN_ACTION = "com.sharon.edusoft.action.main";
        String INIT_ACTION = "com.sharon.edusoft.action.init";
        String PREV_ACTION = "com.sharon.edusoft.action.prev";
        String PLAY_ACTION = "com.sharon.edusoft.action.play";
        String NEXT_ACTION = "com.sharon.edusoft.action.next";
        String STARTFOREGROUND_ACTION = "com.sharon.edusoft.action.startforeground";
        String STOPFOREGROUND_ACTION = "com.sharon.edusoft.action.stopforeground";

    }

    public interface NOTIFICATION_ID {
        int FOREGROUND_SERVICE = 101;
    }
}
