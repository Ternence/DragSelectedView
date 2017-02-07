package optimizer.dianxinos.com.library;

public class TreasureConstants extends ModularConstants{

    public static final int INVALID_NUM = -1;
    public static final int COLUMNS_COUNT = 3;
    public static final int TREASURE_POS_BACK_HOME = 1;  //退出百宝箱 item 在tags中位置
    public static final int TREASURE_FILTER_TYPE_REMIND = 1;
    public static final int TREASURE_FILTER_TYPE_NEW = 2;
    public static final int FILTER_HOME_TOP_LEFT = 0;
    public static final int FILTER_HOME_TOP_CENTER = 1;
    public static final int FILTER_HOME_TOP_RIGHT = 2;
    public static final int ORIENTATION_SLIDE_DEFAULT = -1;   //slideView 初始状态
    public static final int ORIENTATION_SLIDE_UP_FORWARD = 1;   //slideView 当前正向上滑
    public static final int ORIENTATION_SLIDE_DOWN_FORWARD = 2;//slideView 当前正向下滑
    public static final int INVALID_ID = -1;
    public static final int TOP_LEFT_VIEW = 0;
    public static final int TOP_CENTER_VIEW = 1;
    public static final int TOP_RIGHT_VIEW = 2;


    public static final String TREASURE_PARAM_ID = "id";
    public static final String TREASURE_PARAM_ARRAY = "beans";

    /**
     * 百宝箱默认beans'ID顺序
     *
     * Deleted BEAN_SAFE_MSG since V8.9.0
     */
    public static final int[] IDS = {BEAN_NET_MOITOR, BEAN_APP_MGR,
            BEAN_ANTIVIRUS, BEAN_ANTIHARASS, BEAN_APP_LOCK,
            BEAN_HOME_TASTE, BEAN_NETFLOW_SHOP, BEAN_STORAGE_CLEAN,
            BEAN_SAFE_TOOLS, BEAN_RECORD, BEAN_WIFI_MGR,
            BEAN_SUPER_MODE, BEAN_NOTIFY_SPAM, BEAN_BONUS_HELPER, BEAN_SAFE_SEARCH,
            BEAN_PERMISSION_MGR, BEAN_TICKET};

    /**
     * 首页功能区红点筛选顺序
     */
    public static final int[] FILTER_SEEDS = {TreasureConstants.FILTER_HOME_TOP_LEFT,
            TreasureConstants.FILTER_HOME_TOP_CENTER, TreasureConstants.FILTER_HOME_TOP_RIGHT};

}
