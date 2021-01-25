package com.sharon.edusoft.Video;

public class Constants {
    public static final String STORAGE_PATH_UPLOADS = "uploads/";
    public static final String DATABASE_PATH_UPLOADS = "uploads";
    public interface ACTION {
        public static String MAIN_ACTION = "com.sharon.edusoft.action.main";
        public static String INIT_ACTION = "com.sharon.edusoft.action.init";
        public static String PREV_ACTION = "com.sharon.edusoft.action.prev";
        public static String PLAY_ACTION = "com.sharon.edusoft.action.play";
        public static String NEXT_ACTION = "com.sharon.edusoft.action.next";
        public static String STARTFOREGROUND_ACTION = "com.sharon.edusoft.action.startforeground";
        public static String STOPFOREGROUND_ACTION = "com.sharon.edusoft.action.stopforeground";

    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }
}
