package in.rade.armud.armudclient;

/**
 * in.rade.armud.armudclient.Globals.java
 * <p/>
 * Created by Xiaochao Yang on Dec 9, 2011 1:43:35 PM
 */


// More on class on constants:
// http://www.javapractices.com/topic/TopicAction.do?Id=2

public abstract class Globals {

    public static final int ACCELEROMETER_BUFFER_CAPACITY = 2048;
    public static final int ACCELEROMETER_BLOCK_CAPACITY = 64;

    public static final int ACTIVITY_ID_STANDING = 0;
    public static final int ACTIVITY_ID_WALKING = 1;
    public static final int ACTIVITY_ID_RUNNING = 2;
    public static final int COMMAND_ID_ATTACK = 3;
    public static final int COMMAND_ID_PICKUP = 4;
    public static final int COMMAND_ID_THROW = 5;
    public static final int COMMAND_ID_CAST = 6;
    public static final int COMMAND_ID_DROP = 7;
    public static final int NO_COMMAND_DETECTED = 99;

    public static final String SERVICE_TASK_TYPE_KEY = "type";
    public static final int SERVICE_TASK_TYPE_COLLECT = 0;
    public static final int SERVICE_TASK_TYPE_CLASSIFY = 1;

    public static final String ACTION_MOTION_UPDATED = "MYRUNS_MOTION_UPDATED";
    public static final String COMMAND_UPDATED = "MYRUNS_ACTIVITY_UPDATED";

    public static final String CLASS_LABEL_KEY = "label";
    public static final String CLASS_LABEL_STANDING = "Standing";
    public static final String CLASS_LABEL_WALKING = "Walking";
    public static final String CLASS_LABEL_RUNNING = "Running";
    public static final String CLASS_LABEL_OTHER = "others";

    public static final String FEAT_FFT_COEF_LABEL = "fft_coef_";
    public static final String FEAT_MAX_LABEL = "max";
    public static final String FEAT_SET_NAME = "accelerometer_features";

    public static final String FEATURE_FILE_NAME = "features.arff";
    public static final String RAW_DATA_NAME = "raw_data.txt";
    public static final int FEATURE_SET_CAPACITY = 10000;

    public static final int FOCUS_CONTEXT_CHARACTER = 10;
    public static final int FOCUS_CONTEXT_OBJECT = 11;
    public static final int FOCUS_CONTEXT_INVENTORY = 12;
    public static final int FOCUS_CONTEXT_IDLE = 13;

    public static final int NOTIFICATION_ID = 1;

    public static final String ARMUD_DATA_PATH = "/ARMUD_DATA";
    public static final String COMMAND_PATH = "/COMMAND";
}